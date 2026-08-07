package com.wikiglobal.wikibroker.openapi.common

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Hash {
    fun hmacSha256(key: ByteArray, message: ByteArray): ByteArray {
        val restoreKey = SecretKeySpec(key, "HmacSHA256")
        val mac = Mac.getInstance(restoreKey.algorithm).apply { init(restoreKey) }
        return mac.doFinal(message)
    }

    fun sha256Hash(message: ByteArray) = MessageDigest.getInstance("SHA-256").digest(message)!!
}