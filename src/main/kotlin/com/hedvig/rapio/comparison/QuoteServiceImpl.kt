package com.hedvig.rapio.comparison

import com.hedvig.rapio.comparison.domain.ComparisonQuoteRequest
import com.hedvig.rapio.comparison.domain.QuoteRequestRepository
import com.hedvig.rapio.comparison.web.dto.QuoteRequestDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class QuoteServiceImpl @Autowired constructor(
        val quoteRequestRepository: QuoteRequestRepository
) : QuoteService {

    override fun createQuote(requestDTO: QuoteRequestDTO): ComparisonQuoteRequest {

        return ComparisonQuoteRequest()
    }

}