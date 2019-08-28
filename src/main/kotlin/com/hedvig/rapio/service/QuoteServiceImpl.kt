package com.hedvig.rapio.service

import com.hedvig.rapio.data.QuoteRequestRepository
import com.hedvig.rapio.data.entity.QuoteRequest
import com.hedvig.rapio.web.dto.QuoteRequestDTO

class QuoteServiceImpl(
  val quoteRequestRepository: QuoteRequestRepository
) : QuoteService {

  override fun createQuote(requestDTO: QuoteRequestDTO) {
    quoteRequestRepository.save(QuoteRequest.from(requestDTO))

  }

}