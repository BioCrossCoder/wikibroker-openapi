#[cfg(test)]
mod tests {
    use std::str::FromStr;

    use chrono::DateTime;
    use http::{Method, Request};
    use maplit::hashmap;
    use serde_json::Value;
    use uuid::Uuid;
    use wikibroker_openapi_sdk::{CustomHeader, add_x_headers, sign};

    #[test]
    fn test_sign() {
        let base_url = "https://api.example.com";
        let path = "test";
        let query = "q1=c&q2=b&q1=a";
        let api_url = format!("{}/{}", base_url, path);
        let full_url = format!("{}?{}", api_url, query);
        let data = hashmap! {
            "key"=>"value"
        };
        let body = serde_json::to_value(data).unwrap();
        let method = Method::POST;
        let api_key = Uuid::from_str("ef05e5b0-9daf-49e3-a0f4-9a3c13f55c3b").unwrap();
        let api_secret = "4ae4bf20-0afa-4122-ade8-c0beca7bd5e4";
        let timestamp = DateTime::from_timestamp_millis(1798115622000).unwrap();
        let nonce = Uuid::from_str("4428a206-1afd-4b15-a98d-43e91f49a08d").unwrap();
        let expected_signature = "1b0c80dbbc30905719559ab5526dfd59bae04d7337c8843efd9e51ff0af6dfb4";
        let mut req = Request::builder()
            .method(method)
            .uri(full_url)
            .body(body)
            .unwrap();
        add_x_headers::<Value>(req.headers_mut(), api_key, timestamp, nonce);
        sign::<Value>(&mut req, api_secret).unwrap();
        let actual_signature = req
            .headers()
            .get(CustomHeader::Signature.to_string())
            .unwrap()
            .to_str()
            .unwrap();
        assert_eq!(actual_signature, expected_signature);
    }
}
