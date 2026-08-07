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

suspend fun serializeKtorRequestBuilderBody(data: Any): String = when (data) {
    is String -> data
    is ByteArray -> data.decodeToString()
    is ByteReadChannel -> data.toByteArray().decodeToString()
    is OutgoingContent.ByteArrayContent -> data.bytes().decodeToString()
    is OutgoingContent.NoContent -> "{}"
    is OutgoingContent.ReadChannelContent -> data.readFrom().toByteArray().decodeToString()
    else -> data.toString()
}

fun buildKtorInterceptor(
    apiKey: Uuid,
    apiSecret: String,
    loadHeaders: (HttpRequestData, Uuid, Instant, Uuid) -> Unit,
    sign: suspend (HttpRequestData, String) -> Unit,
    timestampGenerator: () -> Instant,
    idGenerator: () -> Uuid,
    serialize: suspend ((Any) -> String) = ::serializeKtorRequestBuilderBody,
): suspend Sender.(HttpRequestBuilder) -> HttpClientCall = { builder ->
    val data = HttpRequestData(builder.method.value, builder.url.toString(), serialize(builder.body))
    loadHeaders(data, apiKey, timestampGenerator(), idGenerator())
    sign(data, apiSecret)
    CustomHeaders.entries.forEach {
        builder.header(it.value, data.getHeader(it.value))
    }
    execute(builder)
}