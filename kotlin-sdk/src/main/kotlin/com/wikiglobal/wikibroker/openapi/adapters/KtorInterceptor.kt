package com.wikiglobal.wikibroker.openapi.adapters

import com.wikiglobal.wikibroker.openapi.common.enums.CustomHeaders
import com.wikiglobal.wikibroker.openapi.common.models.HttpRequestData
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.Sender
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.toByteArray
import kotlin.time.Instant
import kotlin.uuid.Uuid

fun buildKtorInterceptor(
    apiKey: Uuid,
    apiSecret: String,
    loadHeaders: (HttpRequestData, Uuid, Instant, Uuid) -> Unit,
    sign: (HttpRequestData, String) -> Unit,
    timestampGenerator: () -> Instant,
    idGenerator: () -> Uuid,
): suspend Sender.(HttpRequestBuilder) -> HttpClientCall = { builder ->
    val body = when (val content = builder.body) {
        is String -> content
        is ByteArray -> content.decodeToString()
        is ByteReadChannel -> content.toByteArray().decodeToString()
        is OutgoingContent.ByteArrayContent -> content.bytes().decodeToString()
        is OutgoingContent.NoContent -> "{}"
        is OutgoingContent.ReadChannelContent -> content.readFrom().toByteArray().decodeToString()
        else -> content.toString()
    }
    val data = HttpRequestData(builder.method.value, builder.url.toString(), body)
    loadHeaders(data, apiKey, timestampGenerator(), idGenerator())
    sign(data, apiSecret)
    CustomHeaders.entries.forEach {
        builder.header(it.value, data.getHeader(it.value))
    }
    execute(builder)
}