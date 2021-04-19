package com.hedvig.rapio.quotes

import arrow.core.Either
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.quotes.web.dto.BundleQuotesRequestDTO
import com.hedvig.rapio.quotes.web.dto.BundleQuotesResponseDTO
import com.hedvig.rapio.quotes.web.dto.QuoteRequestDTO
import com.hedvig.rapio.quotes.web.dto.QuoteResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignBundleRequestDTO
import com.hedvig.rapio.quotes.web.dto.SignBundleResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignRequestDTO
import com.hedvig.rapio.quotes.web.dto.SignResponseDTO
import java.util.*

interface QuoteService {
    fun createQuote(requestDTO: QuoteRequestDTO, partner: Partner): Either<String, QuoteResponseDTO>
    fun bundleQuotes(request: BundleQuotesRequestDTO): Either<String, BundleQuotesResponseDTO>
    fun signQuote(quoteId: UUID, request: SignRequestDTO): Either<String, SignResponseDTO>
    fun signBundle(request: SignBundleRequestDTO): Either<String, SignBundleResponseDTO>
}
