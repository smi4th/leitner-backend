package com.esgi.leitnerbackend.cards.domain.port.input

import com.esgi.leitnerbackend.cards.domain.model.Card

interface GetQuizCardsUseCase {
  fun getQuizCards(
    date: String,
  ): List<Card>
}