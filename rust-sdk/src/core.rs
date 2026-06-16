use std::{collections::HashMap, string::FromUtf8Error};

use hmac::digest::InvalidLength;
use http::{HeaderValue, Method, Request};
use url::Url;

use crate::common::{CustomHeader, hmac_sha256, sha256_hash};

pub fn generate_signature(key: &str, message: &str) -> Result<String, InvalidLength> {
    let src = hmac_sha256(key.as_bytes(), message.as_bytes())?;
    Ok(hex::encode(src))
}

pub fn generate_canonical_string<B: ToString>(req: &Request<B>) -> Result<String, FromUtf8Error> {
    let empty_header_value = HeaderValue::from_str("").unwrap();
    let method = req.method().to_string();
    let path = req.uri().path().to_string();
    let canonical_query = build_canonical_query(req);
    let api_key = String::from_utf8(
        req.headers()
            .get(CustomHeader::ApiKey.to_string())
            .unwrap_or(&empty_header_value)
            .as_bytes()
            .to_vec(),
    )?;
    let timestamp = String::from_utf8(
        req.headers()
            .get(CustomHeader::Timestamp.to_string())
            .unwrap_or(&empty_header_value)
            .as_bytes()
            .to_vec(),
    )?;
    let nonce = String::from_utf8(
        req.headers()
            .get(CustomHeader::Nonce.to_string())
            .unwrap_or(&empty_header_value)
            .as_bytes()
            .to_vec(),
    )?;
    let body_hash = calculate_body_hash(req);
    Ok([
        method,
        path,
        canonical_query,
        api_key,
        timestamp,
        nonce,
        body_hash,
    ]
    .join("\n"))
}

fn build_canonical_query<B>(req: &Request<B>) -> String {
    let url = Url::parse(&req.uri().to_string()).unwrap();
    let mut query_map = HashMap::<String, Vec<String>>::new();
    for (key, value) in url.query_pairs().into_owned() {
        query_map.entry(key).or_default().push(value);
    }
    let mut keys: Vec<String> = query_map.to_owned().into_keys().collect();
    keys.sort();
    let mut parts = Vec::new();
    keys.into_iter().for_each(|key| {
        let mut values = query_map.get(&key).unwrap().to_owned();
        values.sort();
        values.iter().for_each(|value| {
            parts.push(format!("{}={}", key, value));
        });
    });
    parts.join("&")
}

fn calculate_body_hash<B: ToString>(req: &Request<B>) -> String {
    let body = if req.method() == Method::POST {
        &req.body().to_string()
    } else {
        ""
    };
    hex::encode(sha256_hash(body.as_bytes()))
}
