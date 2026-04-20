package com.esgi.leitnerbackend.cards.domain.port

interface AuthenticationPort {
  fun getCurrentUserId(): String
}