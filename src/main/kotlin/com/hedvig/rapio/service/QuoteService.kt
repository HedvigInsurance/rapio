package com.hedvig.rapio.service

import com.hedvig.rapio.web.dto.QuoteRequestDTO

interface QuoteService {
  fun createQuote(requestDTO: QuoteRequestDTO)
}