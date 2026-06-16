use hmac::{Hmac, KeyInit, Mac, digest::InvalidLength};
use sha2::{Digest, Sha256};

pub fn sha256_hash(message: &[u8]) -> Vec<u8> {
    Sha256::digest(message).to_vec()
}

pub fn hmac_sha256(key: &[u8], message: &[u8]) -> Result<Vec<u8>, InvalidLength> {
    let mut h = Hmac::<Sha256>::new_from_slice(key)?;
    h.update(message);
    Ok(h.finalize().into_bytes().to_vec())
}
