use anyhow::Result;
use log::{debug, error, LevelFilter};
use simple_logger::SimpleLogger;
use sqlx::postgres::{PgConnectOptions, PgPoolOptions};
use sqlx::{ConnectOptions, Pool, Postgres};
use std::env;
use std::net::Ipv4Addr;
use std::str::FromStr;

#[derive(Debug)]
pub struct Config {
    pub db_uri: String,
    pub ip_addr: Ipv4Addr,
    pub port: u16,
    pub sql_logging_level: LevelFilter,
}

pub fn get_env_var_level_filter(key: &str) -> LevelFilter {
    env::var(key).map_or(LevelFilter::Warn, |lvl| {
        LevelFilter::from_str(&lvl).unwrap_or(LevelFilter::Warn)
    })
}

pub fn setup() -> Result<Config> {
    let sql_logging_level = get_env_var_level_filter("RLF_SQL");
    let logging_level = get_env_var_level_filter("RLF_APP");

    SimpleLogger::new()
        .with_level(LevelFilter::Warn)
        .with_module_level("sqlx::query", sql_logging_level)
        .with_module_level("food_server", logging_level)
        .with_utc_timestamps()
        .init()?;

    let db_uri = env::var("DB_URI")?;

    let ip_addr: Ipv4Addr = match env::var("ADDRESS") {
        Ok(addr) => match addr.parse::<Ipv4Addr>() {
            Ok(addr) => addr,
            Err(err) => {
                error!("Invalid ADDRESS: {:?}", err);
                debug!("Using 0.0.0.0 as ADDRESS");
                Ipv4Addr::new(0, 0, 0, 0)
            }
        },
        Err(err) => {
            error!("Invalid/missing ADDRESS: {:?}", err);
            debug!("Using 0.0.0.0 as ADDRESS");
            Ipv4Addr::new(0, 0, 0, 0)
        }
    };

    let port = match env::var("PORT") {
        Ok(port_str) => match port_str.parse::<u16>() {
            Ok(port) => port,
            Err(err) => {
                error!("Invalid PORT ({port_str}): {:?}", err);
                debug!("Using 8080 as PORT");
                8080
            }
        },
        Err(err) => {
            error!("Invalid/missing PORT: {:?}", err);
            debug!("Using 8080 as PORT");
            8080
        }
    };

    let config = Config {
        db_uri,
        ip_addr,
        port,
        sql_logging_level,
    };

    Ok(config)
}

pub async fn connect_database(config: &Config) -> Result<Pool<Postgres>, sqlx::Error> {
    let mut options: PgConnectOptions = config.db_uri.parse()?;
    options.log_statements(config.sql_logging_level);
    PgPoolOptions::new()
        .max_connections(4)
        .connect_with(options)
        .await
}
