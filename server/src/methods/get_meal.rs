use crate::constants::{Allergen, Flag, PAGE_SIZE};
use crate::errors::*;
use crate::methods::consts::*;
use crate::methods::{error_resp, success_page_resp, success_resp, ExtDb};
use crate::models::db;
use crate::models::network::response;
use crate::utils::AppError;
use axum::extract::{Path, Query};
use axum::response::IntoResponse;
use axum::Extension;
use log::trace;
use serde::Deserialize;
use sqlx::{Postgres, QueryBuilder};
use std::collections::{HashMap, HashSet};
use std::ops::Range;
use std::str::FromStr;
use uuid::Uuid;

const MIN_MAX_CALORIES: u32 = 100;

#[derive(Debug, Deserialize, Default)]
pub struct MealQuery {
    #[serde(default)]
    page: usize,
    /// ILIKE name
    name: Option<String>,
    /// Exact IN
    flavor: Option<String>,
    /// Exact IN
    include_flags: Option<String>,
    /// NOT Exact IN
    exclude_flags: Option<String>,
    /// NOT Exact IN
    allergens: Option<String>,
    min_calories: Option<u32>,
    max_calories: Option<u32>,
    min_carbs: Option<f32>,
    max_carbs: Option<f32>,
    min_fat: Option<f32>,
    max_fat: Option<f32>,
    min_protein: Option<f32>,
    max_protein: Option<f32>,
}

#[derive(Debug)]
pub struct MealSearch {
    page: usize,
    name: Option<String>,
    flavor: Option<String>,
    include_flags: Vec<Flag>,
    exclude_flags: Vec<Flag>,
    allergens: Vec<Allergen>,
    calories: Option<Range<i64>>,
    carbs: Option<Range<f32>>,
    fat: Option<Range<f32>>,
    protein: Option<Range<f32>>,
}

impl MealQuery {
    pub fn into_search(self) -> Result<MealSearch, Vec<ErrorNum>> {
        let mut errors = HashSet::new();

        let mut include_flags = vec![];
        let mut exclude_flags = vec![];
        let mut allergens = vec![];

        if let Some(flags) = self.include_flags.map(|str| {
            str.split(',')
                .map(|s| s.to_string())
                .collect::<Vec<String>>()
        }) {
            for flag in flags {
                if let Ok(flag) = Flag::from_str(&flag) {
                    include_flags.push(flag);
                } else {
                    errors.insert(INVALID_FLAG);
                }
            }
        }

        if let Some(flags) = self.exclude_flags.map(|str| {
            str.split(',')
                .map(|s| s.to_string())
                .collect::<Vec<String>>()
        }) {
            for flag in flags {
                if let Ok(flag) = Flag::from_str(&flag) {
                    exclude_flags.push(flag);
                } else {
                    errors.insert(INVALID_FLAG);
                }
            }
        }

        if let Some(allergies) = self.allergens.map(|str| {
            str.split(',')
                .map(|s| s.to_string())
                .collect::<Vec<String>>()
        }) {
            for allergy in allergies {
                if let Ok(allergy) = Allergen::from_str(&allergy) {
                    allergens.push(allergy);
                } else {
                    errors.insert(INVALID_ALLERGEN);
                }
            }
        }

        let (min, max) = (
            self.min_calories.unwrap_or(0),
            self.max_calories.unwrap_or(99999),
        );
        let calories = if min >= max {
            errors.insert(CALORIES_MAX_TOO_LOW);
            None
        } else if max < MIN_MAX_CALORIES {
            errors.insert(CALORIES_TOO_LOW);
            None
        } else {
            Some(min as i64..max as i64)
        };

        fn extract_min_max(
            min: Option<f32>,
            max: Option<f32>,
            max_err: ErrorNum,
            neg_err: ErrorNum,
            errors: &mut HashSet<ErrorNum>,
        ) -> Option<Range<f32>> {
            let (min, max) = (min.unwrap_or(0.0), max.unwrap_or(99999.9));
            if min >= max {
                errors.insert(max_err);
                None
            } else if min < 0.0 || max < 0.0 {
                errors.insert(neg_err);
                None
            } else {
                Some(min..max)
            }
        }

        let carbs = extract_min_max(
            self.min_carbs,
            self.max_carbs,
            CARBS_MAX_TOO_LOW,
            CARBS_NEGATIVE,
            &mut errors,
        );
        let fat = extract_min_max(
            self.min_fat,
            self.max_fat,
            FAT_MAX_TOO_LOW,
            FAT_NEGATIVE,
            &mut errors,
        );
        let protein = extract_min_max(
            self.min_protein,
            self.max_protein,
            PROTEIN_MAX_TOO_LOW,
            PROTEIN_NEGATIVE,
            &mut errors,
        );

        if errors.is_empty() {
            Ok(MealSearch {
                page: self.page,
                name: self.name,
                flavor: self.flavor,
                include_flags,
                exclude_flags,
                allergens,
                calories,
                carbs,
                fat,
                protein,
            })
        } else {
            Err(errors.into_iter().collect())
        }
    }
}

impl MealSearch {
    pub fn process_query(self, lifeline_placeholder: &str) -> QueryBuilder<Postgres> {
        let mut builder = QueryBuilder::new(format!(
            "{}SELECT * FROM {TABLE_MEAL}",
            lifeline_placeholder
        ));

        builder.push(" WHERE hidden = false ");

        if let Some(name) = self.name {
            builder.push(format!(" AND {COL_NAME} ILIKE "));
            builder.push_bind(format!("%{}%", name));
        }

        if let Some(flavor) = self.flavor {
            builder.push(" AND ");
            builder.push_bind(flavor);
            builder.push(format!(" = ANY({COL_FLAVORS}) "));
        }

        if !self.include_flags.is_empty() {
            builder.push(format!(" AND {COL_FLAGS} @> ARRAY["));
            let mut sep = builder.separated(",");
            for flag in self.include_flags {
                sep.push_bind(flag);
            }
            sep.push_unseparated("]::text[] ");
        }

        if !self.exclude_flags.is_empty() {
            builder.push(format!(" AND NOT ({COL_FLAGS} && ARRAY["));
            let mut sep = builder.separated(",");
            for flag in self.exclude_flags {
                sep.push_bind(flag);
            }
            sep.push_unseparated("]::text[]) ");
        }

        if !self.allergens.is_empty() {
            builder.push(format!(" AND NOT ({COL_ALLERGENS} && ARRAY["));
            let mut sep = builder.separated(",");
            for allergy in self.allergens {
                sep.push_bind(allergy);
            }
            sep.push_unseparated("]::text[]) ");
        }

        if let Some(cals) = self.calories {
            builder.push(format!(" AND {COL_CALORIES_TOTAL} >= "));
            builder.push_bind(cals.start);
            builder.push(format!(" AND {COL_CALORIES_TOTAL} <= "));
            builder.push_bind(cals.end);
        }

        if let Some(carbs) = self.carbs {
            builder.push(format!(" AND {COL_CARBS_TOTAL} >= "));
            builder.push_bind(carbs.start);
            builder.push(format!(" AND {COL_CARBS_TOTAL} <= "));
            builder.push_bind(carbs.end);
        }

        if let Some(fat) = self.fat {
            builder.push(format!(" AND {COL_FAT_TOTAL} >= "));
            builder.push_bind(fat.start);
            builder.push(format!(" AND {COL_FAT_TOTAL} <= "));
            builder.push_bind(fat.end);
        }

        if let Some(protein) = self.protein {
            builder.push(format!(" AND {COL_PROTEIN_TOTAL} >= "));
            builder.push_bind(protein.start);
            builder.push(format!(" AND {COL_PROTEIN_TOTAL} <= "));
            builder.push_bind(protein.end);
        }

        let offset = self.page * PAGE_SIZE;
        builder.push(format!(
            " ORDER BY {COL_NAME} OFFSET {offset} LIMIT {PAGE_SIZE}"
        ));

        builder
    }
}

fn make_ingredient_query<'a>(
    lifeline_placeholder: &'a str,
    ingredients: &'a HashSet<&'a Uuid>,
) -> QueryBuilder<'a, Postgres> {
    let mut builder = QueryBuilder::new(format!(
        "{}SELECT {COL_ID}, {COL_NAME}, {COL_COMPANY}, {COL_FLAGS} FROM {TABLE_FOOD}",
        lifeline_placeholder
    ));

    builder.push(format!(" WHERE {COL_ID} IN ("));
    let mut sep = builder.separated(",");
    for id in ingredients {
        sep.push_bind(id);
    }
    sep.push_unseparated(") ");

    builder
}

pub async fn meal(
    Query(params): Query<MealQuery>,
    Extension(db): ExtDb,
) -> Result<impl IntoResponse, AppError> {
    let result = params.into_search();
    if let Err(nums) = result {
        return Ok(error_resp(nums));
    }
    let search = result.unwrap();
    trace!("Searching with {:?}", search);
    let page = search.page;
    let mut conn = db.acquire().await?;
    //querybuilder requires something for its lifetime, so use empty string
    let lifeline_placeholder = "";
    let result: Vec<db::Meal> = search
        .process_query(lifeline_placeholder)
        .build_query_as::<db::Meal>()
        .fetch_all(&mut conn)
        .await?;
    // let result: Vec<db::Meal> = query;
    let ingredients = result
        .iter()
        .flat_map(|meal| &meal.food_ids)
        .collect::<HashSet<&Uuid>>();
    let ingredients_result = make_ingredient_query(lifeline_placeholder, &ingredients)
        .build_query_as::<(Uuid, String, Option<String>, Vec<String>)>()
        .fetch_all(&mut conn)
        .await?;
    let ingredients: HashMap<Uuid, (String, Option<String>, Vec<String>)> = ingredients_result
        .into_iter()
        .map(|(id, name, company, flags)| (id, (name, company, flags)))
        .collect();

    let mut output: Vec<response::Meal> = vec![];
    for item in result {
        if let Ok(food) = item.into_response(&ingredients) {
            output.push(food);
        }
    }
    Ok(success_page_resp(page, output))
}

pub async fn exact_meal(
    Path(id): Path<Uuid>,
    Extension(db): ExtDb,
) -> Result<impl IntoResponse, AppError> {
    let mut conn = db.acquire().await?;
    let meal: Option<db::Meal> = sqlx::query_as(&format!(
        "SELECT * FROM {TABLE_MEAL} WHERE {COL_ID} = $1 AND hidden = false"
    ))
    .bind(id)
    .fetch_optional(&mut conn)
    .await?;
    if let Some(meal) = meal {
        let lifeline_placeholder = "";
        let ingredients_result =
            make_ingredient_query(lifeline_placeholder, &meal.food_ids.iter().collect())
                .build_query_as::<(Uuid, String, Option<String>, Vec<String>)>()
                .fetch_all(&mut conn)
                .await?;
        let ingredients: HashMap<Uuid, (String, Option<String>, Vec<String>)> = ingredients_result
            .into_iter()
            .map(|(id, name, company, flags)| (id, (name, company, flags)))
            .collect();
        if let Ok(meal) = meal.into_response(&ingredients) {
            Ok(success_resp(meal))
        } else {
            Ok(error_resp(vec![INVALID_ID]))
        }
    } else {
        Ok(error_resp(vec![INVALID_ID]))
    }
}
