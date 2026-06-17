use std::str::FromStr;

use chrono::{DateTime, Utc};
use http::{HeaderMap, HeaderName, HeaderValue};
use uuid::Uuid;

mod adapters;
mod common;
mod core;

pub use crate::{common::*, core::*};

pub fn add_x_headers<B: ToString>(
    headers: &mut HeaderMap,
    api_key: Uuid,
    timestamp: DateTime<Utc>,
    nonce: Uuid,
) {
    headers.insert(
        HeaderName::from_str(&CustomHeader::ApiKey.to_string()).unwrap(),
        HeaderValue::from_str(&api_key.to_string()).unwrap(),
    );
    headers.insert(
        HeaderName::from_str(&CustomHeader::Timestamp.to_string()).unwrap(),
        HeaderValue::from_str(&timestamp.timestamp_millis().to_string()).unwrap(),
    );
    headers.insert(
        HeaderName::from_str(&CustomHeader::Nonce.to_string()).unwrap(),
        HeaderValue::from_str(&nonce.to_string()).unwrap(),
    );
}

pub fn sign<R: RequestLike<B>, B: ToString>(req: &mut R, key: &str) -> Result<(), SignError> {
    let canonical_string = generate_canonical_string(req)?;
    let signature = generate_signature(key, &canonical_string)?;
    req.headers_mut().insert(
        HeaderName::from_str(&CustomHeader::Signature.to_string()).unwrap(),
        HeaderValue::from_str(&signature).unwrap(),
    );
    Ok(())
}
