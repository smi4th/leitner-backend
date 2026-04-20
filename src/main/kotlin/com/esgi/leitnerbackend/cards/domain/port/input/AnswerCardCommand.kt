package com.esgi.leitnerbackend.cards.domain.port.input

data class AnswerCardCommand(
  val cardId: String,
  val isValid: Boolean
)
