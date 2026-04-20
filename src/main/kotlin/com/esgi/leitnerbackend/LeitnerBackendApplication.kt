package com.esgi.leitnerbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LeitnerBackendApplication

fun main(args: Array<String>) {
  runApplication<LeitnerBackendApplication>(*args)
}
