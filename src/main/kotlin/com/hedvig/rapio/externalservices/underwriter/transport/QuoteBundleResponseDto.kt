package com.hedvig.rapio.externalservices.underwriter.transport

data class QuoteBundleResponseDto(
    val bundleCost: BundleCost
) {
    data class BundleCost(
        val monthlyGross: Amount,
        val monthlyDiscount: Amount,
        val monthlyNet: Amount
    )

    data class Amount(
        val amount: String,
        val currency: String
    )
}