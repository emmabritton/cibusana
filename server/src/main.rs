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
use dotenvy::dotenv;
use log::debug;
use actix_web::{App, HttpServer};
use actix_web::web::{Data, delete, get, post};
use crate::methods::meal_entry::{add_entry, delete_entry, first_entry, get_entries, last_entry};
use crate::methods::weight::{delete_weight, first_weight, get_weights, last_weight, set_weight};

#[actix_web::main]
async fn main() -> Result<()> {
    let _ = dotenv().is_ok();
    println!("Starting food server...");

    let config = setup()?;

    debug!("Config read");

    let db_state = Data::new(connect_database(&config).await?);

    debug!("DB connection established");

    debug!("Server starting on {}:{}", config.ip_addr, config.port);

    HttpServer::new(move || {
        App::new()
            .app_data(db_state.clone())
            .route("/alive", get().to(alive))
            .route("/users/register", post().to(register))
            .route("/users/login", post().to(login))
            .route("/food", get().to(food)) //.post(submit_food))
            .route("/food/{id}", get().to(exact_food)) //.post(submit_food))
            .route("/meal", get().to(meal)) //.post(submit_food))
            .route("/meal/{id}", get().to(exact_meal)) //.post(submit_food))
            .route("/data/categories", get().to(categories))
            .route("/data/companies", get().to(companies))
            .route("/data/flavors", get().to(flavors))
            .route("/data/allergens", get().to(allergens))
            .route("/data/meal_times", get().to(meal_times))
            .route("/data/flags", get().to(flags))
            .route("/data/cuisines", get().to(cuisines))
            .route("/me/weight", get().to(get_weights))
            .route("/me/weight/{date}", delete().to(delete_weight))
            .route("/me/weight/first", get().to(first_weight))
            .route("/me/weight/last", get().to(last_weight))
            .route("/me/weight", post().to(set_weight))
            .route("/me/entry/first", get().to(first_entry))
            .route("/me/entry/last", get().to(last_entry))
            .route("/me/entry", post().to(add_entry))
            .route("/me/entry", get().to(get_entries))
            .route("/me/entry/{id}", delete().to(delete_entry))
    }).bind((config.ip_addr, config.port))?.run().await?;

    Ok(())
}
