package com.esgi.leitnerbackend.cards.adapter.output.persistence

import com.esgi.leitnerbackend.cards.domain.model.Card.CardCategory
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Entity
@Table(name = "cards")
class CardEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, unique = true)
  var id: String? = null,
  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  var category: CardCategory = CardCategory.FIRST,
  @Column(name = "question", nullable = false) var question: String = "",
  @Column(name = "answer", nullable = false) var answer: String = "",
  @Column(name = "tag", nullable = false) var tag: String = "",
  @Column(nullable = false) @CreatedDate var createdAt: Instant? = Instant.now(),
  @Column(nullable = false) @LastModifiedDate var updatedAt: Instant? = Instant.now()
)
