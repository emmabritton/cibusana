use crate::constants::MIN_PASSWORD_LEN;
use crate::errors::*;
use crate::models::network::*;

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
