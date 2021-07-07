package com.hedvig.rapio.quotes

import arrow.core.getOrHandle
import com.hedvig.libs.logging.calls.LogCall
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.comparison.web.dto.ExternalErrorResponseDTO
import com.hedvig.rapio.quotes.web.dto.ApartmentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.BundleQuotesRequestDTO
import com.hedvig.rapio.quotes.web.dto.DanishAccidentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.DanishHomeContentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.DanishTravelQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.HouseQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.NorwegianHomeContentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.NorwegianTravelQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.QuoteRequestDTO
import com.hedvig.rapio.quotes.web.dto.SignBundleRequestDTO
import com.hedvig.rapio.quotes.web.dto.SignRequestDTO
import com.hedvig.rapio.util.SwedishPersonalNumberValidator
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("v1/quotes")
class QuotesController @Autowired constructor(
    val quoteService: QuoteService
) {
    @PostMapping
    @Secured("ROLE_COMPARISON", "ROLE_DISTRIBUTION")
    @LogCall
    fun createQuote(@Valid @RequestBody request: QuoteRequestDTO): ResponseEntity<*> = logRequestId(request.requestId) {
        val currentUserName = SecurityContextHolder.getContext().authentication.name
        val partner = Partner.valueOf(currentUserName)

        val requestData = when (val data = request.quoteData) {

            is ApartmentQuoteRequestData -> {
                request.copy(
                    quoteData = data.copy(
                        personalNumber = SwedishPersonalNumberValidator.validate(data.personalNumber).idno
                    )
                )
            }

            is HouseQuoteRequestData -> {
                request.copy(
                    quoteData = data.copy(
                        personalNumber = SwedishPersonalNumberValidator.validate(data.personalNumber).idno
                    )
                )
            }

            is NorwegianTravelQuoteRequestData -> request
            is NorwegianHomeContentQuoteRequestData -> request
            is DanishHomeContentQuoteRequestData -> request
            is DanishTravelQuoteRequestData -> request
            is DanishAccidentQuoteRequestData -> request
        }

        return@logRequestId ok(quoteService.createQuote(requestData, partner))
    }

    @PostMapping("/bundle")
    @Secured("ROLE_COMPARISON", "ROLE_DISTRIBUTION")
    @LogCall
    fun bundleQuotes(
        @Valid @RequestBody request: BundleQuotesRequestDTO
    ): ResponseEntity<out Any> {
        return logRequestId(request.requestId) {

            val response = quoteService.bundleQuotes(request)

            return@logRequestId response.bimap(
                { left -> ResponseEntity.status(500).body(ExternalErrorResponseDTO(left)) },
                { right -> ok(right) }
            ).getOrHandle { it }
        }
    }

    @PostMapping("/{quoteId}/sign")
    @Secured("ROLE_COMPARISON", "ROLE_DISTRIBUTION")
    @LogCall
    fun signRegularQuote(
        @Valid @PathVariable quoteId: UUID,
        @Valid @RequestBody request: SignRequestDTO
    ): ResponseEntity<out Any> {
        if (request.startsAt == null && request.currentInsuranceCompanyId == null) {
            return ResponseEntity.badRequest().body("currentInsuranceCompanyId is required when startsAt is null")
        }
        return logRequestId(request.requestId) {

            val response = quoteService.signQuote(quoteId, request)

            return@logRequestId response.bimap(
                { left -> ResponseEntity.status(500).body(ExternalErrorResponseDTO(left)) },
                { right -> ok(right) }
            ).getOrHandle { it }
        }
    }

    @PostMapping("/bundle/sign")
    @Secured("ROLE_COMPARISON", "ROLE_DISTRIBUTION")
    @LogCall
    fun signBundle(
        @Valid @RequestBody request: SignBundleRequestDTO
    ): ResponseEntity<out Any> {

        return logRequestId(request.requestId) {

            val response = quoteService.signBundle(request)

            return@logRequestId response.bimap(
                { left -> ResponseEntity.status(500).body(ExternalErrorResponseDTO(left)) },
                { right -> ok(right) }
            ).getOrHandle { it }
        }
    }

    private fun <T> logRequestId(requestId: String, fn: () -> T): T {
        try {
            MDC.put("requestId", requestId)
            return fn()
        } finally {
            MDC.remove("requestId")
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}
