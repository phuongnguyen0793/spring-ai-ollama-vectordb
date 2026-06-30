package com.example.springai.config

import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class StartupEmbeddingInitializer(private val vectorStore: VectorStore) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val samplePurposes = listOf(
        "Provide cloud-native payments infrastructure",
        "Develop AI-powered healthcare diagnostics",
        "Offer e-commerce platform for small retailers",
        "Create mobile games for casual players",
        "Consulting for digital transformation"
    )

    override fun run(vararg args: String?) {
        val probe = vectorStore.similaritySearch(
            SearchRequest.builder().query("cloud payments").topK(1).similarityThreshold(0.0).build()
        )
        if (!probe.isNullOrEmpty()) {
            logger.info("Vector store already seeded — skipping initialization")
            return
        }
        logger.info("Seeding vector store with ${samplePurposes.size} sample purposes...")
        val documents = samplePurposes.map { purpose -> Document(purpose) }
        vectorStore.add(documents)
        logger.info("Vector store seeded successfully")
    }
}
