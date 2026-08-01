package com.wikiglobal.wikibroker.openapi

import com.wikiglobal.wikibroker.openapi.adapters.OkHttpInterceptor
import com.wikiglobal.wikibroker.openapi.adapters.buildKtorInterceptor
import com.wikiglobal.wikibroker.openapi.common.enums.CustomHeaders
import com.wikiglobal.wikibroker.openapi.common.models.HttpRequestData
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
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


    @Test
    fun testOkHttp() {
        val interceptor =
            OkHttpInterceptor(
                apiKey,
                apiSecret,
                ::addXHeaders,
                ::sign,
                { timestamp },
                { nonce },
            )
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(1, TimeUnit.MILLISECONDS)
        val body = Json.encodeToString(body).toRequestBody("application/json".toMediaType())
        val req = Request.Builder().url(url).method(method, body).build()
        try {
            clientBuilder
                .addInterceptor { chain ->
                    val req = chain.request()
                    val actualSignature = req.header(CustomHeaders.Signature.value) ?: ""
                    assertEquals(expectedSignature, actualSignature)
                    chain.proceed(req)
                }
                .build()
                .newCall(req)
                .execute()
        } catch (e: Exception) {
            println(e)
        }
    }

    @Test
    fun testKtor() = runTest {
        val engine = MockEngine { request ->
            val actualSignature = request.headers[CustomHeaders.Signature.value] ?: ""
            assertEquals(expectedSignature, actualSignature)
            respond("OK", HttpStatusCode.OK)
        }
        val client = HttpClient(engine)
        val interceptor = buildKtorInterceptor(
            apiKey,
            apiSecret,
            ::addXHeaders,
            ::sign,
            { timestamp },
            { nonce }
        )
        client.plugin(HttpSend).intercept(interceptor)
        try {
            val body = Json.encodeToString(body)
            client.request {
                url(OpenApiTest.url)
                method = HttpMethod.parse(OpenApiTest.method)
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        } catch (e: Exception) {
            println(e)
        }
    }
}