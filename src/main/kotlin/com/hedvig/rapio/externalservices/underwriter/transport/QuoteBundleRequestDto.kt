package com.hedvig.rapio.externalservices.underwriter.transport

import java.util.UUID

data class QuoteBundleRequestDto(
    val quoteIds: List<UUID>
)