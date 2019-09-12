package com.hedvig.rapio.comparison.domain

import org.springframework.data.jpa.repository.JpaRepository

interface QuoteRequestRepository : JpaRepository<ComparisonQuoteRequest, Int>