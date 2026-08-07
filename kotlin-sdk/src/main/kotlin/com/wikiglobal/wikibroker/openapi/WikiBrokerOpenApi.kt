package com.wikiglobal.wikibroker.openapi

import com.wikiglobal.wikibroker.openapi.adapters.OkHttpInterceptor
import com.wikiglobal.wikibroker.openapi.adapters.buildKtorInterceptor
import com.wikiglobal.wikibroker.openapi.common.enums.CustomHeaders
import com.wikiglobal.wikibroker.openapi.common.models.HttpRequestData
import kotlin.time.Clock
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

suspend fun sign(req: HttpRequestData, key: String) {
    val canonicalString = generateCanonicalString(req)
    val signature = generateSignature(key, canonicalString)
    req.setHeader(CustomHeaders.Signature.value, signature)
}

fun createOkHttpInterceptor(apiKey: String, apiSecret: String) =
    OkHttpInterceptor(
        Uuid.parse(apiKey),
        apiSecret,
        ::addXHeaders,
        ::sign,
        Clock.System::now,
        Uuid::random
    )

fun createKtorInterceptor(apiKey: String, apiSecret: String) =
    buildKtorInterceptor(
        Uuid.parse(apiKey),
        apiSecret,
        ::addXHeaders,
        ::sign,
        Clock.System::now,
        Uuid::random
    )