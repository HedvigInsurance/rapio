package com.hedvig.rapio.externalservices.underwriter.transport

import com.hedvig.libs.logging.masking.Masked
import java.time.LocalDate

data class SignQuoteRequest(
    val name: Name?,
    @Masked val ssn: String?,
    val startDate: LocalDate?,
    val insuranceCompany: String?,
    @Masked val email: String,
    val memberId: String?
) {
    data class Name(
        @Masked val firstName: String,
        @Masked val lastName: String
    )
}
