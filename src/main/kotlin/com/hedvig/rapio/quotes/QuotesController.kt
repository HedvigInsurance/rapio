package com.hedvig.rapio.quotes

import arrow.core.Left
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.getOrHandle
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.comparison.web.dto.ExternalErrorResponseDTO
import com.hedvig.rapio.quotes.web.dto.ApartmentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.HouseQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.QuoteRequestDTO
import com.hedvig.rapio.quotes.web.dto.SignRequestDTO
import com.hedvig.rapio.util.IdNumberValidator
import com.hedvig.rapio.util.badRequest
import com.hedvig.rapio.util.notAccepted
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
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
  @PostMapping()
  fun createQuote(@Valid @RequestBody request: QuoteRequestDTO): ResponseEntity<*> {

    val authentication = SecurityContextHolder.getContext().authentication

    val currentUserName = authentication.name

    if (!Partner.values().map { it.name }.contains(currentUserName)) {
      logger.error("Could not find any partner named $currentUserName")
    }

    val partner = Partner.valueOf(currentUserName)

    val personnummer = when (request.quoteData) {
      is ApartmentQuoteRequestData -> (request.quoteData as ApartmentQuoteRequestData).personalNumber
      is HouseQuoteRequestData -> (request.quoteData as HouseQuoteRequestData).personalNumber
    }

    return logRequestId(request.requestId) {
      val validIdNumber = when (val idNumber = IdNumberValidator.validate(personnummer)) {
        is None -> Left(badRequest("PersonalNumber is invalid"))
        is Some ->
          when (request.quoteData) {
            is ApartmentQuoteRequestData -> Right(
              request.copy(
                quoteData = (request.quoteData as ApartmentQuoteRequestData).copy(
                  personalNumber = idNumber.t.idno
                )
              )
            )
            is HouseQuoteRequestData -> Right(
              request.copy(
                quoteData = (request.quoteData as HouseQuoteRequestData).copy(
                  personalNumber = idNumber.t.idno
                )
              )
            )
          }
      }

      return@logRequestId validIdNumber.flatMap { requestWithValidatedPnr ->
        quoteService.createQuote(requestWithValidatedPnr, partner).bimap(
          { left -> notAccepted(left) },
          { right -> ok(right) }
        )
      }.getOrHandle { it }
    }
  }

  @PostMapping("{quoteId}/sign")
  fun signQuote(
    @Valid @PathVariable quoteId: UUID,
    @Valid @RequestBody request: SignRequestDTO
  ): ResponseEntity<out Any> {

    return logRequestId(request.requestId) {

      val response = quoteService.signQuote(quoteId, request)

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