use crate::constants::categories::FoodCategory;
use crate::constants::{Allergen, Cuisine, Flag, MealTime};
use axum::response::IntoResponse;
use axum::Extension;
use std::collections::{HashMap, HashSet};
use strum::IntoEnumIterator;

use crate::methods::consts::*;
use crate::methods::{success_resp, ExtDb};
use crate::utils::AppError;

pub async fn alive() -> impl IntoResponse {
    env!("CARGO_PKG_VERSION").to_string()
}

pub async fn cuisines() -> impl IntoResponse {
    success_resp(
        Cuisine::iter()
            .map(|e| e.to_string())
            .collect::<Vec<String>>(),
    )
}

pub async fn meal_times() -> impl IntoResponse {
    success_resp(
        MealTime::iter()
            .map(|e| e.to_string())
            .collect::<Vec<String>>(),
    )
}

pub async fn allergens() -> impl IntoResponse {
    success_resp(
        Allergen::iter()
            .map(|e| e.to_string())
            .collect::<Vec<String>>(),
    )
}

pub async fn flags() -> impl IntoResponse {
    success_resp(Flag::iter().map(|e| e.to_string()).collect::<Vec<String>>())
}

pub async fn categories() -> Result<impl IntoResponse, AppError> {
    let mut map = HashMap::new();
    for cat in FoodCategory::iter() {
        map.insert(cat, cat.sub_cats());
    }
    Ok(success_resp(map))
}

pub async fn flavors(Extension(db): ExtDb) -> Result<impl IntoResponse, AppError> {
    let mut conn = db.acquire().await?;
    let sql = format!("SELECT {COL_FLAVORS} FROM {TABLE_FOOD}");
    let results: Vec<Vec<String>> = sqlx::query_scalar(&sql).fetch_all(&mut conn).await?;
    let flavors: HashSet<String> = results.into_iter().flatten().collect();
    Ok(success_resp(flavors))
}

pub async fn companies(Extension(db): ExtDb) -> Result<impl IntoResponse, AppError> {
    let mut conn = db.acquire().await?;
    let sql = format!("SELECT {COL_COMPANY}, {COL_RANGE} FROM {TABLE_FOOD}");
    let results = sqlx::query_as::<_, (Option<String>, Option<String>)>(&sql)
        .fetch_all(&mut conn)
        .await?;
    let mut result: HashMap<String, HashSet<String>> = HashMap::new();

    results
        .into_iter()
        .filter_map(|(company, range)| company.map(|company| (company, range)))
        .for_each(|(company, range)| {
            let set = result.entry(company).or_default();
            if let Some(range) = range {
                set.insert(range);
            }
        });
    Ok(success_resp(result))
}
