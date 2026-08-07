package com.wikiglobal.wikibroker.openapi

import com.wikiglobal.wikibroker.openapi.common.Hash
import com.wikiglobal.wikibroker.openapi.common.enums.CustomHeaders
import com.wikiglobal.wikibroker.openapi.common.models.HttpRequestData
import io.ktor.http.Url

suspend fun generateSignature(key: String, message: String) =
    Hash.hmacSha256(
        key.toByteArray(Charsets.UTF_8),
        message.toByteArray(Charsets.UTF_8)
    ).toHexString()

suspend fun generateCanonicalString(req: HttpRequestData): String {
    val method = req.method.uppercase()
    val url = Url(req.url)
    val path = url.encodedPath
    val canonicalQuery = buildCanonicalQuery(url)
    val apiKey = req.getHeader(CustomHeaders.ApiKey.value)
    val timestamp = req.getHeader(CustomHeaders.Timestamp.value)
    val nonce = req.getHeader(CustomHeaders.Nonce.value)
    val bodyHash = calculateBodyHash(req)
    return listOf(
        method,
        path,
        canonicalQuery,
        apiKey,
        timestamp,
        nonce,
        bodyHash,
    ).joinToString("\n")
}

private suspend fun calculateBodyHash(req: HttpRequestData): String {
    val body = if (req.method.uppercase() == "POST") req.body else ""
    return Hash.sha256Hash(body.toByteArray(Charsets.UTF_8)).toHexString()
}

private fun buildCanonicalQuery(url: Url): String {
    return url.encodedQuery.split("&")
        .map { it.split("=") }
        .filter { it.size == 2 }
        .sortedWith(compareBy({ it[0] }, { it[1] }))
        .joinToString("&") { "${it[0]}=${it[1]}" }
}
