package com.esgi.leitnerbackend.cards.domain.service

import com.esgi.leitnerbackend.cards.domain.model.Card
import com.esgi.leitnerbackend.cards.domain.port.input.CreateCardCommand
import com.esgi.leitnerbackend.cards.domain.port.output.CardRepositoryPort
import com.esgi.leitnerbackend.cards.domain.port.output.CloudTaskPort
import com.esgi.leitnerbackend.cards.helper.TestsHelper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.check
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExtendWith(MockitoExtension::class)
class CardServiceTest {
  @Mock
  private lateinit var cardRepositoryPort: CardRepositoryPort

  @Mock
  private lateinit var cloudTaskPort: CloudTaskPort

  @InjectMocks
  private lateinit var cardService: CardService

  private val helper = TestsHelper()
  private val now: Instant = helper.now
  private val cardId: String = helper.cardId

  fun createCardCommand(): CreateCardCommand = helper.createCardCommand()

  fun createCard(
    category: Card.CardCategory = Card.CardCategory.FIRST,
    createdAt: Instant = now,
    updatedAt: Instant = now
  ): Card = helper.createCard(category, createdAt, updatedAt)

  fun answerCardCommand(isValid: Boolean) = helper.answerCardCommand(isValid)

  @Nested
  @DisplayName("createCard")
  inner class CreateCard {
    @Test
    @DisplayName("should create a card")
    fun shouldCreateCard() {
      val createCardCommand = createCardCommand()
      cardService.createCard(createCardCommand)

      Mockito.verify(cardRepositoryPort, Mockito.times(1)).save(
        check { card ->
          assert(card.question == createCardCommand.question)
          assert(card.answer == createCardCommand.answer)
          assert(card.tag == createCardCommand.tag)
          assert(card.category == Card.CardCategory.FIRST)
        }
      )
    }
  }

  @Nested
  @DisplayName("getCard")
  inner class GetCard {
    @Test
    @DisplayName("should get a card by id")
    fun shouldGetCardById() {
      val card = createCard()

      Mockito.`when`(cardRepositoryPort.findById(cardId)).thenReturn(card)

      val fetchedCard = cardService.getCard(cardId)
      assert(fetchedCard == card)
    }

    @Test
    @DisplayName("should throw exception when card not found")
    fun shouldThrowExceptionWhenCardNotFound() {
      Mockito.`when`(cardRepositoryPort.findById(cardId)).thenReturn(null)

      try {
        cardService.getCard(cardId)
        assert(false)
      } catch (e: Exception) {
        assert(e.message == "Card not found")
      }
    }
  }

  @Nested
  @DisplayName("getAllCards")
  inner class GetAllCards {
    @Test
    @DisplayName("should return all cards")
    fun shouldReturnAllCards() {
      val cards = listOf(createCard(), createCard())
      Mockito.`when`(cardRepositoryPort.findAll(null)).thenReturn(cards)

      val fetchedCards = cardService.getAllCards(null)
      assert(fetchedCards == cards)
    }
  }

  @Nested
  @DisplayName("answerCard")
  inner class AnswerCard {
    @Test
    @DisplayName("should answer correctly to a card and update its category")
    fun shouldAnswerCorrectlyToCardAndUpdateCategory() {
      val card = createCard(
        category = Card.CardCategory.SECOND,
        createdAt = now.minus(5, ChronoUnit.DAYS),
        updatedAt = now.minus(5, ChronoUnit.DAYS)
      )

      val answerCardCommand = answerCardCommand(isValid = true)

      Mockito.`when`(cardRepositoryPort.findById(cardId)).thenReturn(card)
      cardService.answerCard(answerCardCommand)

      Mockito.verify(cardRepositoryPort, Mockito.times(1)).update(
        check { updatedCard ->
          assert(updatedCard.category == Card.CardCategory.THIRD)
          assert(updatedCard.updatedAt!!.isAfter(card.updatedAt))
        }
      )
    }

    @Test
    @DisplayName("should answer incorrectly to a card and reset its category to FIRST")
    fun shouldAnswerIncorrectlyToCardAndResetCategoryToFirst() {
      val card = createCard(
        category = Card.CardCategory.SECOND,
        createdAt = now.minus(5, ChronoUnit.DAYS),
        updatedAt = now.minus(5, ChronoUnit.DAYS)
      )

      val answerCardCommand = answerCardCommand(isValid = false)

      Mockito.`when`(cardRepositoryPort.findById(cardId)).thenReturn(card)
      cardService.answerCard(answerCardCommand)

      Mockito.verify(cardRepositoryPort, Mockito.times(1)).update(
        check { updatedCard ->
          assert(updatedCard.category == Card.CardCategory.FIRST)
          assert(updatedCard.updatedAt!!.isAfter(card.updatedAt))
        }
      )
    }

    @Test
    @DisplayName("should not advance category beyond DONE when answering correctly")
    fun shouldNotAdvanceCategoryBeyondDoneWhenAnsweringCorrectly() {
      val card = createCard(
        category = Card.CardCategory.DONE
      )

      val answerCardCommand = answerCardCommand(isValid = true)

      Mockito.`when`(cardRepositoryPort.findById(cardId)).thenReturn(card)
      cardService.answerCard(answerCardCommand)

      Mockito.verify(cardRepositoryPort, Mockito.times(1)).update(
        check { updatedCard ->
          assert(updatedCard.category == Card.CardCategory.DONE)
        }
      )
    }
  }

  @Nested
  @DisplayName("getQuizCards")
  inner class GetQuizCards {

    @Test
    @DisplayName("should return quiz cards if it is due for quiz")
    fun shouldReturnQuizCardsIfDueForQuiz() {
      val card = createCard(
        createdAt = now.minus(1, ChronoUnit.DAYS),
        updatedAt = now.minus(1, ChronoUnit.DAYS)
      )

      Mockito.`when`(cardRepositoryPort.findAll()).thenReturn(listOf(card))

      val quizDate = now.minus(1, ChronoUnit.DAYS).toString().substring(0, 10)
      val quizCards = cardService.getQuizCards(quizDate)

      assert(quizCards.size == 1)
      assert(quizCards[0] == card)
    }

    @Test
    @DisplayName("should not return quiz cards if not due for quiz")
    fun shouldNotReturnQuizCardsIfNotDueForQuiz() {
      val card = createCard(
        category = Card.CardCategory.THIRD,
        createdAt = now.minus(10, ChronoUnit.DAYS),
        updatedAt = now.minus(1, ChronoUnit.DAYS)
      )

      Mockito.`when`(cardRepositoryPort.findAll()).thenReturn(listOf(card))

      val quizDate = now.toString().substring(0, 10)
      val quizCards = cardService.getQuizCards(quizDate)

      assert(quizCards.isEmpty())
    }

    @Test
    @DisplayName("should not return quiz cards if already played today")
    fun shouldNotReturnQuizCardsIfAlreadyPlayedToday() {
      val card = createCard(
        createdAt = now.minus(2, ChronoUnit.DAYS),
        updatedAt = now.minus(1, ChronoUnit.DAYS)
      )

      Mockito.`when`(cardRepositoryPort.findAll()).thenReturn(listOf(card))

      val quizDate = now.minus(1, ChronoUnit.DAYS).toString().substring(0, 10)
      val quizCards = cardService.getQuizCards(quizDate)

      assert(quizCards.isEmpty())
    }

    @Test
    @DisplayName("should return quiz cards that were created today and not played yet")
    fun shouldReturnQuizCardsCreatedTodayAndNotPlayedYet() {
      val card = createCard()

      Mockito.`when`(cardRepositoryPort.findAll()).thenReturn(listOf(card))

      val quizDate = now.toString().substring(0, 10)
      val quizCards = cardService.getQuizCards(quizDate)

      assert(quizCards.size == 1)
      assert(quizCards[0] == card)
    }
  }
}