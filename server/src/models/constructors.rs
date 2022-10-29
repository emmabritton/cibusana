use crate::models::network::*;
use uuid::Uuid;

impl response::Register {
    pub fn new(token: Uuid) -> Self {
        Self { token }
    }
}

impl response::Login {
    pub fn new(token: Uuid, name: String) -> Self {
        Self { token, name }
    }
}
