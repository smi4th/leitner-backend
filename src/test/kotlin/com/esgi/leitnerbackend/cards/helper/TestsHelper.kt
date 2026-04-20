package com.esgi.leitnerbackend.cards.helper

import com.esgi.leitnerbackend.cards.domain.model.Card
import com.esgi.leitnerbackend.cards.domain.port.input.AnswerCardCommand
import com.esgi.leitnerbackend.cards.domain.port.input.CreateCardCommand
import java.time.Instant

class TestsHelper {
  val now = Instant.now()
  val cardId = "test-card-id"

  // Helpers that can be used everywhere
  fun createCardCommand(): CreateCardCommand {
    return CreateCardCommand(
      question = "Question?",
      answer = "Answer.",
      tag = "Test"
    )
  }

  fun createCard(
    category: Card.CardCategory = Card.CardCategory.FIRST,
    createdAt: Instant = now,
    updatedAt: Instant = now,
    tag: String = "Test"
  ): Card {
    return Card(
      id = cardId,
      question = "Question?",
      answer = "Answer.",
      tag = tag,
      category = category,
      createdAt = createdAt,
      updatedAt = updatedAt
    )
  }

  fun answerCardCommand(isValid: Boolean) = AnswerCardCommand(
    cardId = cardId,
    isValid = isValid
  )

}