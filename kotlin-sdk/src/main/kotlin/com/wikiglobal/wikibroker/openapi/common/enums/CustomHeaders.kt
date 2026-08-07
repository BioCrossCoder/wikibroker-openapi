package com.wikiglobal.wikibroker.openapi.common.enums

enum class CustomHeaders(val value: String) {
    ApiKey("X-Api-Key"),
    Timestamp("X-Timestamp"),
    Nonce("X-Nonce"),
    Signature("X-Signature"),
}