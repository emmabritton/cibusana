use actix_web::{HttpRequest, Responder};
use actix_web::web::{Json, Query};
use chrono::{DateTime, Utc};
use serde::Deserialize;
use crate::methods::consts::*;
use crate::methods::{error_resp, ExtDb, get_user_id, success_resp};
use crate::models::network::{request, response};
use crate::utils::AppError;

#[derive(Debug, Deserialize, Default)]
pub struct WeightQuery {
    pub start: DateTime<Utc>,
    pub end: DateTime<Utc>,
}

pub async fn get_weights(
    request: HttpRequest,
    params: Query<WeightQuery>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num]))
    };

    let results: Vec<(f32, DateTime<Utc>)> = sqlx::query_as(&format!(
        "SELECT {COL_KGS}, {COL_DATE} FROM {TABLE_WEIGHT} WHERE {COL_USER_ID} = $1 AND {COL_DATE} >= $2 AND {COL_DATE} <= $3 ORDER BY {COL_DATE}"
    ))
        .bind(user_id)
        .bind(params.start)
        .bind(params.end)
        .fetch_all(&mut conn)
        .await?;

    let map: Vec<response::Weight> = results.into_iter()
        .map(|(kgs, date)| response::Weight {kgs, date})
        .collect();

    Ok(success_resp(map))
}

pub async fn last_weight(
    request: HttpRequest,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num]))
    };

    let results: Option<(f32, DateTime<Utc>)> = sqlx::query_as(&format!(
        "SELECT {COL_KGS}, {COL_DATE} FROM {TABLE_WEIGHT} WHERE {COL_USER_ID} = $1 ORDER BY {COL_DATE} DESC LIMIT 1"
    ))
        .bind(user_id)
        .fetch_optional(&mut conn)
        .await?;

    let reading = results.map(|(kgs, date)| response::Weight { kgs, date });

    Ok(success_resp(reading))
}

pub async fn first_weight(
    request: HttpRequest,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num]))
    };

    let results: Option<(f32, DateTime<Utc>)> = sqlx::query_as(&format!(
        "SELECT {COL_KGS}, {COL_DATE} FROM {TABLE_WEIGHT} WHERE {COL_USER_ID} = $1 ORDER BY {COL_DATE} ASC LIMIT 1"
    ))
        .bind(user_id)
        .fetch_optional(&mut conn)
        .await?;

    let reading = results.map(|(kgs, date)| response::Weight { kgs, date });

    Ok(success_resp(reading))
}

pub async fn set_weight(
    request: HttpRequest,
    body: Json<request::Weight>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num]))
    };

    sqlx::query(&format!("INSERT INTO {TABLE_WEIGHT} ({COL_USER_ID},{COL_KGS},{COL_DATE}) VALUES ($1,$2,$3)"))
        .bind(user_id)
        .bind(body.amount)
        .bind(body.date)
        .execute(&mut conn)
        .await?;

    Ok(success_resp(0))
}