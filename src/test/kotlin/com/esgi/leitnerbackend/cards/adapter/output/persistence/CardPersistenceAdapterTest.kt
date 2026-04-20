package com.esgi.leitnerbackend.cards.adapter.output.persistence

import com.esgi.leitnerbackend.cards.helper.TestsHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(CardPersistenceAdapter::class, CardPersistenceMapper::class)
class CardPersistenceAdapterTest {

  @Autowired
  private lateinit var cardPersistenceAdapter: CardPersistenceAdapter

  @Autowired
  private lateinit var cardJpaRepository: CardJpaRepository

  private val helper = TestsHelper()

  @Test
  @DisplayName("should save card")
  fun shouldSaveCard() {
    // Matches Service behavior: ID is null initially
    val card = helper.createCard().copy(id = null)
    val savedCard = cardPersistenceAdapter.save(card)

    assertThat(savedCard.id).isNotNull
    assertThat(savedCard.question).isEqualTo(card.question)
    assertThat(savedCard.answer).isEqualTo(card.answer)
    assertThat(savedCard.category).isEqualTo(card.category)
  }

  @Test
  @DisplayName("should update card")
  fun shouldUpdateCard() {
    val card = helper.createCard().copy(id = null)
    val savedCard = cardPersistenceAdapter.save(card)

    val updatedCard = savedCard.copy(question = "Updated Question")
    val result = cardPersistenceAdapter.update(updatedCard)

    assertThat(result.id).isEqualTo(savedCard.id)
    assertThat(result.question).isEqualTo("Updated Question")

    val foundCard = cardPersistenceAdapter.findById(savedCard.id!!)
    assertThat(foundCard?.question).isEqualTo("Updated Question")
  }

  @Test
  @DisplayName("should find all cards")
  fun shouldFindAllCards() {
    val card1 = helper.createCard(tag = "tag1").copy(id = null)
    val card2 = helper.createCard(tag = "tag2").copy(id = null)

    cardPersistenceAdapter.save(card1)
    cardPersistenceAdapter.save(card2)

    val cards = cardPersistenceAdapter.findAll(null)

    assertThat(cards).hasSizeGreaterThanOrEqualTo(2)
  }

  @Test
  @DisplayName("should find all cards by tags")
  fun shouldFindAllCardsByTags() {
    val card1 = helper.createCard(tag = "tag1").copy(id = null)
    val card2 = helper.createCard(tag = "tag2").copy(id = null)
    val card3 = helper.createCard(tag = "tag3").copy(id = null)

    cardPersistenceAdapter.save(card1)
    cardPersistenceAdapter.save(card2)
    cardPersistenceAdapter.save(card3)

    val cards = cardPersistenceAdapter.findAll(listOf("tag1", "tag3"))

    assertThat(cards).hasSize(2)
    assertThat(cards).extracting("tag").containsExactlyInAnyOrder("tag1", "tag3")
  }

  @Test
  @DisplayName("should find no cards when tags list is empty")
  fun shouldFindNoCardsWhenTagsListIsEmpty() {
    val card1 = helper.createCard(tag = "tag1").copy(id = null)
    cardPersistenceAdapter.save(card1)

    val cards = cardPersistenceAdapter.findAll(emptyList())

    assertThat(cards).isEmpty()
  }

  @Test
  @DisplayName("should find no cards when no tags match")
  fun shouldFindNoCardsWhenNoTagsMatch() {
    val card1 = helper.createCard(tag = "tag1").copy(id = null)
    cardPersistenceAdapter.save(card1)

    val cards = cardPersistenceAdapter.findAll(listOf("unknown-tag"))

    assertThat(cards).isEmpty()
  }

  @Test
  @DisplayName("should find card by id")
  fun shouldFindCardById() {
    val card = helper.createCard().copy(id = null)
    val savedCard = cardPersistenceAdapter.save(card)

    val foundCard = cardPersistenceAdapter.findById(savedCard.id!!)

    assertThat(foundCard).isNotNull
    assertThat(foundCard?.id).isEqualTo(savedCard.id)
  }

  @Test
  @DisplayName("should return null when card not found")
  fun shouldReturnNullWhenCardNotFound() {
    val foundCard = cardPersistenceAdapter.findById("non-existent-id")
    assertThat(foundCard).isNull()
  }
}
