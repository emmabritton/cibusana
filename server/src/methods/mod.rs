use crate::models::wrappers::{PageWrapper, ResponseError, ResponseWrapper};
use anyhow::Result;
use axum::body::BoxBody;
use axum::http::{Response, StatusCode};
use axum::response::IntoResponse;
use axum::{Extension, Json};
use sqlx::pool::PoolConnection;
use sqlx::{Pool, Postgres};
use std::sync::Arc;
use uuid::Uuid;

pub mod data;
pub mod get_food;
pub mod get_meal;
pub mod user;

mod consts {
    pub const TABLE_USERS: &str = r#" "food-app"."users" "#;
    pub const TABLE_USER_TOKENS: &str = r#" "food-app"."user_tokens" "#;
    pub const TABLE_FOOD: &str = r#" "food-app"."food_item" "#;
    pub const TABLE_MEAL: &str = r#" "food-app"."meal" "#;
    pub const TABLE_MEAL_ENTRY: &str = r#" "food-app"."meal_entry" "#;

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
}

type ExtDb = Extension<Arc<Pool<Postgres>>>;

fn error_resp(error_codes: Vec<i64>) -> Response<BoxBody> {
    (
        StatusCode::OK,
        Json(ResponseWrapper::<ResponseError>::error(ResponseError::new(
            error_codes,
        ))),
    )
        .into_response()
}

fn success_resp<T>(content: T) -> Response<BoxBody>
where
    (StatusCode, Json<ResponseWrapper<T>>): IntoResponse,
{
    (StatusCode::OK, Json(ResponseWrapper::content(content))).into_response()
}

fn success_page_resp<T>(page: usize, content: Vec<T>) -> Response<BoxBody>
where
    (StatusCode, Json<ResponseWrapper<PageWrapper<T>>>): IntoResponse,
{
    (
        StatusCode::OK,
        Json(ResponseWrapper::content(PageWrapper::new(page, content))),
    )
        .into_response()
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
