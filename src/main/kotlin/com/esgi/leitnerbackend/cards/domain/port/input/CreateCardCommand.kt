package com.esgi.leitnerbackend.cards.domain.port.input

data class CreateCardCommand(
  val question: String,
  val answer: String,
  val tag: String
)
