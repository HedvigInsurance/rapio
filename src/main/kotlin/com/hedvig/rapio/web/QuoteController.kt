package com.hedvig.rapio.web

import com.hedvig.rapio.service.QuoteService
import com.hedvig.rapio.web.dto.QuoteRequestDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("quote")
class QuoteController @Autowired constructor(
  val quoteService: QuoteService
) {

  @PostMapping("create")
  fun createQuote(request: QuoteRequestDTO): ResponseEntity<Void> {
    quoteService.createQuote(request)

    return ResponseEntity.accepted().build()
  }

  @PostMapping("{quoteId}/sign")
  fun signQuote(): ResponseEntity<Void> {
    return ResponseEntity.accepted().build()
  }
}