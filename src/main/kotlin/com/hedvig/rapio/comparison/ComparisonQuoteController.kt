package com.hedvig.rapio.comparison

import com.hedvig.rapio.comparison.web.dto.QuoteRequestDTO
import com.hedvig.rapio.comparison.web.dto.QuoteResponseDTO
import com.hedvig.rapio.comparison.web.dto.SignRequestDTO
import com.hedvig.rapio.comparison.web.dto.SignResponseDTO
import com.hedvig.rapio.util.IdNumberValidator
import com.hedvig.rapio.util.IdNumberValidatorInvalid
import com.hedvig.rapio.util.IdNumberValidatorValid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("v1/quotes")
class ComparisonQuoteController @Autowired constructor(
        val quoteService: QuoteService
) {
    @PostMapping()
    fun createQuote(@Valid @RequestBody request: QuoteRequestDTO): ResponseEntity<QuoteResponseDTO> {
        val validIdNumber = when (val potentiallyValidIdNumber = IdNumberValidator.validate(request.quoteData.personalNumber)) {
            is IdNumberValidatorInvalid -> return ResponseEntity.badRequest().build()
            else -> potentiallyValidIdNumber as IdNumberValidatorValid
        }

        val quote = quoteService.createQuote(request.copy(quoteData = request.quoteData.copy(personalNumber = validIdNumber.idno)))

        return ResponseEntity.ok(quote)
    }

    @PostMapping("{quoteId}/sign")
    fun signQuote(@Valid @PathVariable quoteId : UUID, @Valid @RequestBody request: SignRequestDTO): ResponseEntity<SignResponseDTO> {

        val response = quoteService.signQuote(quoteId, request)

        return ResponseEntity.ok(response)
    }
}