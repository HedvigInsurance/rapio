package com.hedvig.rapio.externalservices.productPricing.transport

import com.hedvig.rapio.externalservices.memberService.model.TrialType
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class TrialDto(
    val id: UUID,
    val memberId: String,
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val type: TrialType,
    val partner: String,
    val address: Address,
    val certificateUrl: String?,
    val status: TrialStatus,
    val createdAt: Instant
) {
    data class Address(
        val street: String,
        val city: String,
        val zipCode: String,
        val livingSpace: Int?,
        val apartmentNo: String?,
        val floor: Int?
    )

    enum class TrialStatus {
        ACTIVE_IN_FUTURE_AND_TERMINATED_IN_FUTURE,
        TERMINATED_IN_FUTURE,
        TERMINATED_TODAY,
        TERMINATED
    }
}

fun TrialDto.TrialStatus.toInsuranceStatus() = when (this) {
    TrialDto.TrialStatus.ACTIVE_IN_FUTURE_AND_TERMINATED_IN_FUTURE -> InsuranceStatus.ACTIVE_IN_FUTURE
    TrialDto.TrialStatus.TERMINATED_IN_FUTURE,
    TrialDto.TrialStatus.TERMINATED_TODAY -> InsuranceStatus.ACTIVE
    TrialDto.TrialStatus.TERMINATED -> InsuranceStatus.TERMINATED
}
