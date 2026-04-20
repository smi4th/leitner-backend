package com.esgi.leitnerbackend.cards.domain.service

import com.esgi.leitnerbackend.cards.domain.model.Card
import com.esgi.leitnerbackend.cards.domain.port.input.*
import com.esgi.leitnerbackend.cards.domain.port.output.CardRepositoryPort
import com.esgi.leitnerbackend.cards.domain.port.output.CloudTaskPort
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class CardService(
  private val cardRepositoryPort: CardRepositoryPort, private val cloudTaskPort: CloudTaskPort
) : AnswerCardUseCase, GetAllCardsUseCase, GetQuizCardsUseCase, CreateCardUseCase, GetCardUseCase {
  override fun answerCard(answerCardCommand: AnswerCardCommand) {
    val flashcard: Card = this.getCard(answerCardCommand.cardId)

    val updatedCard = if (answerCardCommand.isValid) {
      flashcard.copy(category =  flashcard.category.next(), updatedAt = Instant.now())
    } else {
      flashcard.copy(category = Card.CardCategory.FIRST, updatedAt = Instant.now())
    }

    cardRepositoryPort.update(updatedCard)
  }

  override fun getCard(id: String): Card {
    return cardRepositoryPort.findById(id) ?: throw Exception("Card not found")
  }

  override fun getAllCards(tags: List<String>?): List<Card> {
    return cardRepositoryPort.findAll(tags)
  }

  override fun getQuizCards(date: String): List<Card> {
    val quizDate = LocalDate.parse(date)
    val allCards = cardRepositoryPort.findAll()

    val cardsCreatedBeforeQuiz = allCards.filter { card ->
      val createdAtDate = card.instantToLocaleDate(card.createdAt)
      createdAtDate != null && !createdAtDate.isAfter(quizDate)
    }

    val cardsAlreadyPlayed =  cardsCreatedBeforeQuiz.filter { card ->
      val updatedAtDate = card.instantToLocaleDate(card.updatedAt)
      val createdAtDate = card.instantToLocaleDate(card.createdAt)

      updatedAtDate != null && createdAtDate != null &&
      updatedAtDate.isEqual(quizDate) && !createdAtDate.isEqual(quizDate)
    }

    if(cardsAlreadyPlayed.isNotEmpty()) {
      return emptyList()
    }

    return cardsCreatedBeforeQuiz
      .filter { card -> card.category != Card.CardCategory.DONE }
      .filter { card ->
        val frequency = card.category.getFrequency()
        val lastAnswerDate = card.instantToLocaleDate(card.updatedAt)
          
        val daysSinceLastAnswer = ChronoUnit.DAYS.between(lastAnswerDate, quizDate)
        val isNotPlayedYet = Duration.between(card.createdAt, card.updatedAt).toMillis() < 10

        isNotPlayedYet || daysSinceLastAnswer >= frequency
      }
  }

  override fun createCard(createCardCommand: CreateCardCommand): Card {
    val card = Card(
      id = null,
      question = createCardCommand.question,
      answer = createCardCommand.answer,
      tag = createCardCommand.tag
    )

    val savedCard = cardRepositoryPort.save(card)
    cloudTaskPort.scheduleCardCreatedTask(savedCard)
    return savedCard
  }
}