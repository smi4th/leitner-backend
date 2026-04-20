package com.esgi.leitnerbackend.cards.adapter.input.web

import com.esgi.leitnerbackend.cards.adapter.input.web.request.AnswerCardRequest
import com.esgi.leitnerbackend.cards.adapter.input.web.request.CreateCardRequest
import com.esgi.leitnerbackend.cards.adapter.input.web.response.CardResponse
import com.esgi.leitnerbackend.cards.domain.model.Card
import com.esgi.leitnerbackend.cards.domain.port.input.*
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/cards")
class CardController(
  private val createCardUseCase: CreateCardUseCase,
  private val getAllCardsUseCase: GetAllCardsUseCase,
  private val getQuizCardsUseCase: GetQuizCardsUseCase,
  private val answerCardUseCase: AnswerCardUseCase,
  private val getCardUseCase: GetCardUseCase
) {

  @GetMapping
  fun getCards(
    @RequestParam(required = false) tags: List<String>?,
  ): ResponseEntity<List<CardResponse>> {
    val cards = getAllCardsUseCase.getAllCards(
      tags
    )
    return ResponseEntity.ok(cards.map {
      it.toResponse()
    })
  }

  @GetMapping("/{cardId}")
  fun getCard(@PathVariable cardId: String): ResponseEntity<CardResponse> {
    return try {
      val card = getCardUseCase.getCard(cardId)
      ResponseEntity.ok(card.toResponse())
    } catch (e: Exception) {
      ResponseEntity.notFound().build()
    }
  }

  @PostMapping
  fun createCard(
    @RequestBody @Valid request: CreateCardRequest
  ): ResponseEntity<CardResponse> {
    val command = CreateCardCommand(
      question = request.question,
      answer = request.answer,
      tag = request.tag
    )

    val card = createCardUseCase.createCard(
      command
    )

    return ResponseEntity.status(201).body(card.toResponse())
  }

  @GetMapping("/quizz")
  fun getQuizCards(
    @RequestParam date: String = LocalDate.now().toString()
  ): ResponseEntity<List<CardResponse>> {
    val cards = getQuizCardsUseCase.getQuizCards(
      date
    )
    return ResponseEntity.ok(cards.map {
      it.toResponse()
    })
  }

  @PatchMapping("/{cardId}/answer")
  fun answerCard(
    @PathVariable cardId: String,
    @RequestBody @Valid request: AnswerCardRequest
  ): ResponseEntity<Void> {
    val command = AnswerCardCommand(cardId = cardId, isValid = request.isValid)
    answerCardUseCase.answerCard(command)

    return ResponseEntity.noContent().build()
  }

  private fun Card.toResponse() = CardResponse(
    id = this.id,
    category = this.category.name,
    question = this.question,
    answer = this.answer,
    tag = this.tag
  )
}