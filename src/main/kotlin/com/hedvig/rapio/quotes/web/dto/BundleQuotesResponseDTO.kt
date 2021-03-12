package com.hedvig.rapio.quotes.web.dto

data class BundleQuotesResponseDTO(
    val requestId: String,
    val monthlyPremium: Amount
) {
    companion object {
        fun from(requestId: String, amount: String, currency: String): BundleQuotesResponseDTO =
            BundleQuotesResponseDTO(requestId, Amount(amount, currency))
    }

    data class Amount(
        val amount: String,
        val currency: String
    )
}