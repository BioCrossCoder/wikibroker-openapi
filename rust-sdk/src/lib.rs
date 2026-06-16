use std::str::FromStr;

use chrono::{DateTime, Utc};
use http::{HeaderMap, HeaderName, HeaderValue, Request};
use uuid::Uuid;

mod common;
mod core;

use crate::{
    common::{CustomHeader, SignError},
    core::{generate_canonical_string, generate_signature},
};

fn add_x_headers<B: ToString>(
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

fn sign<B: ToString>(req: &mut Request<B>, key: &str) -> Result<(), SignError> {
    let canonical_string = generate_canonical_string(req)?;
    let signature = generate_signature(key, &canonical_string)?;
    req.headers_mut().insert(
        HeaderName::from_str(&CustomHeader::Signature.to_string()).unwrap(),
        HeaderValue::from_str(&signature).unwrap(),
    );
    Ok(())
}
