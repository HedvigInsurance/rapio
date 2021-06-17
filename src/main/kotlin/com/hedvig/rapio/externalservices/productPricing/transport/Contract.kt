package com.hedvig.rapio.externalservices.productPricing.transport

import com.hedvig.rapio.externalservices.productPricing.TypeOfContract
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class Contract(
    val id: UUID,
    val holderMemberId: String,
    val masterInception: LocalDate?,
    val status: ContractStatus,
    val terminationDate: LocalDate?,
    val currentAgreementId: UUID,
    val typeOfContract: TypeOfContract,
    val genericAgreements: List<GenericAgreement>,
    val createdAt: Instant
)
