package com.wikiglobal.wikibroker.openapi

import com.wikiglobal.wikibroker.openapi.common.enums.CustomHeaders
import com.wikiglobal.wikibroker.openapi.common.models.HttpRequestData
import kotlin.time.Instant
import kotlin.uuid.Uuid

fun addXHeaders(
    req: HttpRequestData,
    apiKey: Uuid,
    timestamp: Instant,
    nonce: Uuid
) {
    req.apply {
        setHeader(CustomHeaders.ApiKey.value, apiKey.toString())
        setHeader(CustomHeaders.Timestamp.value, timestamp.toEpochMilliseconds().toString())
        setHeader(CustomHeaders.Nonce.value, nonce.toString())
    }
}

fun sign(req: HttpRequestData, key: String) {
    val canonicalString = generateCanonicalString(req)
    val signature = generateSignature(key, canonicalString)
    req.setHeader(CustomHeaders.Signature.value, signature)
}
