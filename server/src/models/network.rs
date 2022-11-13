/// Models only used in requests
pub mod request {
    use crate::constants::{MealTime, MeasurementUnit};
    use chrono::{DateTime, Utc};
    use serde::Deserialize;
    use std::collections::HashMap;
    use uuid::Uuid;

    #[derive(Debug, Deserialize)]
    pub struct Register {
        pub name: String,
        pub email: String,
        pub password: String,
        pub units: MeasurementUnit,
        pub target_weight_grams: Option<u32>,
        pub target_weight_date: Option<DateTime<Utc>>,
        pub height: u32
    }

    #[derive(Debug, Deserialize)]
    pub struct Login {
        pub email: String,
        pub password: String,
    }

    #[derive(Debug, Deserialize, Default)]
    pub struct Weight {
        pub date: DateTime<Utc>,
        pub grams: u32,
    }

    #[derive(Debug, Deserialize)]
    pub struct Food {
        pub id: Option<String>,
        pub name: Option<String>,
        pub calories_p100: Option<u32>,
        pub carbs_p100: Option<f32>,
        pub fat_p100: Option<f32>,
        pub protein_p100: Option<f32>,
        pub serving_sizes: Option<HashMap<String, u32>>,
        pub company: Option<String>,
        pub range: Option<String>,
        pub category: Option<String>,
        pub sub_category: Option<String>,
    }

    #[derive(Debug, Deserialize)]
    pub struct MealEntry {
        pub food_id: Uuid,
        pub is_meal: bool,
        pub meal_time: MealTime,
        pub date: DateTime<Utc>,
        pub grams: u32,
        pub calories: u32,
    }

    #[derive(Debug, Deserialize)]
    pub struct Measurement {
        pub date: DateTime<Utc>,
        pub measurements: HashMap<String, f32>,
    }

    #[derive(Debug, Deserialize)]
    pub struct UserData {
        pub units: MeasurementUnit,
        pub measurement_names: Vec<String>,
        pub target_weight_grams: Option<u32>,
        pub target_weight_date: Option<DateTime<Utc>>,
        pub height: u32
    }
}

/// Models only used in responses
pub mod response {
    use crate::constants::{Allergen, Cuisine, Flag, MealTime, MeasurementUnit};
    use chrono::{DateTime, Utc};
    use serde::Serialize;
    use std::collections::HashMap;
    use uuid::Uuid;

    #[derive(Debug, Serialize)]
    pub struct UserData {
        pub units: MeasurementUnit,
        pub measurement_names: Vec<String>,
        pub target_weight_grams: Option<u32>,
        pub target_weight_date: Option<DateTime<Utc>>,
        pub height: u32
    }

    #[derive(Debug, Serialize)]
    pub struct Measurement {
        pub date: DateTime<Utc>,
        pub measurements: HashMap<String, f32>,
    }

    #[derive(Debug, Serialize)]
    pub struct Register {
        pub token: Uuid,
    }

    #[derive(Debug, Serialize)]
    pub struct Login {
        pub token: Uuid,
        pub name: String,
    }

    #[derive(Debug, Serialize)]
    pub struct Food {
        pub id: Uuid,
        pub name: String,
        pub calories_p100: u32,
        pub carbs_p100: f32,
        pub fat_p100: f32,
        pub protein_p100: f32,
        pub serving_sizes: HashMap<String, u32>,
        pub company: Option<String>,
        pub range: Option<String>,
        pub category: String,
        pub sub_category: String,
        pub allergens: Vec<Allergen>,
        pub flags: Vec<Flag>,
    }

    #[derive(Debug, Serialize)]
    pub struct Meal {
        pub id: Uuid,
        pub name: String,
        pub ingredients: Vec<MealIngredient>,
        pub cuisine: Cuisine,
        pub serving_size: u32,
        pub calories_total: u32,
        pub carbs_total: f32,
        pub fat_total: f32,
        pub protein_total: f32,
        pub recipe: String,
        pub equipment: Vec<String>,
        pub cook_time: u32,
        pub prep_time: u32,
        pub allergens: Vec<Allergen>,
        pub flags: Vec<Flag>,
        pub flavors: Vec<String>,
    }

    #[derive(Debug, Serialize)]
    pub struct MealIngredient {
        pub id: Uuid,
        pub name: String,
        pub company: Option<String>,
        pub amount: u32,
        pub unit: String,
    }

    #[derive(Debug, Serialize)]
    pub struct Weight {
        #[serde(rename = "d")]
        pub date: DateTime<Utc>,
        #[serde(rename = "g")]
        pub grams: u32,
    }

    #[derive(Debug, Serialize)]
    pub struct MealEntry {
        pub id: u64,
        pub food_id: Uuid,
        pub is_meal: bool,
        pub meal_time: MealTime,
        pub date: DateTime<Utc>,
        pub grams: u32,
        pub calories: u32,
    }
}
