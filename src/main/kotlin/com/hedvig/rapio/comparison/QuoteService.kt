package com.hedvig.rapio.comparison

import arrow.core.Either
import com.hedvig.rapio.comparison.web.dto.*
import java.util.*

interface QuoteService {
    fun createQuote(requestDTO: QuoteRequestDTO) : Either<String, QuoteResponseDTO>
    fun signQuote(quoteId: UUID, request: SignRequestDTO): Either<String, SignResponseDTO>
}