use http::{HeaderMap, HeaderValue, Method, Uri};

pub trait RequestLike<T: ToString> {
    fn method(&self) -> &Method;
    fn uri(&self) -> Uri;
    fn headers(&self) -> &HeaderMap<HeaderValue>;
    fn body(&self) -> String;
    fn headers_mut(&mut self) -> &mut HeaderMap<HeaderValue>;
}
