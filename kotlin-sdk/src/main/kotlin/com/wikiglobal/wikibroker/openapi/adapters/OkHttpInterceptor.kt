package com.wikiglobal.wikibroker.openapi.adapters

import com.wikiglobal.wikibroker.openapi.common.enums.CustomHeaders
import com.wikiglobal.wikibroker.openapi.common.models.HttpRequestData
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import kotlin.time.Instant
import kotlin.uuid.Uuid

class OkHttpInterceptor(
    val apiKey: Uuid,
    val apiSecret: String,
    val loadHeaders: (HttpRequestData, Uuid, Instant, Uuid) -> Unit,
    val sign: suspend (HttpRequestData, String) -> Unit,
    val timestampGenerator: () -> Instant,
    val idGenerator: () -> Uuid,
) : Interceptor {
    companion object {
        private fun readRequestBody(body: RequestBody?): String {
            val buf = okio.Buffer()
            body?.writeTo(buf)
            return buf.readUtf8()
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val data = HttpRequestData(req.method, req.url.toString(), readRequestBody(req.body))
        this.loadHeaders(data, this.apiKey, this.timestampGenerator(), this.idGenerator())
        val self = this
        runBlocking { self.sign(data, self.apiSecret) }
        val reqBuilder = req.newBuilder()
        CustomHeaders.entries.forEach {
            reqBuilder.addHeader(it.value, data.getHeader(it.value))
        }
        return chain.proceed(reqBuilder.build())
    }
}