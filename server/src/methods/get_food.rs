use crate::constants::categories::{FoodCategory, FoodSubCategory};
use crate::constants::{Allergen, Flag, PAGE_SIZE};
use crate::errors::*;
use crate::methods::consts::*;
use crate::methods::{error_resp, success_page_resp, success_resp, ExtDb};
use crate::models::db;
use crate::models::network::response;
use crate::utils::AppError;
use log::trace;
use serde::Deserialize;
use sqlx::{Postgres, QueryBuilder};
use std::collections::HashSet;
use std::ops::Range;
use std::str::FromStr;
use actix_web::Responder;
use actix_web::web::{Path, Query};
use uuid::Uuid;

const MIN_MAX_CALORIES: u32 = 100;

#[derive(Debug, Deserialize, Default)]
pub struct FoodQuery {
    #[serde(default)]
    page: usize,
    /// Exact
    company: Option<String>,
    /// Exact
    range: Option<String>,
    /// Exact
    category: Option<String>,
    /// Exact
    subcategory: Option<String>,
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
pub struct FoodSearch {
    page: usize,
    company: Option<String>,
    range: Option<String>,
    category: Option<FoodCategory>,
    subcategory: Option<FoodSubCategory>,
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

impl FoodQuery {
    pub fn into_search(&self) -> Result<FoodSearch, Vec<ErrorNum>> {
        let mut errors = HashSet::new();

        let cat = if let Some(text) = &self.category {
            if let Ok(cat) = FoodCategory::from_str(text) {
                Some(cat)
            } else {
                errors.insert(CATEGORY_INVALID);
                None
            }
        } else {
            None
        };

        let subcat = if let Some(text) = &self.subcategory {
            if let Ok(subcat) = FoodSubCategory::from_str(&text) {
                if self.category.is_none() {
                    errors.insert(INVALID_FILTER_CATEGORY);
                    None
                } else {
                    Some(subcat)
                }
            } else {
                errors.insert(SUBCATEGORY_INVALID);
                None
            }
        } else {
            None
        };

        let mut include_flags = vec![];
        let mut exclude_flags = vec![];
        let mut allergens = vec![];

        if let Some(flags) = self.include_flags.as_ref().map(|str| {
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

        if let Some(flags) = self.exclude_flags.as_ref().map(|str| {
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

        if let Some(allergies) = self.allergens.as_ref().map(|str| {
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
            Ok(FoodSearch {
                page: self.page.clone(),
                company: self.company.clone(),
                range: self.range.clone(),
                category: cat,
                subcategory: subcat,
                name: self.name.clone(),
                flavor: self.flavor.clone(),
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

impl FoodSearch {
    pub fn process_query(&self, lifeline_placeholder: &str) -> QueryBuilder<Postgres> {
        let mut builder = QueryBuilder::new(format!(
            "{}SELECT * FROM {TABLE_FOOD}",
            lifeline_placeholder
        ));

        builder.push(" WHERE hidden = false ");

        if let Some(name) = &self.name {
            builder.push(format!(" AND {COL_NAME} ILIKE "));
            builder.push_bind(format!("%{}%", name));
        }

        if let Some(company) = &self.company {
            builder.push(format!(" AND {COL_COMPANY} = "));
            builder.push_bind(company);
        }

        if let Some(range) = &self.range {
            builder.push(format!(" AND {COL_RANGE} = "));
            builder.push_bind(range);
        }

        if let Some(category) = self.category {
            builder.push(format!(" AND {COL_CATEGORY} = "));
            builder.push_bind(category.to_string());
        }

        if let Some(subcategory) = self.subcategory {
            builder.push(format!(" AND {COL_SUBCATEGORY} = "));
            builder.push_bind(subcategory.to_string());
        }

        if let Some(flavor) = &self.flavor {
            builder.push(" AND ");
            builder.push_bind(flavor);
            builder.push(format!(" = ANY({COL_FLAVORS}) "));
        }

        if !self.include_flags.is_empty() {
            builder.push(format!(" AND {COL_FLAGS} @> ARRAY["));
            let mut sep = builder.separated(",");
            for flag in &self.include_flags {
                sep.push_bind(flag);
            }
            sep.push_unseparated("]::text[] ");
        }

        if !self.exclude_flags.is_empty() {
            builder.push(format!(" AND NOT ({COL_FLAGS} && ARRAY["));
            let mut sep = builder.separated(",");
            for flag in &self.exclude_flags {
                sep.push_bind(flag);
            }
            sep.push_unseparated("]::text[]) ");
        }

        if !self.allergens.is_empty() {
            builder.push(format!(" AND NOT ({COL_ALLERGENS} && ARRAY["));
            let mut sep = builder.separated(",");
            for allergy in &self.allergens {
                sep.push_bind(allergy);
            }
            sep.push_unseparated("]::text[]) ");
        }

        if let Some(cals) = &self.calories {
            builder.push(format!(" AND {COL_CALORIES_P100} >= "));
            builder.push_bind(cals.start);
            builder.push(format!(" AND {COL_CALORIES_P100} <= "));
            builder.push_bind(cals.end);
        }

        if let Some(carbs) = &self.carbs {
            builder.push(format!(" AND {COL_CARBS_P100} >= "));
            builder.push_bind(carbs.start);
            builder.push(format!(" AND {COL_CARBS_P100} <= "));
            builder.push_bind(carbs.end);
        }

        if let Some(fat) = &self.fat {
            builder.push(format!(" AND {COL_FAT_P100} >= "));
            builder.push_bind(fat.start);
            builder.push(format!(" AND {COL_FAT_P100} <= "));
            builder.push_bind(fat.end);
        }

        if let Some(protein) = &self.protein {
            builder.push(format!(" AND {COL_PROTEIN_P100} >= "));
            builder.push_bind(protein.start);
            builder.push(format!(" AND {COL_PROTEIN_P100} <= "));
            builder.push_bind(protein.end);
        }

        let offset = self.page * PAGE_SIZE;
        builder.push(format!(
            " ORDER BY {COL_NAME} OFFSET {offset} LIMIT {PAGE_SIZE}"
        ));

        builder
    }
}

pub async fn exact_food(
    id: Path<Uuid>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;
    let food: Option<db::Food> = sqlx::query_as(&format!(
        "SELECT * FROM {TABLE_FOOD} WHERE {COL_ID} = $1 AND hidden = false"
    ))
    .bind(id.into_inner())
    .fetch_optional(&mut conn)
    .await?;
    if let Some(food) = food {
        if let Ok(food) = food.try_into() {
            Ok(success_resp::<response::Food>(food))
        } else {
            Ok(error_resp(vec![INVALID_ID]))
        }
    } else {
        Ok(error_resp(vec![INVALID_ID]))
    }
}

pub async fn food(
    params: Query<FoodQuery>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let result = &params.into_search();
    if let Err(nums) = result {
        return Ok(error_resp(nums.clone()));
    }
    let search = result.as_ref().unwrap();
    trace!("Searching with {:?}", search);
    let page = search.page;
    let mut conn = db.acquire().await?;
    //querybuilder requires something for its lifetime, so use empty string
    let lifeline_placeholder = "";
    let builder = &mut search.process_query(lifeline_placeholder);
    let query = builder.build_query_as::<db::Food>();
    let result: Vec<db::Food> = query.fetch_all(&mut conn).await?;
    let mut output: Vec<response::Food> = vec![];
    for item in result {
        if let Ok(food) = item.try_into() {
            output.push(food);
        }
    }
    Ok(success_page_resp(page, output))
}

// pub async fn submit_food(
//     Json(payload): Json<RequestWrapper<request::Food>>,
//     Extension(db): ExtDb,
// ) -> Result<impl IntoResponse, AppError> {
//     let result = payload.validate();
//     if result.is_err() {
//         return Ok(error_resp(result.unwrap_err()));
//     }
//     let (token, content) = result.unwrap();
//
//     let mut conn = db.acquire().await?;
//
//     match is_valid_user(&mut conn, token.clone()).await {
//         Ok(is_valid) => if !is_valid {
//             return Ok(error_resp(vec![INVALID_USER]));
//         }
//         Err(err) => {
//             error!("{}", err);
//             return Ok(error_resp(vec![SERVER_ERROR]));
//         }
//     }
//
//     //check if token is valid
//     match content.try_into_db(token) {
//         Err(errors) => return Ok(error_resp(errors.clone())),
//         Ok(food) => {
//
//         }
//     }
// }
