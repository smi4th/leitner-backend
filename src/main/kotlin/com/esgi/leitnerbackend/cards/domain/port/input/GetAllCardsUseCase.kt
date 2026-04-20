package com.esgi.leitnerbackend.cards.domain.port.input

import com.esgi.leitnerbackend.cards.domain.model.Card

interface GetAllCardsUseCase {
  fun getAllCards(
    tags: List<String>?,
  ): List<Card>
}