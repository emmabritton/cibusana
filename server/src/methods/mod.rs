use std::str::FromStr;
use crate::models::wrappers::{PageWrapper, ResponseError, ResponseWrapper};
use anyhow::Result;
use sqlx::pool::PoolConnection;
use sqlx::{Pool, Postgres};
use actix_web::http::header::{CacheControl, CacheDirective, HeaderMap};
use actix_web::HttpResponse;
use actix_web::web::Data;
use log::error;
use serde::Serialize;
use uuid::Uuid;
use crate::errors::{ErrorNum, SERVER_ERROR, TOKEN_EMPTY, TOKEN_INVALID};
use crate::methods::consts::*;

pub mod data;
pub mod get_food;
pub mod get_meal;
pub mod user;
pub mod weight;

mod consts {
    pub const TABLE_USERS: &str = r#" "food-app"."users" "#;
    pub const TABLE_USER_TOKENS: &str = r#" "food-app"."user_tokens" "#;
    pub const TABLE_FOOD: &str = r#" "food-app"."food_item" "#;
    pub const TABLE_MEAL: &str = r#" "food-app"."meal" "#;
    pub const TABLE_MEAL_ENTRY: &str = r#" "food-app"."meal_entry" "#;
    pub const TABLE_WEIGHT: &str = r#" "food-app"."weight" "#;

    pub const COL_EMAIL: &str = "email_address";
    pub const COL_PASS: &str = "password";
    pub const COL_NAME: &str = "name";
    pub const COL_ID: &str = "id";
    pub const COL_USER_ID: &str = "user_id";
    pub const COL_TOKEN: &str = "token";
    pub const COL_CALORIES_P100: &str = "calories_p100";
    pub const COL_CARBS_P100: &str = "carbs_p100";
    pub const COL_FAT_P100: &str = "fat_p100";
    pub const COL_PROTEIN_P100: &str = "protein_p100";
    pub const COL_SERVING_SIZE: &str = "serving_size";
    pub const COL_SERVING_SIZE_NAME: &str = "serving_size_name";
    pub const COL_CREATED_BY: &str = "created_by";
    pub const COL_CREATED_AT: &str = "created_at";
    pub const COL_COMPANY: &str = "company";
    pub const COL_RANGE: &str = "range";
    pub const COL_CATEGORY: &str = "category";
    pub const COL_SUBCATEGORY: &str = "sub_category";
    pub const COL_FLAVORS: &str = "flavors";
    pub const COL_ALLERGENS: &str = "allergens";
    pub const COL_FLAGS: &str = "flags";
    pub const COL_CUISINE: &str = "cuisine";
    pub const COL_RECIPE: &str = "recipe";
    pub const COL_EQUIPMENT: &str = "equipment";
    pub const COL_FOOD_IDS: &str = "food_ids";
    pub const COL_FOOD_AMOUNTS: &str = "food_amounts";
    pub const COL_CALORIES_TOTAL: &str = "calories_total";
    pub const COL_CARBS_TOTAL: &str = "carbs_total";
    pub const COL_FAT_TOTAL: &str = "fat_total";
    pub const COL_PROTEIN_TOTAL: &str = "protein_total";
    pub const COL_KGS: &str = "kgs";
    pub const COL_DATE: &str = "date";

    pub const TOKEN_HEADER: &str = "x-token";
}

type ExtDb = Data<Pool<Postgres>>;

fn get_user_token(headers: &HeaderMap) -> Result<Uuid, ErrorNum> {
    let value = headers.get(TOKEN_HEADER);
    if value.is_none() {
        return Err(TOKEN_EMPTY);
    }
    let text = value.unwrap().to_str().map_err(|e| {
        error!("Reading token: {e}");
        TOKEN_INVALID
    })?;
    let uuid = Uuid::from_str(text)
        .map_err(|e| {
            error!("Parsing token: {e}");
            TOKEN_INVALID
        })?;
    Ok(uuid)
}

async fn get_user_id(headers: &HeaderMap, conn: &mut PoolConnection<Postgres>) -> Result<Uuid, ErrorNum> {
    let token = get_user_token(headers)?;

    let user_id: Option<Uuid> = sqlx::query_scalar(&format!("SELECT {COL_USER_ID} FROM {TABLE_USER_TOKENS} WHERE {COL_TOKEN} = $1"))
        .bind(token)
        .fetch_optional(conn)
        .await
        .map_err(|e| {
            error!("DB Error when getting user id: {e}");
            SERVER_ERROR
        })?;

    match user_id {
        None => Err(TOKEN_INVALID),
        Some(id) => Ok(id)
    }
}

fn error_resp(error_codes: Vec<i64>) -> HttpResponse {
    HttpResponse::Ok()
        .json(ResponseWrapper::<ResponseError>::error(ResponseError::new(
            error_codes
        )))
}

fn success_cached_resp<T: Serialize>(content: T) -> HttpResponse {
    HttpResponse::Ok()
        .append_header(CacheControl(vec![CacheDirective::MaxAge(18000)]))
        .json(ResponseWrapper::content(content))
}

fn success_resp<T: Serialize>(content: T) -> HttpResponse {
    HttpResponse::Ok()
        .json(ResponseWrapper::content(content))
}

fn success_page_resp<T: Serialize>(page: usize, content: Vec<T>) -> HttpResponse {
    HttpResponse::Ok()
        .json(ResponseWrapper::content(PageWrapper::new(page, content)))
}

async fn is_valid_user(conn: &mut PoolConnection<Postgres>, token: Uuid) -> Result<bool> {
    let row = sqlx::query(&format!(
        "SELECT {} WHERE {} = $1",
        consts::COL_ID,
        consts::COL_ID
    ))
        .bind(token)
        .fetch_optional(conn)
        .await?;
    Ok(row.is_some())
}
