use actix_web::{HttpRequest, Responder};
use actix_web::web::{Json, Path, Query};
use chrono::{DateTime, Utc};
use serde::Deserialize;
use crate::methods::{error_resp, ExtDb, get_user_id, success_resp};
use crate::models::db;
use crate::utils::AppError;
use crate::methods::*;
use crate::models::network::{request, response};

#[derive(Debug, Deserialize, Default)]
pub struct EntryQuery {
    pub start: DateTime<Utc>,
    pub end: DateTime<Utc>,
}

pub async fn get_entries(
    request: HttpRequest,
    params: Query<EntryQuery>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num]))
    };

    let results: Vec<db::MealEntry> = sqlx::query_as(&format!(
        "SELECT * FROM {TABLE_MEAL_ENTRY} WHERE {COL_USER_ID} = $1 AND {COL_DATE} >= $2 AND {COL_DATE} <= $3 ORDER BY {COL_DATE}"
    ))
        .bind(user_id)
        .bind(params.start)
        .bind(params.end)
        .fetch_all(&mut conn)
        .await?;

    let mut entries = vec![];

    for result in results {
        if let Ok(entry) = result.into_response() {
            entries.push(entry);
        }
    }

    Ok(success_resp(entries))
}

pub async fn first_entry(
    request: HttpRequest,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num]))
    };

    let result: Option<db::MealEntry> = sqlx::query_as(&format!(
        "SELECT * FROM {TABLE_MEAL_ENTRY} WHERE {COL_USER_ID} = $1 ORDER BY {COL_DATE} ASC LIMIT 1"
    ))
        .bind(user_id)
        .fetch_optional(&mut conn)
        .await?;

    match result.map(|db| db.into_response()) {
        Some(result) => match result {
            Ok(entry) => Ok(success_resp(entry)),
            Err(err) => Ok(error_resp(vec![err]))
        }
        None => Ok(success_resp(None::<response::MealEntry>))
    }
}

pub async fn last_entry(
    request: HttpRequest,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num]))
    };

    let result: Option<db::MealEntry> = sqlx::query_as(&format!(
        "SELECT * FROM {TABLE_MEAL_ENTRY} WHERE {COL_USER_ID} = $1 ORDER BY {COL_DATE} DESC LIMIT 1"
    ))
        .bind(user_id)
        .fetch_optional(&mut conn)
        .await?;

    match result.map(|db| db.into_response()) {
        Some(result) => match result {
            Ok(entry) => Ok(success_resp(entry)),
            Err(err) => Ok(error_resp(vec![err]))
        }
        None => Ok(success_resp(None::<response::MealEntry>))
    }
}

pub async fn add_entry(
    request: HttpRequest,
    body: Json<request::MealEntry>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num]))
    };

    sqlx::query(&format!("INSERT INTO {TABLE_MEAL_ENTRY} ({COL_USER_ID},{COL_ENTRY_ID},{COL_IS_MEAL},{COL_DATE},{COL_AMOUNT},{COL_CALORIES},{COL_MEAL_TIME}) VALUES ($1,$2,$3,$4,$5,$6,$7)"))
        .bind(user_id)
        .bind(body.food_id)
        .bind(body.is_meal)
        .bind(body.date)
        .bind(body.grams as i32)
        .bind(body.calories as i32)
        .bind(body.meal_time.to_string())
        .execute(&mut conn)
        .await?;

    Ok(success_resp(0))
}

pub async fn delete_entry(
    request: HttpRequest,
    id: Path<u64>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num]))
    };

    sqlx::query(&format!("DELETE FROM {TABLE_MEAL_ENTRY} WHERE {COL_USER_ID} = $1 AND {COL_ID} = $2"))
        .bind(user_id)
        .bind(id.into_inner() as i64)
        .execute(&mut conn)
        .await?;

    Ok(success_resp(0))
}
