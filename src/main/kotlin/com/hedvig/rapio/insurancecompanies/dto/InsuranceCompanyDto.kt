package com.hedvig.rapio.insurancecompanies.dto

import com.hedvig.rapio.externalservices.underwriter.transport.InsuranceCompanyDto as UnderwriterInsuranceCompanyDto

data class InsuranceCompanyDto(
    val id: String,
    val displayName: String,
    val switchable: Boolean
)

fun UnderwriterInsuranceCompanyDto.toDto(): InsuranceCompanyDto = InsuranceCompanyDto(id, displayName, switchable)
