-- Revert byspel:users from sqlite

BEGIN;

drop table users;
drop table shadow;

COMMIT;
