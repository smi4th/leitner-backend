package com.esgi.leitnerbackend.cards.domain.port.output

import com.esgi.leitnerbackend.cards.domain.model.Card

interface CloudTaskPort {
  fun scheduleCardCreatedTask(card: Card)
}
