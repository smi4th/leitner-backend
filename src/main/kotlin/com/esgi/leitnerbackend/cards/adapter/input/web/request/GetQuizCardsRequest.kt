package com.esgi.leitnerbackend.cards.adapter.input.web.request

import jakarta.validation.constraints.Pattern
import java.time.LocalDate

data class GetQuizCardsRequest(
  @field:Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date format must match YYYY-MM-DD")
  val date: String = LocalDate.now().toString()
)