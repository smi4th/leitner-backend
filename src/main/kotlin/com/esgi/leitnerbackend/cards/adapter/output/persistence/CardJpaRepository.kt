package com.esgi.leitnerbackend.cards.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CardJpaRepository : JpaRepository<CardEntity, String> {
  fun findAllByTagIn(tags: List<String>): List<CardEntity>
}
