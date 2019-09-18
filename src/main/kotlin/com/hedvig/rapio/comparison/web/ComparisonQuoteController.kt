package com.hedvig.rapio.comparison.web

import com.hedvig.rapio.comparison.QuoteService
import com.hedvig.rapio.comparison.web.dto.QuoteRequestDTO
import com.hedvig.rapio.comparison.web.dto.QuoteResponseDTO
import com.hedvig.rapio.comparison.web.dto.SignRequestDTO
import com.hedvig.rapio.comparison.web.dto.SignResponseDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.query.Param
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("v1/quote")
class ComparisonQuoteController @Autowired constructor(
        val quoteService: QuoteService
) {

    @PostMapping()
    fun createQuote(@Valid @RequestBody request: QuoteRequestDTO): ResponseEntity<QuoteResponseDTO> {
        val quote = quoteService.createQuote(request)

        return ResponseEntity.ok(QuoteResponseDTO(quote.id!!, Instant.now().toEpochMilli(), 143L))
    }

    @PostMapping("{quoteId}/sign")
    fun signQuote(@PathVariable quoteId :String, @Valid @RequestBody request: SignRequestDTO): ResponseEntity<SignResponseDTO> {
        return ResponseEntity.ok(SignResponseDTO(quoteId))
    }
}