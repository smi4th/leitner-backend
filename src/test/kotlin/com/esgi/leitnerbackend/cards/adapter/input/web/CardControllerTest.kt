package com.esgi.leitnerbackend.cards.adapter.input.web

import com.esgi.leitnerbackend.cards.domain.model.Card
import com.esgi.leitnerbackend.cards.domain.port.input.*
import com.esgi.leitnerbackend.cards.helper.TestsHelper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.hamcrest.Matchers.hasSize
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import java.time.Instant

@WebMvcTest(CardController::class)
class CardControllerTest {
  private val helper = TestsHelper()
  private val now: Instant = helper.now
  private val cardId: String = helper.cardId

  @Autowired
  private lateinit var mockMvc: MockMvc

  @MockitoBean
  private lateinit var createCardUseCase: CreateCardUseCase

  @MockitoBean
  private lateinit var getAllCardsUseCase: GetAllCardsUseCase

  @MockitoBean
  private lateinit var answerCardUseCase: AnswerCardUseCase

  @MockitoBean
  private lateinit var getQuizCardsUseCase: GetQuizCardsUseCase

  @MockitoBean
  private lateinit var getCardUseCase: GetCardUseCase

  fun createCard(
    category: Card.CardCategory = Card.CardCategory.FIRST,
    createdAt: Instant = now,
    updatedAt: Instant = now,
    tag: String = "Test"
  ): Card = helper.createCard(category, createdAt, updatedAt, tag)

  @Nested
  @DisplayName("createCard")
  inner class CreateCard {
    @Test
    @WithMockUser
    @DisplayName("Should return a 201 when creating a card")
    fun shouldCreateCard() {
      val card = createCard()

      Mockito.`when`(createCardUseCase.createCard(any())).thenReturn(card)

      val requestBody = """
      {
        "question": "New question ?",
        "answer": "New answer !",
        "tag": "Test"
      }
    """

      mockMvc.perform(
        post("/cards")
          .with(csrf())
          .contentType("application/json")
          .content(requestBody)
      )
        .andExpect(status().isCreated)
    }

    @Test
    @WithMockUser
    @DisplayName("Should return a 400 when creating a card with invalid body")
    fun shouldReturn400WhenCreatingCardWithInvalidBody() {
      val card = createCard()

      Mockito.`when`(createCardUseCase.createCard(any())).thenReturn(card)

      val requestBody = """
      {
        "question": "New question ?",
        "answer": "New answer !",
        "tasg": "Test"
      }
    """

      mockMvc.perform(
        post("/cards")
          .with(csrf())
          .contentType("application/json")
          .content(requestBody)
      )
        .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser
    @DisplayName("Should return a 400 when creating a card with empty question")
    fun shouldReturn400WhenCreatingCardWithEmptyQuestion() {
      val card = createCard()

      Mockito.`when`(createCardUseCase.createCard(any())).thenReturn(card)

      val requestBody = """
      {
        "question": "",
        "answer": "New answer !",
        "tag": "Test"
      }
    """

      mockMvc.perform(
        post("/cards")
          .with(csrf())
          .contentType("application/json")
          .content(requestBody)
      )
        .andExpect(status().isBadRequest)
    }
  }

  @Nested
  @DisplayName("getCards")
  inner class GetCards {
    @Test
    @WithMockUser
    @DisplayName("Should return a 200 and list of cards")
    fun shouldGetAllCards() {
      val card1 = createCard()
      val card2 = createCard()
      val cards = listOf(card1, card2)

      Mockito.`when`(getAllCardsUseCase.getAllCards(null)).thenReturn(cards)

      mockMvc.perform(get("/cards"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$", hasSize<Any>(2)))
        .andExpect(jsonPath("$[0].question").value(card1.question))
        .andExpect(jsonPath("$[1].question").value(card2.question))
    }

    @Test
    @WithMockUser
    @DisplayName("Should pass tags parameter to use case")
    fun shouldGetCardsWithTags() {
      val tags = listOf("Test", "Test2")
      val cards = listOf(createCard())

      Mockito.`when`(getAllCardsUseCase.getAllCards(tags)).thenReturn(cards)

      mockMvc.perform(
        get("/cards")
          .param("tags", "Test", "Test2")
      )
        .andExpect(status().isOk)
        .andExpect(jsonPath("$", hasSize<Any>(1)))

      Mockito.verify(getAllCardsUseCase).getAllCards(tags)
    }

    @Test
    @WithMockUser
    @DisplayName("Should return empty list when no cards found")
    fun shouldReturnEmptyListWhenNoCardsFound() {
      Mockito.`when`(getAllCardsUseCase.getAllCards(null)).thenReturn(emptyList())

      mockMvc.perform(get("/cards"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$", hasSize<Any>(0)))
    }
  }

  @Nested
  @DisplayName("getQuizCards")
  inner class GetQuizCards {
    @Test
    @WithMockUser
    @DisplayName("Should return 200 and cards for a specific date")
    fun shouldGetQuizCards() {
      val date = Instant.now().toString()
      val cards = listOf(createCard(), createCard())

      Mockito.`when`(getQuizCardsUseCase.getQuizCards(any())).thenReturn(cards)

      mockMvc.perform(get("/cards/quizz").param("date", date))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$", hasSize<Any>(2)))
    }

    @Test
    @WithMockUser
    @DisplayName("Should use current date by default when getting quiz cards")
    fun shouldGetQuizCardsWithDefaultDate() {
      val cards = listOf(createCard(), createCard())
      val today = Instant.now().toString().substring(0, 10)

      Mockito.`when`(getQuizCardsUseCase.getQuizCards(any())).thenReturn(cards)

      mockMvc.perform(get("/cards/quizz"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$", hasSize<Any>(2)))

      Mockito.verify(getQuizCardsUseCase).getQuizCards(today)
    }
  }

  @Nested
  @DisplayName("answerCard")
  inner class AnswerCard {
    @Test
    @WithMockUser
    @DisplayName("Should return 204 when answering a card correctly")
    fun shouldAnswerCard() {
      val requestBody = """
        {
          "isValid": true
        }
      """

      mockMvc.perform(
        patch("/cards/$cardId/answer")
          .with(csrf())
          .contentType("application/json")
          .content(requestBody)
      )
        .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when answering a card with invalid body")
    fun shouldReturn400WhenAnsweringCardWithInvalidBody() {
      val requestBody = """
        {
          "isValid": "TEST"  
        }
      """

      mockMvc.perform(
        patch("/cards/$cardId/answer")
          .with(csrf())
          .contentType("application/json")
          .content(requestBody)
      )
        .andExpect(status().isBadRequest)
    }
  }

  @Nested
  @DisplayName("getCardById")
  inner class GetCardById {
    @Test
    @WithMockUser
    @DisplayName("Should return 200 and the card details")
    fun shouldGetCardById() {
      val card = createCard()

      Mockito.`when`(getCardUseCase.getCard(cardId)).thenReturn(card)

      mockMvc.perform(get("/cards/$cardId"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.id").value(cardId))
        .andExpect(jsonPath("$.question").value(card.question))
        .andExpect(jsonPath("$.category").value(card.category.toString()))
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when the card does not exist")
    fun shouldReturn404WhenCardNotFound() {
      Mockito.`when`(getCardUseCase.getCard(cardId)).thenThrow(RuntimeException("Card not found"))

      mockMvc.perform(get("/cards/$cardId"))
        .andExpect(status().isNotFound)
    }
  }
}