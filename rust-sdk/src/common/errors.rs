use std::string::FromUtf8Error;

use hmac::digest::InvalidLength;

#[derive(Debug)]
pub enum SignError {
    GenerateCanonicalStringError(FromUtf8Error),
    GenerateSignatureError(InvalidLength),
}

impl From<FromUtf8Error> for SignError {
    fn from(value: FromUtf8Error) -> Self {
        SignError::GenerateCanonicalStringError(value)
    }
}

impl From<InvalidLength> for SignError {
    fn from(value: InvalidLength) -> Self {
        SignError::GenerateSignatureError(value)
    }
}
