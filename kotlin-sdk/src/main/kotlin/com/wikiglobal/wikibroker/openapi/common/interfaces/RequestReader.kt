package com.wikiglobal.wikibroker.openapi.common.interfaces

interface RequestReader {
    fun header(name: String): String
    val method: String
    val url: String
    val body: String
}