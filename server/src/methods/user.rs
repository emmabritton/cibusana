use actix_web::Responder;
use actix_web::web::Json;
use crate::errors::{EMAIL_ALREADY_TAKEN, INVALID_LOGIN, SERVER_ERROR};
use crate::methods::consts::*;
use crate::methods::{error_resp, success_resp, ExtDb};
use crate::models::db::User;
use crate::models::network::{request, response};
use crate::utils::{encrypt, verify, AppError};
use log::error;
use uuid::Uuid;

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
    let user = User {
        id: Uuid::new_v4(),
        name: payload.name,
        email: payload.email,
        password: pass_result.unwrap(),
    };
    let mut conn = db.acquire().await?;
    let row = sqlx::query(&format!(
        "SELECT {COL_ID} FROM {TABLE_USERS} WHERE {COL_EMAIL} = $1"
    ))
        .bind(user.email.clone())
        .fetch_optional(&mut conn)
        .await?;
    if row.is_some() {
        Ok(error_resp(vec![EMAIL_ALREADY_TAKEN]))
    } else {
        sqlx::query(&format!("INSERT INTO {TABLE_USERS} ({COL_ID}, {COL_NAME}, {COL_EMAIL}, {COL_PASS}) VALUES ($1,$2,$3,$4)"))
            .bind(user.id)
            .bind(user.name)
            .bind(user.email)
            .bind(user.password)
            .execute(&mut conn)
            .await?;
        let uuid = sqlx::query_scalar(&format!(
            "INSERT INTO {TABLE_USER_TOKENS} ({COL_USER_ID}) VALUES ($1) RETURNING {COL_TOKEN}"
        ))
            .bind(user.id)
            .fetch_one(&mut conn)
            .await?;
        Ok(success_resp(response::Register::new(uuid)))
    }
}

pub async fn login(
    payload: Json<request::Login>,
    db: ExtDb,
) -> Result<impl Responder, AppError> {
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
