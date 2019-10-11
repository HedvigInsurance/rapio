--liquibase formatted.sql

--changeset johant:20191011-1407-update-quote-request-table.sql logicalFilePath:migrations/20191011-1407-update-quote-request-table.sql

ALTER TABLE "comparison_quote_request"
    ADD COLUMN "valid_to" TIMESTAMP WITHOUT TIME ZONE;

--rollback ALTER TABLE "comparison_quote_request"
--rollback    DROP COLUMN "valid_to"