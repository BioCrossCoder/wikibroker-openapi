package com.wikiglobal.wikibroker.openapi

import com.wikiglobal.wikibroker.openapi.common.enums.CustomHeaders
import com.wikiglobal.wikibroker.openapi.common.interfaces.RequestBuilder
import com.wikiglobal.wikibroker.openapi.common.interfaces.RequestOperator
import kotlin.time.Instant
import kotlin.uuid.Uuid

fun <T> addXHeaders(
    builder: RequestBuilder<T>,
    apiKey: Uuid,
    timestamp: Instant,
    nonce: Uuid
) {
    builder.header(CustomHeaders.ApiKey.value, apiKey.toString())
        .header(CustomHeaders.Timestamp.value, timestamp.toEpochMilliseconds().toString())
        .header(CustomHeaders.Nonce.value, nonce.toString())
}

fun <T> sign(req: RequestOperator<T>, key: String) {
    val canonicalString = generateCanonicalString(req)
    val signature = generateSignature(key, canonicalString)
    req.header(CustomHeaders.Signature.value, signature)
}
