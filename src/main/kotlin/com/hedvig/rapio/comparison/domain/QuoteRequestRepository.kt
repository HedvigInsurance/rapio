package com.hedvig.rapio.comparison.domain

import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.*

interface QuoteRequestRepository {
    @SqlQuery("SELECT * from comparison_quote_request WHERE id = :id")
    fun loadQuoteRequest(id: UUID):ComparisonQuote

    @SqlUpdate("""UPDATE "comparison_quote_request" SET 
        request_time = :requestTime, 
        quote_data = :quoteData ,
        request_id = :requestId,
        underwriter_quote_id = :underwriterQuoteId,
        signed = :signed
        where id = :id""")
    fun updateQuoteRequest(@BindBean quote: ComparisonQuote)

    @SqlUpdate("""INSERT INTO comparison_quote_request 
        (id, request_time, quote_data, request_id, underwriter_quote_id, signed) values 
        (:id, :requestTime, :quoteData, :requestId, :underwriterQuoteId, :signed)""")
    fun insert(@BindBean quote: ComparisonQuote)
}