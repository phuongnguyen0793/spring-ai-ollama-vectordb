package com.example.springai.controller

import com.example.springai.service.CompanyService
import com.example.springai.web.dto.CompanyNameRequest
import com.example.springai.web.dto.PurposeRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class CompanyController(private val companyService: CompanyService) {

    @PostMapping("/validate-name")
    fun validateName(@RequestBody req: CompanyNameRequest): ResponseEntity<Any> {
        val result = companyService.validateCompanyName(req.companyName)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/search-purpose")
    fun searchPurpose(@RequestBody req: PurposeRequest): ResponseEntity<Any> {
        val results = companyService.searchSimilarPurposes(
            purposeText = req.purpose,
            topK = req.topK,
            threshold = req.threshold
        )
        return ResponseEntity.ok(
            mapOf(
                "query" to req.purpose,
                "topK" to req.topK,
                "threshold" to req.threshold,
                "results" to results,
                "count" to results.size
            )
        )
    }
}
