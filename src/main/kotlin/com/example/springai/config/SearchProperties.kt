package com.example.springai.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.search")
data class SearchProperties(
    val defaultTopK: Int = 5,
    val defaultThreshold: Double = 0.5
)
