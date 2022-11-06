use std::collections::HashMap;
use actix_web::{HttpRequest, Responder};
use actix_web::web::{Json, Query};
use chrono::NaiveDateTime;
use serde::Deserialize;
use crate::methods::consts::*;
use crate::methods::{error_resp, ExtDb, get_user_id, success_resp};
use crate::models::network::request;
use crate::utils::AppError;

#[derive(Debug, Deserialize, Default)]
pub struct WeightQuery {
    pub start: NaiveDateTime,
    pub end: NaiveDateTime,
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

    let results: Vec<(f32, NaiveDateTime)> = sqlx::query_as(&format!(
        "SELECT {COL_KGS}, {COL_DATE} FROM {TABLE_WEIGHT} WHERE {COL_USER_ID} = $1 AND {COL_DATE} >= $2 AND {COL_DATE} <= $3 ORDER BY {COL_DATE}"
    ))
        .bind(user_id)
        .bind(params.start)
        .bind(params.end)
        .fetch_all(&mut conn)
        .await?;

    let map : HashMap<NaiveDateTime, f32> = results.into_iter()
        .map(|(kgs, date)| (date, kgs))
        .collect();

    Ok(success_resp(map))
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