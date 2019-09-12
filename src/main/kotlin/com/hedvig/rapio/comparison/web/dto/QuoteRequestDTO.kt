package com.hedvig.rapio.comparison.web.dto

data class QuoteRequestDTO(
        val street: String,
        val zipCode: String,
        val city: String,
        val livingSpace: Int,
        val insuranceCoverageOverAMillion: Boolean,
        val homeOwner: Boolean,
        val deductible: Int,
        val personalNumber: String,
        val phoneNumber: String,
        val allRisk: Boolean,
        val leisureCoverage: Boolean,
        val increasedTravelCoverage: Boolean,
        val golfInsurance: Boolean,
        val securityDoor: Boolean,
        val alarmConnectedToAlarmCenter: Boolean,
        val paymentInterval: String,
        val householdSize: Int
)