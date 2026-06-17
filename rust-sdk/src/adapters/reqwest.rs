#[cfg(feature = "reqwest")]
mod reqwest {
    use std::str::FromStr;

    use crate::RequestLike;
    use http::Uri;
    use reqwest::Request;
    use serde_json::{Value, from_slice};

    impl<T: ToString> RequestLike<T> for Request {
        fn method(&self) -> &http::Method {
            Request::method(&self)
        }
        fn uri(&self) -> http::Uri {
            Uri::from_str(&Request::url(&self).to_string()).unwrap()
        }
        fn headers(&self) -> &http::HeaderMap<http::HeaderValue> {
            Request::headers(&self)
        }
        fn body(&self) -> String {
            from_slice::<Value>(Request::body(&self).unwrap().as_bytes().unwrap())
                .unwrap()
                .to_string()
        }
        fn headers_mut(&mut self) -> &mut http::HeaderMap<http::HeaderValue> {
            Request::headers_mut(self)
        }
    }
}
