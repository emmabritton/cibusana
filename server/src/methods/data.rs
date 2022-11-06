use crate::constants::categories::FoodCategory;
use crate::constants::{Allergen, Cuisine, Flag, MealTime};
use std::collections::{HashMap, HashSet};
use actix_web::Responder;
use strum::IntoEnumIterator;

use crate::methods::consts::*;
use crate::methods::{ExtDb, success_cached_resp};
use crate::utils::AppError;

pub async fn alive() -> impl Responder {
    env!("CARGO_PKG_VERSION").to_string()
}

pub async fn cuisines() -> impl Responder {
    success_cached_resp(
        Cuisine::iter()
            .map(|e| e.to_string())
            .collect::<Vec<String>>(),
    )
}

pub async fn meal_times() -> impl Responder {
    success_cached_resp(
        MealTime::iter()
            .map(|e| e.to_string())
            .collect::<Vec<String>>(),
    )
}

pub async fn allergens() -> impl Responder {
    success_cached_resp(
        Allergen::iter()
            .map(|e| e.to_string())
            .collect::<Vec<String>>(),
    )
}

pub async fn flags() -> impl Responder {
    success_cached_resp(Flag::iter().map(|e| e.to_string()).collect::<Vec<String>>())
}

pub async fn categories() -> Result<impl Responder, AppError> {
    let mut map = HashMap::new();
    for cat in FoodCategory::iter() {
        map.insert(cat, cat.sub_cats());
    }
    Ok(success_cached_resp(map))
}

pub async fn flavors(db: ExtDb) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;
    let sql = format!("SELECT {COL_FLAVORS} FROM {TABLE_FOOD}");
    let results: Vec<Vec<String>> = sqlx::query_scalar(&sql).fetch_all(&mut conn).await?;
    let flavors: HashSet<String> = results.into_iter().flatten().collect();
    Ok(success_cached_resp(flavors))
}

pub async fn companies(db: ExtDb) -> Result<impl Responder, AppError> {
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
    Ok(success_cached_resp(result))
}
