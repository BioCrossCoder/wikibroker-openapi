package com.wikiglobal.wikibroker.openapi.common.models

data class HttpRequestData(
    var method: String,
    var url: String,
    var body: String = "",
) {
    fun <T> setBody(body: T, serialize: (T) -> String) {
        this.body = serialize(body)
    }

    private val headers = mutableMapOf<String, String>()

    fun setHeader(name: String, value: String) {
        headers[name] = value
    }

    fun getHeader(name: String) = headers.getOrDefault(name, "")

}
