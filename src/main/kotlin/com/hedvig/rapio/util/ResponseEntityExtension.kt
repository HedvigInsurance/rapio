package com.hedvig.rapio.util

import com.hedvig.rapio.comparison.web.dto.ExternalErrorResponseDTO
import org.springframework.http.ResponseEntity

fun notAccepted(error: String) = ResponseEntity.status(422).body(ExternalErrorResponseDTO(error))

fun badRequest(error: String) = ResponseEntity.badRequest().body(ExternalErrorResponseDTO(error))
