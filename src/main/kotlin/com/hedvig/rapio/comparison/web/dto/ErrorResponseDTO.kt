package com.hedvig.rapio.comparison.web.dto

data class ErrorResponse(
        val errorCode: ErrorCodes,
        val errorMessage: String
)

enum class ErrorCodes {
    MEMBER_HAS_EXISTING_INSURANCE,
    MEMBER_BREACHES_UW_GUIDELINES,
    MEMBER_QUOTE_HAS_EXPIRED
}