package com.hedvig.rapio.quotes

import arrow.core.*
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.comparison.web.dto.ExternalErrorResponseDTO
import com.hedvig.rapio.quotes.web.dto.*
import com.hedvig.rapio.util.IdNumberValidator
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid


fun notAccepted(error:String) = ResponseEntity.status(422).body(ExternalErrorResponseDTO(error))

fun badRequest(error:String) = ResponseEntity.badRequest().body(ExternalErrorResponseDTO(error))

private val logger = KotlinLogging.logger {}
@RestController
@RequestMapping("v1/quotes")
class QuotesController @Autowired constructor(
        val quoteService: QuoteService
) {
    @PostMapping()
    fun createQuote(@Valid @RequestBody request: QuoteRequestDTO): ResponseEntity<*> {

        val authentication = SecurityContextHolder.getContext().authentication

        val currentUserName = authentication.name

        if(!Partner.values().map { it.name }.contains(currentUserName)) {
            logger.error("Could not find any partner named $currentUserName")
        }

        val partner = Partner.valueOf(currentUserName)

        return logRequestId(request.requestId) {
            val validIdNumber = when (val idnumber = IdNumberValidator.validate(request.quoteData.personalNumber)) {
                is None -> Left(badRequest("PersonalNumber is invalid"))
                is Some -> Right(QuoteRequestDTO.quoteData.personalNumber.set(request, idnumber.t.idno))
            }

            return@logRequestId validIdNumber.flatMap { requestWithValidatedPnr ->
                quoteService.createQuote(requestWithValidatedPnr, partner).bimap(
                        { left -> notAccepted(left) },
                        { right -> ok(right) }
                )
            }.getOrHandle { it }
            /*
        return validatePersonalNumber(request).toEither { badRequest("PersonalNumber is invalid") }.flatMap {
            requestWithValidatedPnr  ->
                quoteService.createQuote(requestWithValidatedPnr).bimap(
                        {left -> notAccepted(left)},
                        {right -> ok(right)}
                )}.
            getOrHandle { it } */

            /*
        return Either.fx<ResponseEntity<ErrorResponse>, ResponseEntity<QuoteResponseDTO>> {

            val (requestWithValidatedPnr) = validatePersonalNumber(request).toEither { badRequest("PersonalNumber is invalid") }

            val (maybeQuote) = quoteService.createQuote(requestWithValidatedPnr).bimap(
                    { left ->  notAccepted(left)},
                    { right -> ok(right) }
            )
            maybeQuote
        }.getOrHandle { it } */

        }
    }

    private fun validatePersonalNumber(request: QuoteRequestDTO): Option<QuoteRequestDTO> {
        return IdNumberValidator.validate(request.quoteData.personalNumber)
                .map { QuoteRequestDTO.quoteData.personalNumber.set(request, it.idno) }
    }

    @PostMapping("{quoteId}/sign")
    fun signQuote(@Valid @PathVariable quoteId : UUID, @Valid @RequestBody request: SignRequestDTO): ResponseEntity<out Any> {

        return logRequestId(request.requestId) {

            val response = quoteService.signQuote(quoteId, request)

            return@logRequestId response.bimap(
                    {left -> ResponseEntity.status(500).body(ExternalErrorResponseDTO(left))},
                    {right -> ok(right)}
            ).getOrHandle { it }
        }
    }

    private fun <T> logRequestId(requestId:String, fn:() -> T): T {
        try {
            MDC.put("requestId", requestId)
            return fn()
        } finally {
            MDC.remove("requestId")
        }
    }
}