package com.hedvig.rapio.comparison

import arrow.core.Either
import com.hedvig.rapio.comparison.web.dto.*
import com.hedvig.rapio.util.IdNumberValidator
import com.hedvig.rapio.util.IdNumberValidatorInvalid
import com.hedvig.rapio.util.IdNumberValidatorValid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("v1/quotes")
class ComparisonQuoteController @Autowired constructor(
        val quoteService: QuoteService
) {
    @PostMapping()
    fun createQuote(@Valid @RequestBody request: QuoteRequestDTO): ResponseEntity<Any> {
        val validIdNumber = when (val potentiallyValidIdNumber = IdNumberValidator.validate(request.quoteData.personalNumber)) {
            is IdNumberValidatorInvalid -> return ResponseEntity.badRequest().build()
            else -> potentiallyValidIdNumber as IdNumberValidatorValid
        }

        val quoteOrError = quoteService.createQuote(request.copy(quoteData = request.quoteData.copy(personalNumber = validIdNumber.idno)))
        return when(quoteOrError) {
            is Either.Left -> ResponseEntity.status(402).body(ErrorResponse(quoteOrError.a))
            is Either.Right -> ok(quoteOrError.b)
        }
    }

    @PostMapping("{quoteId}/sign")
    fun signQuote(@Valid @PathVariable quoteId : UUID, @Valid @RequestBody request: SignRequestDTO): ResponseEntity<Any> {

        val response = quoteService.signQuote(quoteId, request)

        return if(response != null) {
            ResponseEntity.ok(response)
        }
        else {
            ResponseEntity.status(402).body(ErrorResponse("Could not create quote"))
        }
    }
}