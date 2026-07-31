package com.wikiglobal.wikibroker.openapi

import com.wikiglobal.wikibroker.openapi.common.Hash
import com.wikiglobal.wikibroker.openapi.common.enums.CustomHeaders
import com.wikiglobal.wikibroker.openapi.common.interfaces.RequestReader
import java.net.URI

fun generateSignature(key: String, message: String): String =
    Hash.hmacSha256(
        key.toByteArray(Charsets.UTF_8),
        message.toByteArray(Charsets.UTF_8)
    ).toHexString()


fun generateCanonicalString(req: RequestReader): String {
    val method = req.method.uppercase()
    val path = URI(req.url).toURL().path
    val canonicalQuery = buildCanonicalQuery(req)
    val apiKey = req.header(CustomHeaders.ApiKey.value)
    val timestamp = req.header(CustomHeaders.Timestamp.value)
    val nonce = req.header(CustomHeaders.Nonce.value)
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

private fun calculateBodyHash(req: RequestReader): String {
    val body = if (req.method.uppercase() == "POST") req.body else ""
    return Hash.sha256Hash(body.toByteArray(Charsets.UTF_8)).toHexString()
}

private fun buildCanonicalQuery(req: RequestReader): String {
    val queryString = URI(req.url).toURL().query ?: ""
    return queryString.split("&")
        .map { it.split("=") }
        .filter { it.size == 2 }
        .sortedWith(compareBy({ it[0] }, { it[1] }))
        .joinToString("&") { "${it[0]}=${it[1]}" }
}
