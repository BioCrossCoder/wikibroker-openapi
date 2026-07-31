package com.wikiglobal.wikibroker.openapi.common.interfaces

interface RequestBuilder<T> {
    fun header(name: String, value: String): RequestBuilder<T>
    fun method(method: String): RequestBuilder<T>
    fun url(url: String): RequestBuilder<T>
    fun body(body: String): RequestBuilder<T>
    fun <U> body(body: U, serialize: (U) -> String): RequestBuilder<T>
    fun build(): T
}