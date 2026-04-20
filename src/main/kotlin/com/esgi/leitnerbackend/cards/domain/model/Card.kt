package com.esgi.leitnerbackend.cards.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class Card(
    val id: String? = null,
    val category: CardCategory = CardCategory.FIRST,
    val question: String,
    val answer: String,
    val tag: String,
    val createdAt: Instant? = Instant.now(),
    val updatedAt: Instant? = Instant.now()
) {

  enum class CardCategory {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    FIFTH,
    SIXTH,
    SEVENTH,
    DONE;

    fun next(): CardCategory {
      val categories = entries
      val nextIndex = (this.ordinal + 1).coerceAtMost(categories.size - 1)
      return categories[nextIndex]
    }

    fun getFrequency(): Int {
        return when (this) {
            FIRST -> 1
            SECOND -> 2
            THIRD -> 4
            FOURTH -> 8
            FIFTH -> 16
            SIXTH -> 32
            SEVENTH -> 64
            DONE -> 0
        }
    }
  }

  fun instantToLocaleDate(instant: Instant?): LocalDate? {
      return instant?.atZone(ZoneId.systemDefault())?.toLocalDate()
  }
}
