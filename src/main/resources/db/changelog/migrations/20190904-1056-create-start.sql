--liquibase formatted.sql

--changeset johant:20190904-1056-create-insplanet-quote-request.sql

CREATE TABLE "comparison_quote_request" (
    id UUID PRIMARY KEY,
    request_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    quote_data JSONB NOT NULL
)

--rollback DROP TABLE "quote_request"