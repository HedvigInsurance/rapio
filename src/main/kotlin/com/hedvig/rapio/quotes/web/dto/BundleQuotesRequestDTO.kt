package com.hedvig.rapio.quotes.web.dto

import java.util.UUID
import javax.validation.constraints.NotEmpty

data class BundleQuotesRequestDTO(
    val requestId: String,
    @get:NotEmpty
    val quoteIds: List<UUID>
)