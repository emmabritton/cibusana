mod constants;
mod errors;
mod init;
mod methods;
mod models;
mod utils;

use crate::init::{connect_database, setup};
use crate::methods::data::{
    alive, allergens, categories, companies, cuisines, flags, flavors, meal_times,
};
use crate::methods::get_food::{exact_food, food};
use crate::methods::get_meal::{exact_meal, meal};
use crate::methods::user::{login, register};
use anyhow::Result;
use axum::routing::{get, post};
use axum::{Extension, Router, Server};
use dotenvy::dotenv;
use log::debug;
use std::net::SocketAddr;
use std::sync::Arc;

#[tokio::main]
async fn main() -> Result<()> {
    let _ = dotenv().is_ok();
    println!("Starting food server...");

    let config = setup()?;

    debug!("Config read");

    let db_state = Arc::new(connect_database(&config).await?);

    debug!("DB connection established");

    let app = Router::new()
        .route("/alive", get(alive))
        .route("/users/register", post(register))
        .route("/users/login", post(login))
        .route("/food", get(food)) //.post(submit_food))
        .route("/food/:id", get(exact_food)) //.post(submit_food))
        .route("/meal", get(meal)) //.post(submit_food))
        .route("/meal/:id", get(exact_meal)) //.post(submit_food))
        .route("/data/categories", get(categories))
        .route("/data/companies", get(companies))
        .route("/data/flavors", get(flavors))
        .route("/data/allergens", get(allergens))
        .route("/data/meal_times", get(meal_times))
        .route("/data/flags", get(flags))
        .route("/data/cuisines", get(cuisines))
        .layer(Extension(db_state));

    let addr = SocketAddr::from((config.ip_addr, config.port));

    debug!("Server starting on {addr}");

    Server::bind(&addr).serve(app.into_make_service()).await?;

    Ok(())
}
