package com.esgi.leitnerbackend.cards.adapter.input.web.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CardResponse(
    val id: String? = null,
    val category: String? = null,
    val question: String? = null,
    val answer: String? = null,
    val tag: String? = null
)
