package com.example.springai.service

import com.example.springai.config.SearchProperties
import com.example.springai.util.JapaneseCompanyNameValidator
import com.example.springai.web.dto.PurposeResult
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class CompanyService(
    private val nameValidator: JapaneseCompanyNameValidator,
    private val vectorStore: VectorStore,
    private val searchProps: SearchProperties
) {

    fun validateCompanyName(name: String): Map<String, Any> {
        val valid = nameValidator.isValid(name)
        val reasons = if (!valid) nameValidator.validateReasons(name) else emptyList<String>()
        return mapOf("companyName" to name, "valid" to valid, "reasons" to reasons)
    }

    fun searchSimilarPurposes(
        purposeText: String,
        topK: Int? = null,
        threshold: Double? = null
    ): List<PurposeResult> {
        val results = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(purposeText)
                .topK(topK ?: searchProps.defaultTopK)
                .similarityThreshold(threshold ?: searchProps.defaultThreshold)
                .build()
        )
        return results?.map { doc ->
            PurposeResult(
                id = doc.id ?: "",
                purposeText = doc.text ?: "",
                score = doc.score ?: 0.0
            )
        } ?: emptyList()
    }
}
