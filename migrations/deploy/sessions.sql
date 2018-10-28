-- Deploy byspel:sessions to sqlite
-- requires: users

BEGIN;

create table sessions(
  session_id uuid not null primary key,
  user_id uuid not null,
  expires timestamp not null,
  foreign key (user_id) references users(id) on delete cascade
);

COMMIT;
