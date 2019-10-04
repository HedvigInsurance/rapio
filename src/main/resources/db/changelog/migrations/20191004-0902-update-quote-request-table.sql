--liquibase formatted.sql

--changeset johant:20191004-0902-update-quote-request-table.sql logicalFilePath:migrations/20191004-0902-update-quote-request-table.sql

ALTER TABLE "comparison_quote_request"
    ADD COLUMN "request_id" TEXT,
    ADD COLUMN "underwriter_quote_id" TEXT,
    ADD COLUMN "signed" BOOLEAN NOT NULL DEFAULT(false);

--rollback ALTER TABLE "comparison_quote_request"
--rollback    DROP COLUMN "request_id",
--rollback    DROP COLUMN "underwriter_quote_id",
--rollback    DROP COLUMN "signed";