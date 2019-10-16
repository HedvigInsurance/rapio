package com.hedvig.rapio.quotes

import arrow.core.Either
import com.hedvig.rapio.quotes.web.dto.QuoteRequestDTO
import com.hedvig.rapio.quotes.web.dto.QuoteResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignRequestDTO
import com.hedvig.rapio.quotes.web.dto.SignResponseDTO
import java.util.*

interface QuoteService {
    fun createQuote(requestDTO: QuoteRequestDTO) : Either<String, QuoteResponseDTO>
    fun signQuote(quoteId: UUID, request: SignRequestDTO): Either<String, SignResponseDTO>
}