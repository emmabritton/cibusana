use actix_web::body::BoxBody;
use actix_web::http::StatusCode;
use actix_web::{HttpResponse, ResponseError};
use password_hash::rand_core::OsRng;
use password_hash::{PasswordHash, PasswordHasher, PasswordVerifier, SaltString};
use scrypt::Scrypt;
use std::fmt::{Debug, Display, Formatter};

pub fn encrypt(password: &str) -> Result<String, String> {
    let salt = SaltString::generate(&mut OsRng);
    let password = Scrypt
        .hash_password(password.as_bytes(), &salt)
        .map_err(|e| e.to_string())?;
    Ok(password.to_string())
}

pub fn verify(password: &str, hashed: &str) -> bool {
    Scrypt
        .verify_password(password.as_bytes(), &PasswordHash::new(hashed).unwrap())
        .is_ok()
}

pub struct AppError(anyhow::Error);

impl Debug for AppError {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.0)
    }
}

impl Display for AppError {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.0)
    }
}

impl ResponseError for AppError {
    fn status_code(&self) -> StatusCode {
        StatusCode::OK
    }

    fn error_response(&self) -> HttpResponse<BoxBody> {
        HttpResponse::Ok().body(format!("Something went wrong: {}", self.0))
    }
}

impl<E> From<E> for AppError
where
    E: Into<anyhow::Error>,
{
    fn from(err: E) -> Self {
        Self(err.into())
    }
}
