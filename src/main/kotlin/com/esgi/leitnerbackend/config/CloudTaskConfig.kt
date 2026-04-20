package com.esgi.leitnerbackend.config

import com.google.cloud.tasks.v2.CloudTasksClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!local")
class CloudTaskConfig {

  @Bean
  fun cloudTasksClient(): CloudTasksClient = CloudTasksClient.create()
}
