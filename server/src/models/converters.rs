use crate::constants::{Allergen, Flag};
use crate::errors::*;
use crate::models::db;
use crate::models::db::MeasureUnit;
use crate::models::network::{request, response};
use std::collections::HashMap;
use std::num::TryFromIntError;
use std::str::FromStr;
use uuid::Uuid;

impl request::UserData {
    pub fn to_db(&self, user_id: Uuid) -> db::UserData {
        db::UserData {
            user_id,
            preferred_unit: self.units,
            default_measurement_names: self.measurement_names.clone(),
            target_weight_grams: self.target_weight_grams.map(|value| value as i32),
            target_weight_date: self.target_weight_date,
            height: self.height as i32
        }
    }
}

impl db::UserData {
    pub fn into_response(self) -> response::UserData {
        response::UserData {
            units: self.preferred_unit,
            measurement_names: self.default_measurement_names,
            target_weight_grams: self.target_weight_grams.map(|value| value as u32),
            target_weight_date: self.target_weight_date,
            height: self.height as u32
        }
    }
}

impl db::Measurement {
    pub fn into_response(self) -> response::Measurement {
        response::Measurement {
            date: self.date,
            measurements: self
                .m_names
                .into_iter()
                .zip(self.m_values.into_iter())
                .collect(),
        }
    }
}

impl request::Measurement {
    pub fn to_db(&self, user_id: Uuid) -> db::Measurement {
        db::Measurement {
            user_id,
            date: self.date,
            m_names: self.measurements.keys().cloned().collect(),
            m_values: self.measurements.values().cloned().collect(),
        }
    }
}

impl db::MealEntry {
    pub fn into_response(self) -> Result<response::MealEntry, ErrorNum> {
        Ok(response::MealEntry {
            id: u64::try_from(self.id).map_err(|_| INVALID_ID)?,
            food_id: self.entry_id,
            is_meal: self.is_meal,
            meal_time: self.meal_time,
            date: self.date,
            grams: u32::try_from(self.amount).map_err(|_| INVALID_GRAMS)?,
            calories: u32::try_from(self.calories).map_err(|_| INVALID_CALORIES)?,
        })
    }
}

impl db::Meal {
    pub fn into_response(
        self,
        ingredients: &HashMap<Uuid, (String, Option<String>, Vec<String>)>,
    ) -> Result<response::Meal, ErrorNum> {
        let allergens = self
            .allergens
            .into_iter()
            .map(|allergy| Allergen::from_str(&allergy).map_err(|_| INVALID_ALLERGEN))
            .collect::<Vec<Result<Allergen, ErrorNum>>>()
            .into_iter()
            .collect::<Result<Vec<Allergen>, ErrorNum>>()?;
        let flags = self
            .flags
            .into_iter()
            .map(|flag| Flag::from_str(&flag).map_err(|_| INVALID_FLAG))
            .collect::<Vec<Result<Flag, ErrorNum>>>()
            .into_iter()
            .collect::<Result<Vec<Flag>, ErrorNum>>()?;
        let ingredients = self
            .food_amounts
            .into_iter()
            .zip(self.food_ids)
            .map(|(amount, id)| {
                if let Some(ingredient) = ingredients.get(&id) {
                    Ok(response::MealIngredient {
                        id,
                        name: ingredient.0.clone(),
                        company: ingredient.1.clone(),
                        amount: u32::try_from(amount).map_err(|_| INVALID_CALORIES)?,
                        unit: ingredient.2.get_unit().to_string(),
                    })
                } else {
                    Err(INVALID_INGREDIENT)
                }
            })
            .collect::<Vec<Result<response::MealIngredient, ErrorNum>>>()
            .into_iter()
            .collect::<Result<Vec<response::MealIngredient>, ErrorNum>>()?;
        Ok(response::Meal {
            id: self.id,
            name: self.name,
            ingredients,
            cuisine: self.cuisine,
            serving_size: u32::try_from(self.serving_size).map_err(|_| INVALID_CALORIES)?,
            calories_total: u32::try_from(self.calories_total).map_err(|_| INVALID_CALORIES)?,
            carbs_total: self.carbs_total,
            fat_total: self.fat_total,
            protein_total: self.protein_total,
            recipe: self.recipe,
            equipment: self.equipment,
            cook_time: u32::try_from(self.cook_time).map_err(|_| INVALID_CALORIES)?,
            prep_time: u32::try_from(self.prep_time).map_err(|_| INVALID_CALORIES)?,
            allergens,
            flags,
            flavors: self.flavors,
        })
    }
}

impl TryFrom<db::Food> for response::Food {
    type Error = ErrorNum;

    fn try_from(value: db::Food) -> Result<Self, Self::Error> {
        let serving_sizes = value
            .serving_size
            .iter()
            .map(|num| u32::try_from(*num))
            .collect::<Vec<Result<u32, TryFromIntError>>>()
            .into_iter()
            .collect::<Result<Vec<u32>, TryFromIntError>>()
            .map_err(|_| INVALID_SERVING_SIZE)?;
        let allergens = value
            .allergens
            .into_iter()
            .map(|allergy| Allergen::from_str(&allergy).map_err(|_| INVALID_ALLERGEN))
            .collect::<Vec<Result<Allergen, ErrorNum>>>()
            .into_iter()
            .collect::<Result<Vec<Allergen>, ErrorNum>>()?;
        let flags = value
            .flags
            .into_iter()
            .map(|flag| Flag::from_str(&flag).map_err(|_| INVALID_FLAG))
            .collect::<Vec<Result<Flag, ErrorNum>>>()
            .into_iter()
            .collect::<Result<Vec<Flag>, ErrorNum>>()?;
        Ok(response::Food {
            id: value.id,
            name: value.name,
            calories_p100: u32::try_from(value.calories_p100).map_err(|_| INVALID_CALORIES)?,
            carbs_p100: value.carbs_p100,
            fat_p100: value.fat_p100,
            protein_p100: value.protein_p100,
            serving_sizes: value
                .serving_size_name
                .into_iter()
                .zip(serving_sizes.into_iter())
                .collect(),
            company: value.company,
            range: value.range,
            category: value.category.to_string(),
            sub_category: value.sub_category.to_string(),
            allergens,
            flags,
        })
    }
}

// impl request::Food {
//     pub fn into_db(self, token: Uuid) -> Result<db::Food, Vec<ErrorNum>> {
//         let mut errors = vec![];
//         let mut id: Uuid = Uuid::default();
//         let mut name: String = String::default();
//         let mut calories_p100: i32 = 0;
//         let mut carbs_p100: f32 = 0.0;
//         let mut fat_p100: f32 = 0.0;
//         let mut protein_p100: f32 = 0.0;
//         let mut company: Option<String> = None;
//         let mut range: Option<String> = None;
//         let mut category: FoodCategory = FoodCategory::Other;
//         let mut sub_category: FoodSubCategory = FoodSubCategory::Other;
//         if let Some(id_str) = self.id {
//             if let Ok(req_id) = Uuid::try_from(id_str.as_str()) {
//                 id = req_id;
//             } else {
//                 errors.push(ID_EMPTY);
//             }
//         } else {
//             errors.push(ID_EMPTY);
//         }
//         if let Some(name_str) = self.name {
//             let trimmed = name_str.trim();
//             if !trimmed.is_empty() {
//                 name = trimmed.to_string();
//             } else {
//                 errors.push(NAME_EMPTY);
//             }
//         } else {
//             errors.push(NAME_EMPTY);
//         }
//         if let Some(company_str) = self.company {
//             company = Some(company_str)
//         }
//         if let Some(range_str) = self.range {
//             range = Some(range_str)
//         }
//         if let Some(cat_str) = self.category {
//             if let Ok(cat) = FoodCategory::try_from(cat_str.as_str()) {
//                 category = cat;
//             } else {
//                 errors.push(CATEGORY_INVALID);
//             }
//         } else {
//             errors.push(CATEGORY_EMPTY);
//         }
//         if let Some(subcat_str) = self.sub_category {
//             if let Ok(subcat) = FoodSubCategory::try_from(subcat_str.as_str()) {
//                 if category.sub_cats().contains(&subcat) {
//                     sub_category = subcat;
//                 } else {
//                     errors.push(SUBCATEGORY_MISMATCH);
//                 }
//             } else {
//                 errors.push(SUBCATEGORY_INVALID);
//             }
//         } else {
//             errors.push(SUBCATEGORY_EMPTY);
//         }
//         if let Some(cals) = self.calories_p100 {
//             calories_p100 = cals as i32;
//         } else {
//             errors.push(CALORIES_EMPTY);
//         }
//         if let Some(carbs) = self.carbs_p100 {
//             carbs_p100 = carbs;
//         } else {
//             errors.push(CARBS_EMPTY);
//         }
//         if let Some(fat) = self.fat_p100 {
//             fat_p100 = fat;
//         } else {
//             errors.push(FAT_EMPTY);
//         }
//         if let Some(protein) = self.protein_p100 {
//             protein_p100 = protein;
//         } else {
//             errors.push(PROTEIN_EMPTY);
//         }
//
//         if !errors.is_empty() {
//             return Err(errors);
//         }
//
//         Ok(db::Food {
//             id,
//             name,
//             created_by: token,
//             created_at: Utc::now(),
//             calories_p100,
//             carbs_p100,
//             fat_p100,
//             protein_p100,
//             serving_size: vec![],
//             serving_size_name: vec![],
//             range,
//             company,
//             flavors: vec![],
//             category,
//             sub_category,
//             allergens: vec![],
//             flags: vec![],
//         })
//     }
// }
