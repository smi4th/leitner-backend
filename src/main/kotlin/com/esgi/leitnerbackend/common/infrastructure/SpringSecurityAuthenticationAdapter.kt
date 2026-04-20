package com.esgi.leitnerbackend.common.infrastructure

import com.esgi.leitnerbackend.cards.domain.port.AuthenticationPort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SpringSecurityAuthenticationAdapter : AuthenticationPort {
  override fun getCurrentUserId(): String {
    val authentication = SecurityContextHolder.getContext().authentication

    if (authentication == null || !authentication.isAuthenticated || authentication.principal == "anonymousUser") {
      throw RuntimeException("No user authenticated")
    }

    return authentication.name
  }
}