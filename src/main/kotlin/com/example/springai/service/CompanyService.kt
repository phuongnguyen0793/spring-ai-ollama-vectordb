package com.example.springai.service

import com.example.springai.util.JapaneseCompanyNameValidator
import com.example.springai.web.dto.PurposeResult
import com.example.springai.integration.OllamaClient
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service

@Service
class CompanyService(
    private val nameValidator: JapaneseCompanyNameValidator,
    private val ollamaClient: OllamaClient,
    private val jdbc: JdbcTemplate
) {

    fun validateCompanyName(name: String): Map<String, Any> {
        val valid = nameValidator.isValid(name)
        val reasons = if (!valid) nameValidator.validateReasons(name) else emptyList<String>()
        return mapOf("companyName" to name, "valid" to valid, "reasons" to reasons)
    }

    /**
     * Search for similar purposes using vector embeddings.
     * 
     * @param purposeText The text to search for
     * @param topK Maximum number of results to return (default: 5)
     * @param threshold Maximum distance to include in results (default: 0.5)
     *                  Lower values = more similar. Range: 0.0 (identical) to 2.0 (completely different)
     * @return List of similar purposes sorted by distance (most similar first)
     */
    fun searchSimilarPurposes(
        purposeText: String,
        topK: Int = 5,
        threshold: Double = 0.5
    ): List<PurposeResult> {
        val embedding = ollamaClient.embed(purposeText)
        // convert float array to comma separated string
        val vecStr = embedding.joinToString(",")
        
        // pgvector similarity: '<->' operator returns L2 distance
        // Distance values: 0.0 = identical, larger values = more different
        val sql = """
            SELECT id, purpose_text, (embedding <-> ('[${vecStr}]')::vector) as distance 
            FROM purposes 
            WHERE embedding <-> ('[${vecStr}]')::vector <= ? 
            ORDER BY distance ASC 
            LIMIT ?
        """.trimIndent()
        
        val mapper = RowMapper { rs, _ -> 
            PurposeResult(
                rs.getLong("id"), 
                rs.getString("purpose_text"), 
                rs.getDouble("distance")
            ) 
        }
        
        return jdbc.query(sql, arrayOf(threshold, topK), mapper)
    }
}
