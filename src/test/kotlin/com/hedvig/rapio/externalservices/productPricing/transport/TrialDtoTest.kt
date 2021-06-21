package com.hedvig.rapio.externalservices.productPricing.transport

import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TrialDtoTest {
    @Test
    fun `correctly maps TrialStatus to InsuranceStatus`() {
        assertThat(TrialDto.TrialStatus.ACTIVE_IN_FUTURE_AND_TERMINATED_IN_FUTURE.toInsuranceStatus())
            .isEqualTo(InsuranceStatus.ACTIVE_IN_FUTURE)
        assertThat(TrialDto.TrialStatus.TERMINATED_IN_FUTURE.toInsuranceStatus())
            .isEqualTo(InsuranceStatus.ACTIVE)
        assertThat(TrialDto.TrialStatus.TERMINATED_TODAY.toInsuranceStatus())
            .isEqualTo(InsuranceStatus.ACTIVE)
        assertThat(TrialDto.TrialStatus.TERMINATED.toInsuranceStatus())
            .isEqualTo(InsuranceStatus.TERMINATED)
    }
}
