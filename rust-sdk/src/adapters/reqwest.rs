#[cfg(feature = "reqwest")]
pub mod reqwest_adapter {
    use std::str::FromStr;

    use crate::{RequestLike, SignError};
    use chrono::{DateTime, Utc};
    use http::{HeaderMap, Uri};
    use reqwest::{Request, Response};
    use serde_json::{Value, from_slice};
    use uuid::Uuid;

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

    pub struct ClientWithAuth {
        inner: reqwest::Client,
        api_key: Uuid,
        api_secret: String,
        load_headers: fn(&mut HeaderMap, Uuid, DateTime<Utc>, Uuid),
        sign: fn(&mut Request, &str) -> Result<(), SignError>,
        timestamp_generator: fn() -> DateTime<Utc>,
        id_generator: fn() -> Uuid,
    }

    pub enum ClientError {
        Reqwest(reqwest::Error),
        Sign(SignError),
    }

    impl From<SignError> for ClientError {
        fn from(value: SignError) -> Self {
            ClientError::Sign(value)
        }
    }

    impl From<reqwest::Error> for ClientError {
        fn from(value: reqwest::Error) -> Self {
            ClientError::Reqwest(value)
        }
    }

    impl ClientWithAuth {
        pub fn new(
            inner: reqwest::Client,
            api_key: Uuid,
            api_secret: String,
            load_headers: fn(&mut HeaderMap, Uuid, DateTime<Utc>, Uuid),
            sign: fn(&mut Request, &str) -> Result<(), SignError>,
            timestamp_generator: fn() -> DateTime<Utc>,
            id_generator: fn() -> Uuid,
        ) -> Self {
            Self {
                inner,
                api_key,
                api_secret,
                load_headers,
                sign,
                timestamp_generator,
                id_generator,
            }
        }
        pub fn execute(
            &self,
            mut request: Request,
        ) -> impl Future<Output = Result<Response, ClientError>> {
            (self.load_headers)(
                request.headers_mut(),
                self.api_key,
                (self.timestamp_generator)(),
                (self.id_generator)(),
            );
            async {
                let result = (self.sign)(&mut request, &self.api_secret);
                match result {
                    Ok(()) => self
                        .inner
                        .execute(request)
                        .await
                        .map_err(|e| ClientError::from(e)),
                    Err(e) => Err(ClientError::from(e)),
                }
            }
        }
    }
}
