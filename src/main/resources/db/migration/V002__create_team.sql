-- Create sequence for TEAM primary key
create sequence team_seq start with 1000;

-- Create TEAM table
create table team
(
    id          bigint       not null primary key default nextval('team_seq'),
    name        varchar(100) not null unique,
    description varchar(500),
    created_at  timestamp    not null,
    is_active   boolean      not null
);
