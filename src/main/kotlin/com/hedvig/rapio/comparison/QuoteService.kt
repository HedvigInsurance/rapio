package com.hedvig.rapio.comparison

import com.hedvig.rapio.comparison.domain.ComparisonQuoteRequest
import com.hedvig.rapio.comparison.web.dto.QuoteRequestDTO

interface QuoteService {
    open fun createQuote(requestDTO: QuoteRequestDTO) : ComparisonQuoteRequest
}