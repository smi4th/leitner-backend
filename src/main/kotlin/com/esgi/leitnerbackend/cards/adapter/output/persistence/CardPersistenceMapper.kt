package com.esgi.leitnerbackend.cards.adapter.output.persistence

import com.esgi.leitnerbackend.cards.domain.model.Card
import org.springframework.stereotype.Component

@Component
class CardPersistenceMapper {
  fun toEntity(domain: Card): CardEntity {
    return CardEntity(
      id = domain.id,
      category = domain.category,
      question = domain.question,
      answer = domain.answer,
      tag = domain.tag,
      createdAt = domain.createdAt,
      updatedAt = domain.updatedAt
    )
  }

  fun toDomain(entity: CardEntity): Card {
    return Card(
      id = entity.id,
      category = entity.category,
      question = entity.question,
      answer = entity.answer,
      tag = entity.tag,
      createdAt = entity.createdAt,
      updatedAt = entity.updatedAt
    )
  }
}
