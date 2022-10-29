use axum::http::StatusCode;
use axum::response::{IntoResponse, Response};
use password_hash::rand_core::OsRng;
use password_hash::{PasswordHash, PasswordHasher, PasswordVerifier, SaltString};
use scrypt::Scrypt;

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

impl IntoResponse for AppError {
    fn into_response(self) -> Response {
        (
            StatusCode::INTERNAL_SERVER_ERROR,
            format!("Something went wrong: {}", self.0),
        )
            .into_response()
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
