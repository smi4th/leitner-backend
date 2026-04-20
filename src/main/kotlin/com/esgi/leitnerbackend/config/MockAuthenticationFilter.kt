package com.esgi.leitnerbackend.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class MockAuthenticationFilter : OncePerRequestFilter() {
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    val auth = UsernamePasswordAuthenticationToken(
      "mock-user@esgi.fr",
      null,
      listOf(SimpleGrantedAuthority("ROLE_USER"))
    )

    SecurityContextHolder.getContext().authentication = auth
    filterChain.doFilter(request, response)
  }
}