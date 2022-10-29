use anyhow::Result;
use log::LevelFilter;
use sqlx::{ConnectOptions, Pool, Postgres};
use sqlx::postgres::{PgConnectOptions, PgPoolOptions};
use uuid::Uuid;

const DB_URI: &str = "postgresql://foodserver:5h64jyurjteTRH_thtrgre@db-postgresql-lon1-96106-do-user-3581367-0.b.db.ondigitalocean.com:25060/journal?sslmode=require";

#[tokio::main]
async fn main() -> Result<()> {
    let db = connect_database(DB_URI).await?;

    let mut conn = db.acquire().await?;
    let foods: Vec<Food> = sqlx::query_as(&format!(
        "SELECT * FROM \"food-app\".\"food_item\""
    ))
        .fetch_all(&mut conn)
        .await?;

    if !foods.is_empty() {
        println!("Following foods have a calorie difference greater than 10%:")
    }
    for food in foods {
        if food.is_significant_diff() {
           println!(" {: <50} {: <40} {: <4} (DB: {: <4} Calc: {: <4})", food.id, food.name.chars().take(40).collect::<String>(), food.get_diff(), food.get_calories(), food.get_calc_calories());
        }
    }

    Ok(())
}

async fn connect_database(uri: &str) -> Result<Pool<Postgres>, sqlx::Error> {
    let mut options: PgConnectOptions = uri.parse()?;
    options.log_statements(LevelFilter::Warn);
    PgPoolOptions::new()
        .max_connections(4)
        .connect_with(options)
        .await
}

#[derive(sqlx::FromRow, Debug)]
struct Food {
    pub id: Uuid,
    pub name: String,
    pub calories_p100: i32,
    pub carbs_p100: f32,
    pub fat_p100: f32,
    pub protein_p100: f32,
}

impl Food {
    pub fn get_calc_calories(&self) -> usize {
        (self.carbs_p100 * 4.0 + self.fat_p100 * 9.0 + self.protein_p100 * 4.0).ceil() as usize
    }

    pub fn get_calories(&self) -> usize {
        self.calories_p100 as usize
    }

    pub fn get_diff(&self) -> usize {
        if self.get_calories() >= self.get_calc_calories() {
            self.get_calories() - self.get_calc_calories()
        } else {
            self.get_calc_calories() - self.get_calories()
        }
    }

    pub fn is_significant_diff(&self) -> bool {
        if self.get_diff() < 5 {
            return false;
        }
        let max = self.get_calories().max(self.get_calc_calories()) as f32;
        let percent  = (self.get_diff() as f32) / max;
        percent > 0.1
    }
}