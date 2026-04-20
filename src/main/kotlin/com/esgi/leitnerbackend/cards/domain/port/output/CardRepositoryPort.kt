package com.esgi.leitnerbackend.cards.domain.port.output

import com.esgi.leitnerbackend.cards.domain.model.Card

interface CardRepositoryPort {
  fun save(card: Card): Card
  fun findAll(tags: List<String>? = null): List<Card>
  fun findById(id: String): Card?
  fun update(card: Card): Card
}