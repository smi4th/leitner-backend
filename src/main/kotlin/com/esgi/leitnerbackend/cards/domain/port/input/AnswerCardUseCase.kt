package com.esgi.leitnerbackend.cards.domain.port.input

interface AnswerCardUseCase {
  fun answerCard(answerCardCommand: AnswerCardCommand)
}