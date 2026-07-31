package com.wikiglobal.wikibroker.openapi.common

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Hash {
    fun hmacSha256(key: ByteArray, message: ByteArray): ByteArray {
        val restoreKey = SecretKeySpec(key, "HmacSHA256")
        val mac = Mac.getInstance(restoreKey.algorithm)
        mac.init(restoreKey)
        return mac.doFinal(message)
    }

    fun sha256Hash(message: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(message)
        return md.digest()
    }
}