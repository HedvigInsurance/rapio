package com.hedvig.rapio.quotes.web.dto

data class SignBundleResponseDTO(
  val requestId: String,
  val productIds: List<String>,
  val signedAt: Long,
  val completionUrl: String?
)