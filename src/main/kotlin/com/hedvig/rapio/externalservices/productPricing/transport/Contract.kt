package com.hedvig.rapio.externalservices.productPricing.transport

import java.time.Instant
import java.time.LocalDate
import java.util.*

data class Contract(
    val id: UUID,
    val holderMemberId: String,
    val masterInception: LocalDate?,
    val status: ContractStatus,
    val terminationDate: LocalDate?,
    val currentAgreementId: UUID,
    val agreements: List<Agreement>,
    val createdAt: Instant
)
