package com.hedvig.rapio.data

import com.hedvig.rapio.data.entity.QuoteRequest
import org.springframework.data.jpa.repository.JpaRepository

interface QuoteRequestRepository : JpaRepository<QuoteRequest, Int>