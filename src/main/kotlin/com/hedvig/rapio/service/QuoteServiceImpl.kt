package com.hedvig.rapio.service

import com.hedvig.rapio.data.QuoteRequestRepository
import com.hedvig.rapio.data.entity.QuoteRequest
import com.hedvig.rapio.web.dto.QuoteRequestDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class QuoteServiceImpl @Autowired constructor(
  val quoteRequestRepository: QuoteRequestRepository
) : QuoteService {

  override fun createQuote(requestDTO: QuoteRequestDTO) {
    quoteRequestRepository.save(QuoteRequest.from(requestDTO))
  }

}