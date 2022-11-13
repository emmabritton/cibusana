use crate::errors::{EMAIL_ALREADY_TAKEN, INVALID_LOGIN, SERVER_ERROR};
use crate::methods::consts::*;
use crate::methods::{error_resp, success_resp, ExtDb, get_user_id};
use crate::models::network::{request, response};
use crate::utils::{encrypt, verify, AppError};
use actix_web::web::Json;
use actix_web::{HttpRequest, Responder};
use log::error;
use uuid::Uuid;
use crate::models::db;

pub async fn register(
    Json(payload): Json<request::Register>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
    let errors = payload.validate();
    if !errors.is_empty() {
        return Ok(error_resp(errors.clone()));
    }
    let pass_result = encrypt(&payload.password);
    if pass_result.is_err() {
        error!(
            "{}",
            pass_result.err().unwrap_or_else(|| String::from(
                "Unknown error with no error body when encrypting"
            ))
        );
        return Ok(error_resp(vec![SERVER_ERROR]));
    }
    let user = db::User {
        id: Uuid::new_v4(),
        name: payload.name,
        email: payload.email,
        password: pass_result.unwrap(),
    };
    let data = db::UserData {
        user_id: user.id,
        preferred_unit: payload.units,
        default_measurement_names: vec![],
        target_weight_grams: payload.target_weight_grams.map(|value| value as i32),
        target_weight_date: payload.target_weight_date,
        height: payload.height as i32,
    };
    let mut trans = db.begin().await?;
    let row = sqlx::query(&format!(
        "SELECT {COL_ID} FROM {TABLE_USERS} WHERE {COL_EMAIL} = $1"
    ))
        .bind(user.email.clone())
        .fetch_optional(&mut trans)
        .await?;
    if row.is_some() {
        Ok(error_resp(vec![EMAIL_ALREADY_TAKEN]))
    } else {
        sqlx::query(&format!("INSERT INTO {TABLE_USERS} ({COL_ID}, {COL_NAME}, {COL_EMAIL}, {COL_PASS}) VALUES ($1,$2,$3,$4)"))
            .bind(user.id)
            .bind(user.name)
            .bind(user.email)
            .bind(user.password)
            .execute(&mut trans)
            .await?;

        sqlx::query(&format!("INSERT INTO {TABLE_USER_DATA} ({COL_USER_ID}, {COL_HEIGHT}, {COL_MEASUREMENT_NAMES}, {COL_UNITS}, {COL_TARGET_WEIGHT}, {COL_TARGET_DATE}) VALUES ($1,$2,$3,$4,$5,$6)"))
            .bind(data.user_id)
            .bind(data.height)
            .bind(data.default_measurement_names)
            .bind(data.preferred_unit)
            .bind(data.target_weight_grams)
            .bind(data.target_weight_date)
            .execute(&mut trans)
            .await?;

        let uuid = sqlx::query_scalar(&format!(
            "INSERT INTO {TABLE_USER_TOKENS} ({COL_USER_ID}) VALUES ($1) RETURNING {COL_TOKEN}"
        ))
            .bind(user.id)
            .fetch_one(&mut trans)
            .await?;

        trans.commit().await?;
        Ok(success_resp(response::Register::new(uuid)))
    }
}

pub async fn login(payload: Json<request::Login>, db: ExtDb) -> Result<impl Responder, AppError> {
    let errors = payload.validate();
    if !errors.is_empty() {
        return Ok(error_resp(errors.clone()));
    }
    let mut conn = db.acquire().await?;
    let user: Option<(String, Uuid, String)> = sqlx::query_as(&format!(
        "SELECT {COL_NAME}, {COL_ID}, {COL_PASS} FROM {TABLE_USERS} WHERE {COL_EMAIL} = $1"
    ))
        .bind(&payload.email)
        .fetch_optional(&mut conn)
        .await?;
    if let Some(user) = user {
        if verify(&payload.password, &user.2) {
            let uuid = sqlx::query_scalar(&format!(
                "INSERT INTO {TABLE_USER_TOKENS} ({COL_USER_ID}) VALUES ($1) RETURNING {COL_TOKEN}"
            ))
                .bind(user.1)
                .fetch_one(&mut conn)
                .await?;
            Ok(success_resp(response::Login::new(uuid, user.0)))
        } else {
            Ok(error_resp(vec![INVALID_LOGIN]))
        }
    } else {
        //TODO add verify call to help prevent timing attacks
        Ok(error_resp(vec![INVALID_LOGIN]))
    }
}

pub async fn set_data(request: HttpRequest, payload: Json<request::UserData>, db: ExtDb) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num])),
    };

    let db = payload.to_db(user_id);

    sqlx::query(&format!("UPDATE {TABLE_USER_DATA} SET {COL_HEIGHT} = $2, {COL_MEASUREMENT_NAMES} = $3, {COL_UNITS} = $4, {COL_TARGET_WEIGHT} = $5, {COL_TARGET_DATE} = $6 WHERE {COL_USER_ID} = $1"))
        .bind(db.user_id)
        .bind(db.height)
        .bind(db.default_measurement_names)
        .bind(db.preferred_unit)
        .bind(db.target_weight_grams)
        .bind(db.target_weight_date)
        .execute(&mut conn)
        .await?;

    Ok(success_resp(0))
}

pub async fn get_data(request: HttpRequest, db: ExtDb) -> Result<impl Responder, AppError> {
    let mut conn = db.acquire().await?;

    let user_id = match get_user_id(request.headers(), &mut conn).await {
        Ok(uuid) => uuid,
        Err(num) => return Ok(error_resp(vec![num])),
    };

    let result: db::UserData  = sqlx::query_as(&format!("SELECT * FROM {TABLE_USER_DATA} WHERE {COL_USER_ID} = $1"))
        .bind(user_id)
        .fetch_one(&mut conn)
        .await?;

    Ok(success_resp(result.into_response()))
}
