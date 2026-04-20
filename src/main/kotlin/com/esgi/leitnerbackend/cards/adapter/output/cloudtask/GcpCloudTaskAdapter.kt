package com.esgi.leitnerbackend.cards.adapter.output.cloudtask

import com.esgi.leitnerbackend.cards.domain.model.Card
import com.esgi.leitnerbackend.cards.domain.port.output.CloudTaskPort
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.tasks.v2.CloudTasksClient
import com.google.cloud.tasks.v2.HttpMethod
import com.google.cloud.tasks.v2.HttpRequest
import com.google.cloud.tasks.v2.OidcToken
import com.google.cloud.tasks.v2.QueueName
import com.google.cloud.tasks.v2.Task
import com.google.protobuf.ByteString
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!local")
class GcpCloudTaskAdapter(
  private val cloudTasksClient: CloudTasksClient,
  private val objectMapper: ObjectMapper,
  @Value("\${app.cloud-tasks.project-id}") private val projectId: String,
  @Value("\${app.cloud-tasks.location}") private val location: String,
  @Value("\${app.cloud-tasks.queue}") private val queue: String,
  @Value("\${app.cloud-tasks.function-url}") private val functionUrl: String,
  @Value("\${app.cloud-tasks.service-account}") private val serviceAccount: String,
) : CloudTaskPort {

  private val logger = LoggerFactory.getLogger(GcpCloudTaskAdapter::class.java)

  override fun scheduleCardCreatedTask(card: Card) {
    val queuePath = QueueName.of(projectId, location, queue).toString()
    val payload = objectMapper.writeValueAsString(
      mapOf(
        "id" to card.id,
        "question" to card.question,
        "answer" to card.answer,
        "tag" to card.tag,
        "category" to card.category.name,
      )
    )

    val oidcToken = OidcToken.newBuilder()
      .setServiceAccountEmail(serviceAccount)
      .setAudience(functionUrl)
      .build()

    val task = Task.newBuilder().setHttpRequest(
        HttpRequest.newBuilder().setUrl(functionUrl).setHttpMethod(HttpMethod.POST)
          .setBody(ByteString.copyFromUtf8(payload)).putHeaders("Content-Type", "application/json")
          .setOidcToken(oidcToken).build()
      ).build()

    val createdTask = cloudTasksClient.createTask(queuePath, task)
    logger.info("Cloud Task created: {}", createdTask.name)
  }
}
