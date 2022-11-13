use crate::methods::*;
use crate::methods::{error_resp, get_user_id, success_resp, ExtDb};
use crate::models::db;
use crate::models::network::{request, response};
use crate::utils::AppError;
use actix_web::web::{Json, Path, Query};
use actix_web::{HttpRequest, Responder};
use chrono::{DateTime, Utc};
use serde::Deserialize;

#[derive(Debug, Deserialize, Default)]
pub struct MeasureQuery {
    pub start: DateTime<Utc>,
    pub end: DateTime<Utc>,
}

pub async fn get_measures(
    request: HttpRequest,
    params: Query<MeasureQuery>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num])),
    };

    let results: Vec<db::Measurement> = sqlx::query_as(&format!(
        "SELECT * FROM {TABLE_MEASURE} WHERE {COL_USER_ID} = $1 AND {COL_DATE} >= $2 AND {COL_DATE} <= $3 ORDER BY {COL_DATE}"
    ))
        .bind(user_id)
        .bind(params.start)
        .bind(params.end)
        .fetch_all(&mut conn)
        .await?;

    let map: Vec<response::Measurement> =
        results.into_iter().map(|db| db.into_response()).collect();

    Ok(success_resp(map))
}

pub async fn last_measure(request: HttpRequest, db: ExtDb) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num])),
    };

    let result: Option<db::Measurement> = sqlx::query_as(&format!(
        "SELECT * FROM {TABLE_MEASURE} WHERE {COL_USER_ID} = $1 ORDER BY {COL_DATE} DESC LIMIT 1"
    ))
    .bind(user_id)
    .fetch_optional(&mut conn)
    .await?;

    let measurement = result.map(|result| result.into_response());

    Ok(success_resp(measurement))
}

pub async fn first_measure(request: HttpRequest, db: ExtDb) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num])),
    };

    let result: Option<db::Measurement> = sqlx::query_as(&format!(
        "SELECT * FROM {TABLE_MEASURE} WHERE {COL_USER_ID} = $1 ORDER BY {COL_DATE} ASC LIMIT 1"
    ))
    .bind(user_id)
    .fetch_optional(&mut conn)
    .await?;

    let measurement = result.map(|result| result.into_response());

    Ok(success_resp(measurement))
}

pub async fn delete_measure(
    request: HttpRequest,
    date: Path<DateTime<Utc>>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num])),
    };

    sqlx::query(&format!(
        "DELETE FROM {TABLE_MEASURE} WHERE {COL_USER_ID} = $1 AND {COL_DATE} = $2"
    ))
    .bind(user_id)
    .bind(date.into_inner())
    .execute(&mut conn)
    .await?;

    Ok(success_resp(0))
}

pub async fn set_measure(
    request: HttpRequest,
    body: Json<request::Measurement>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num])),
    };

    let measure = body.to_db(user_id);

    sqlx::query(&format!("INSERT INTO {TABLE_MEASURE} ({COL_USER_ID},{COL_DATE},{COL_M_NAMES},{COL_M_VALUES}) VALUES ($1,$2,$3,$4)"))
        .bind(user_id)
        .bind(measure.date)
        .bind(measure.m_names)
        .bind(measure.m_values)
        .execute(&mut conn)
        .await?;

    Ok(success_resp(0))
}
