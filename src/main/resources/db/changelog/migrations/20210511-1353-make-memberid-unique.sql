--liquibase formatted.sql

--changeset tobiasbexelius:20210511-1353-make-memberid-unique.sql

ALTER TABLE "external_member" ADD CONSTRAINT unique_member_id UNIQUE (member_id);

--rollback ALTER TABLE "external_member" DROP CONSTRAINT unique_member_id