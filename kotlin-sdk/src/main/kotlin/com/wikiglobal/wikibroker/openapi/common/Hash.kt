package com.wikiglobal.wikibroker.openapi.common

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.SHA256

object Hash {
    suspend fun hmacSha256(key: ByteArray, message: ByteArray): ByteArray {
        val hmac = CryptographyProvider.Default.get(HMAC)
        val key = hmac.keyDecoder(SHA256).decodeFromByteArray(HMAC.Key.Format.RAW, key)
        return key.signatureGenerator().generateSignature(message)
    }

    suspend fun sha256Hash(message: ByteArray) = CryptographyProvider.Default.get(SHA256).hasher().hash(message)
}