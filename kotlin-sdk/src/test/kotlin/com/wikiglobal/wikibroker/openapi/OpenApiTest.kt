package com.wikiglobal.wikibroker.openapi

import com.wikiglobal.wikibroker.openapi.common.enums.CustomHeaders
import com.wikiglobal.wikibroker.openapi.common.models.HttpRequestData
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

class OpenApiTest {
    companion object {
        val baseURL = "https://api.example.com"
        val path = "test?q1=c&q2=b&q1=a"
        val url: String
            get() = "$baseURL/$path"
        val body = mapOf("key" to "value")
        val method = "POST"
        val apiKey = Uuid.parse("ef05e5b0-9daf-49e3-a0f4-9a3c13f55c3b")
        val apiSecret = "4ae4bf20-0afa-4122-ade8-c0beca7bd5e4"
        val nonce = Uuid.parse("4428a206-1afd-4b15-a98d-43e91f49a08d")
        val timestamp = Instant.fromEpochMilliseconds(1798115622000)
        val expectedSignature = "1b0c80dbbc30905719559ab5526dfd59bae04d7337c8843efd9e51ff0af6dfb4"
    }

    @Test
    fun testSign() {
        val req = HttpRequestData(method, url)
        req.setBody(body, Json::encodeToString)
        addXHeaders(req, apiKey, timestamp, nonce)
        sign(req, apiSecret)
        assertEquals(expectedSignature, req.getHeader(CustomHeaders.Signature.value))
    }
}