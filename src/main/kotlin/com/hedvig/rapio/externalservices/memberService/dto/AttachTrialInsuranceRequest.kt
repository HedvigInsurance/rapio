package com.hedvig.rapio.externalservices.memberService.dto

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.externalservices.memberService.model.Ownership
import java.time.LocalDate

data class AttachTrialInsuranceRequest(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val ownership: Ownership,
    val partner: Partner
)