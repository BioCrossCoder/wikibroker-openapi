#[cfg(test)]
mod tests {
    use std::str::FromStr;

    use chrono::{DateTime, Utc};
    use http::{Method, Request};
    use maplit::hashmap;
    use serde_json::Value;
    use uuid::Uuid;
    use wikibroker_openapi_sdk::*;

    const BASE_URL: &str = "https://api.example.com";
    const PATH: &str = "test";
    const QUERY: &str = "q1=c&q2=b&q1=a";
    fn api_url() -> String {
        format!("{}/{}", BASE_URL, PATH)
    }
    fn full_url() -> String {
        format!("{}?{}", api_url(), QUERY)
    }
    fn body() -> Value {
        let data = hashmap! {
            "key"=>"value"
        };
        serde_json::to_value(data).unwrap()
    }
    const METHOD: Method = Method::POST;
    fn api_key() -> Uuid {
        Uuid::from_str("ef05e5b0-9daf-49e3-a0f4-9a3c13f55c3b").unwrap()
    }
    const API_SECRET: &str = "4ae4bf20-0afa-4122-ade8-c0beca7bd5e4";
    const TIMESTAMP: DateTime<Utc> = DateTime::from_timestamp_millis(1798115622000).unwrap();
    fn nonce() -> Uuid {
        Uuid::from_str("4428a206-1afd-4b15-a98d-43e91f49a08d").unwrap()
    }
    const EXPECTED_SIGNATURE: &str =
        "1b0c80dbbc30905719559ab5526dfd59bae04d7337c8843efd9e51ff0af6dfb4";

    #[test]
    fn test_sign() {
        let mut req = Request::builder()
            .method(METHOD)
            .uri(full_url())
            .body(body())
            .unwrap();
        add_x_headers::<Value>(req.headers_mut(), api_key(), TIMESTAMP, nonce());
        sign::<Value>(&mut req, API_SECRET).unwrap();
        let actual_signature = req
            .headers()
            .get(CustomHeader::Signature.to_string())
            .unwrap()
            .to_str()
            .unwrap();
        assert_eq!(actual_signature, EXPECTED_SIGNATURE);
    }

    #[test]
    fn test_reqwest() {
        let client = reqwest::Client::new();
        let raw_req = client
            .request(METHOD, full_url())
            .body(body().to_string())
            .build()
            .unwrap();
        let mut req = Request::builder()
            .method(raw_req.method())
            .uri(raw_req.url().to_string())
            .body(
                serde_json::from_slice::<Value>(raw_req.body().unwrap().as_bytes().unwrap())
                    .unwrap(),
            )
            .unwrap();
        add_x_headers::<Value>(req.headers_mut(), api_key(), TIMESTAMP, nonce());
        sign(&mut req, API_SECRET).unwrap();
        let actual_signature = req
            .headers()
            .get(CustomHeader::Signature.to_string())
            .unwrap()
            .to_str()
            .unwrap();
        assert_eq!(actual_signature, EXPECTED_SIGNATURE);
    }
}
