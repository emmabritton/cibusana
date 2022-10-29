use crate::constants::MIN_PASSWORD_LEN;
use crate::errors::*;
use crate::models::network::*;
use crate::models::wrappers::RequestWrapper;
use uuid::Uuid;

impl request::Register {
    pub fn validate(&self) -> Vec<ErrorNum> {
        let mut errors = vec![];
        if self.name.trim().is_empty() {
            errors.push(NAME_EMPTY);
        }
        if self.email.trim().is_empty() {
            errors.push(EMAIL_EMPTY);
        } else if !self.email.contains('@') {
            errors.push(EMAIL_INVALID);
        }
        if self.password.trim().is_empty() {
            errors.push(PASSWORD_EMPTY);
        } else if self.password.chars().count() < MIN_PASSWORD_LEN {
            errors.push(PASSWORD_TOO_SHORT);
        }
        errors
    }
}

impl<T> RequestWrapper<T> {
    pub fn validate(self) -> Result<(Uuid, T), Vec<ErrorNum>> {
        let mut errors = vec![];
        let mut uuid = Uuid::default();
        if let Some(token) = self.token {
            if let Ok(token) = Uuid::try_from(token.as_str()) {
                uuid = token;
            } else {
                errors.push(TOKEN_INVALID);
            }
        } else {
            errors.push(TOKEN_EMPTY);
        }

        if let Some(content) = self.content {
            return Ok((uuid, content));
        } else {
            errors.push(CONTENT_EMPTY);
        }

        Err(errors)
    }
}

impl request::Login {
    pub fn validate(&self) -> Vec<ErrorNum> {
        let mut errors = vec![];
        if self.email.trim().is_empty() {
            errors.push(EMAIL_EMPTY);
        }
        if self.password.trim().is_empty() {
            errors.push(PASSWORD_EMPTY);
        }
        errors
    }
}
