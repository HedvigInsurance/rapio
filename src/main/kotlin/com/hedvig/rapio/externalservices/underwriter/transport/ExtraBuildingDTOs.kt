package com.hedvig.rapio.externalservices.underwriter.transport

import java.util.UUID

data class ExtraBuildingRequestDto(
    val id: UUID?,
    val type: ExtraBuildingType,
    val area: Int,
    val hasWaterConnected: Boolean
)

enum class ExtraBuildingType {
    GARAGE,
    CARPORT,
    SHED,
    STOREHOUSE,
    FRIGGEBOD,
    ATTEFALL,
    OUTHOUSE,
    GUESTHOUSE,
    GAZEBO,
    GREENHOUSE,
    SAUNA,
    BARN,
    BOATHOUSE,
    OTHER
}
