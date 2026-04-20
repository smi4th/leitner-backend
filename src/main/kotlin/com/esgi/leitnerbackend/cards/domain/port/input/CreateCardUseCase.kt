package com.esgi.leitnerbackend.cards.domain.port.input

import com.esgi.leitnerbackend.cards.domain.model.Card

interface CreateCardUseCase {
  fun createCard(
    createCardCommand: CreateCardCommand
  ): Card
}