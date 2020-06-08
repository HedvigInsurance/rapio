package com.hedvig.rapio.externalservices.apigateway.transport

import com.neovisionaries.i18n.CountryCode

data class CreateSetupPaymentLinkRequestDto(
  val memberId: String,
  val countryCode: CountryCode
)
