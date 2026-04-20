package com.esgi.leitnerbackend.cards.domain.port.input

import com.esgi.leitnerbackend.cards.domain.model.Card

interface GetCardUseCase {
  fun getCard(id: String): Card
}