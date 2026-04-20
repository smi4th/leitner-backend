package com.esgi.leitnerbackend.cards.adapter.output.cloudtask

import com.esgi.leitnerbackend.cards.domain.model.Card
import com.esgi.leitnerbackend.cards.domain.port.output.CloudTaskPort
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("local")
class NoOpCloudTaskAdapter : CloudTaskPort {

  private val logger = LoggerFactory.getLogger(NoOpCloudTaskAdapter::class.java)

  override fun scheduleCardCreatedTask(card: Card) {
    logger.info("[CloudTask - NoOp] Would schedule task for card id={} question={}", card.id, card.question)
  }
}
