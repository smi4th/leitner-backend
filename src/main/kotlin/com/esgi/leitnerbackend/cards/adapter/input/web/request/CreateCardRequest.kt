package com.esgi.leitnerbackend.cards.adapter.input.web.request

import com.esgi.leitnerbackend.cards.domain.port.input.CreateCardCommand
import jakarta.validation.constraints.NotBlank

data class CreateCardRequest(
  @field:NotBlank(message = "Question is mandatory")
  val question: String,

  @field:NotBlank(message = "Answer is mandatory")
  val answer: String,

  @field:NotBlank(message = "Tag is mandatory")
  val tag: String
) {
  fun toUseCase(): CreateCardCommand = CreateCardCommand(
    question = question,
    answer = answer,
    tag = tag
  )
}