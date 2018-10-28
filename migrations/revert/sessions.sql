-- Revert byspel:sessions from sqlite

BEGIN;

drop table sessions;

COMMIT;
