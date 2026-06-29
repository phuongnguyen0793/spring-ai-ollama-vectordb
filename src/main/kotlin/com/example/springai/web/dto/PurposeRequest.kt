package com.example.springai.web.dto

data class PurposeRequest(
    val purpose: String,
    val topK: Int? = null,           // Defaults to 5 if not provided
    val threshold: Double? = null    // Defaults to 0.5 if not provided
)
