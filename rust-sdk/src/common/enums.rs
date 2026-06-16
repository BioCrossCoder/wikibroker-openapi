use std::fmt::{self};

pub enum CustomHeader {
    ApiKey,
    Timestamp,
    Nonce,
    Signature,
}

impl fmt::Display for CustomHeader {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            CustomHeader::ApiKey => write!(f, "X-Api-Key"),
            CustomHeader::Timestamp => write!(f, "X-Timestamp"),
            CustomHeader::Nonce => write!(f, "X-Nonce"),
            CustomHeader::Signature => write!(f, "X-Signature"),
        }
    }
}
