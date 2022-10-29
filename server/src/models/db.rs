use crate::constants::categories::{FoodCategory, FoodSubCategory};
use crate::constants::{Cuisine, Flag, MealTime};
use chrono::NaiveDateTime;
use uuid::Uuid;

#[derive(Debug, sqlx::FromRow)]
pub struct User {
    pub id: Uuid,
    pub name: String,
    pub email: String,
    pub password: String,
}

#[derive(Debug, sqlx::FromRow)]
pub struct UserTokens {
    pub id: u64,
    pub user_id: Uuid,
    pub token: Uuid,
}

#[derive(Debug, sqlx::FromRow)]
pub struct Food {
    pub id: Uuid,
    pub name: String,
    pub created_by: Uuid,
    pub created_at: NaiveDateTime,
    pub calories_p100: i32,
    pub carbs_p100: f32,
    pub fat_p100: f32,
    pub protein_p100: f32,
    pub serving_size: Vec<i32>,
    pub serving_size_name: Vec<String>,
    pub range: Option<String>,
    pub company: Option<String>,
    pub flavors: Vec<String>,
    pub category: FoodCategory,
    pub sub_category: FoodSubCategory,
    pub allergens: Vec<String>,
    pub flags: Vec<String>,
}

#[derive(Debug, sqlx::FromRow)]
pub struct UserEntry {
    pub id: u64,
    pub user_id: Uuid,
    pub food_id: Uuid,
    pub meal_time: MealTime,
    pub date: NaiveDateTime,
}

#[derive(Debug, sqlx::FromRow)]
pub struct Meal {
    pub id: Uuid,
    pub created_by: Uuid,
    pub created_at: NaiveDateTime,
    pub name: String,
    pub food_ids: Vec<Uuid>,
    pub food_amounts: Vec<i32>,
    pub cuisine: Cuisine,
    pub serving_size: i32,
    pub calories_p100: i32,
    pub carbs_p100: f32,
    pub fat_p100: f32,
    pub protein_p100: f32,
    pub calories_total: i32,
    pub carbs_total: f32,
    pub fat_total: f32,
    pub protein_total: f32,
    pub recipe: String,
    pub equipment: Vec<String>,
    pub cook_time: i32,
    pub prep_time: i32,
    pub allergens: Vec<String>,
    pub flags: Vec<String>,
    pub flavors: Vec<String>,
}

pub trait MeasureUnit {
    fn get_unit(&self) -> &'static str;
}

impl MeasureUnit for Vec<String> {
    fn get_unit(&self) -> &'static str {
        if self.contains(&Flag::Drink.to_string()) || self.contains(&Flag::Liquid.to_string()) {
            "ml"
        } else {
            "g"
        }
    }
}

impl MeasureUnit for Food {
    fn get_unit(&self) -> &'static str {
        self.flags.get_unit()
    }
}
