package com.hedvig.rapio.comparison

import arrow.core.*
import arrow.core.extensions.fx
import com.hedvig.rapio.comparison.web.dto.*
import com.hedvig.rapio.util.IdNumberValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid


fun notAccepted(error:String) = ResponseEntity.status(402).body(ErrorResponse(error))

fun badRequest(error:String) = ResponseEntity.badRequest().body(ErrorResponse(error))

@RestController
@RequestMapping("v1/quotes")
class ComparisonQuoteController @Autowired constructor(
        val quoteService: QuoteService
) {
    @PostMapping()
    fun createQuote(@Valid @RequestBody request: QuoteRequestDTO): ResponseEntity<out Any> {

        val validIdNumber = when (val idnumber = IdNumberValidator.validate(request.quoteData.personalNumber)) {
            is None -> Left(badRequest("PersonalNumber is invalid"))
            is Some -> Right(QuoteRequestDTO.quoteData.personalNumber.set(request, idnumber.t.idno))
        }

        return validIdNumber.flatMap {
            requestWithValidatedPnr  ->
            quoteService.createQuote(requestWithValidatedPnr).bimap(
                    {left -> notAccepted(left)},
                    {right -> ok(right)}
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

    private fun validatePersonalNumber(request: QuoteRequestDTO): Option<QuoteRequestDTO> {
        return IdNumberValidator.validate(request.quoteData.personalNumber)
                .map { QuoteRequestDTO.quoteData.personalNumber.set(request, it.idno) }
    }

    @PostMapping("{quoteId}/sign")
    fun signQuote(@Valid @PathVariable quoteId : UUID, @Valid @RequestBody request: SignRequestDTO): ResponseEntity<Any> {

        val response = quoteService.signQuote(quoteId, request)

        return if(response != null) {
            ok(response)
        }
        else {
            ResponseEntity.status(402).body(ErrorResponse("Could not create quote"))
        }
    }
}