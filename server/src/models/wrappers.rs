use crate::errors::format_error;
use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct RequestWrapper<T> {
    pub content: Option<T>,
    pub token: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ResponseWrapper<T> {
    content: Option<T>,
    error: Option<ResponseError>,
}

impl<T> ResponseWrapper<T> {
    pub fn content(content: T) -> Self {
        Self {
            content: Some(content),
            error: None,
        }
    }

    pub fn error(error: ResponseError) -> Self {
        Self {
            content: None,
            error: Some(error),
        }
    }
}

#[derive(Debug, Serialize, Deserialize)]
pub struct PageWrapper<T> {
    page: usize,
    items: Vec<T>,
}

impl<T> PageWrapper<T> {
    pub fn new(page: usize, items: Vec<T>) -> Self {
        Self { page, items }
    }
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ResponseError {
    pub error_codes: Vec<i64>,
    pub error_message: String,
}

impl ResponseError {
    pub fn new(error_codes: Vec<i64>) -> Self {
        let error_message = format_error(&error_codes);
        Self {
            error_codes,
            error_message,
        }
    }

    #[allow(unused)]
    pub fn new_custom(error_codes: Vec<i64>, error_message: String) -> ResponseError {
        Self {
            error_codes,
            error_message,
        }
    }
}
