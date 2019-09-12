package com.hedvig.rapio.comparison

import com.hedvig.rapio.comparison.web.dto.QuoteRequestDTO

interface QuoteService {
    fun createQuote(requestDTO: QuoteRequestDTO)
}