#[cfg(feature = "http")]
mod http_adapter {
    use http::Request;

    use crate::RequestLike;

    impl<T: ToString> RequestLike<T> for Request<T> {
        fn method(&self) -> &http::Method {
            Request::method(&self)
        }
        fn uri(&self) -> http::Uri {
            Request::uri(&self).to_owned()
        }
        fn headers(&self) -> &http::HeaderMap<http::HeaderValue> {
            Request::headers(&self)
        }
        fn body(&self) -> String {
            Request::body(&self).to_string()
        }
        fn headers_mut(&mut self) -> &mut http::HeaderMap<http::HeaderValue> {
            Request::headers_mut(self)
        }
    }
}
