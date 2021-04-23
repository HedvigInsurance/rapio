--liquibase formatted.sql

--changeset vonElfvin:20210421-2333-create-external-member.sql

CREATE TABLE "external_member" (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    member_id VARCHAR(32) NOT NULL,
    partner VARCHAR(32) NOT NULL
)

--rollback DROP TABLE "external_member"