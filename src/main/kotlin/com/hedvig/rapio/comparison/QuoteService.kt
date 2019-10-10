package com.hedvig.rapio.comparison

import arrow.core.Either
import com.hedvig.rapio.comparison.domain.ComparisonQuote
import com.hedvig.rapio.comparison.web.dto.QuoteRequestDTO
import com.hedvig.rapio.comparison.web.dto.QuoteResponseDTO
import com.hedvig.rapio.comparison.web.dto.SignRequestDTO
import com.hedvig.rapio.comparison.web.dto.SignResponseDTO
import java.util.*

interface QuoteService {
    fun createQuote(requestDTO: QuoteRequestDTO) : Either<String, QuoteResponseDTO>
    fun signQuote(quoteId: UUID, request: SignRequestDTO): SignResponseDTO?
}