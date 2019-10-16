package com.hedvig.rapio.externalservices.underwriter.transport

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

data class ErrorResponse(
        val errorCode: ErrorCodes = ErrorCodes.UNKNOWN_ERROR_CODE,
        val errorMessage: String
)

enum class ErrorCodes {
    MEMBER_HAS_EXISTING_INSURANCE,
    MEMBER_BREACHES_UW_GUIDELINES,
    MEMBER_QUOTE_HAS_EXPIRED,

    @JsonEnumDefaultValue
    UNKNOWN_ERROR_CODE
}