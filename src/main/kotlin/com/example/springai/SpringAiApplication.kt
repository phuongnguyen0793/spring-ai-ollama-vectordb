package com.example.springai

import com.example.springai.config.SearchProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(SearchProperties::class)
class SpringAiApplication

fun main(args: Array<String>) {
    runApplication<SpringAiApplication>(*args)
}
