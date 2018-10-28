-- Deploy byspel:users to sqlite

BEGIN;

create table users(
  id uuid not null primary key,
  primary_email string not null unique,
  full_name string,
  avatar string not null,
  last_login timestamp
);

create table shadow(
  user_id uuid not null primary key,
  hash string not null,
  foreign key (user_id) references users(id)
);

COMMIT;
