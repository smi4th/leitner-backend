package com.esgi.leitnerbackend.cards.adapter.output.persistence

import com.esgi.leitnerbackend.cards.domain.model.Card
import com.esgi.leitnerbackend.cards.domain.port.output.CardRepositoryPort
import org.springframework.stereotype.Component

@Component
class CardPersistenceAdapter(
  private val cardJpaRepository: CardJpaRepository,
  private val cardPersistenceMapper: CardPersistenceMapper
) : CardRepositoryPort {

  override fun save(card: Card): Card {
    val entity = cardPersistenceMapper.toEntity(card)
    val savedEntity = cardJpaRepository.save(entity)
    return cardPersistenceMapper.toDomain(savedEntity)
  }

  override fun findAll(tags: List<String>?): List<Card> {
    val entities =
      if (tags != null) {
        cardJpaRepository.findAllByTagIn(tags)
      } else {
        cardJpaRepository.findAll()
      }
    return entities.map { cardPersistenceMapper.toDomain(it) }
  }

  override fun findById(id: String): Card? {
    return cardJpaRepository.findById(id).map { cardPersistenceMapper.toDomain(it) }.orElse(null)
  }

  override fun update(card: Card): Card {
    return save(card)
  }
}
