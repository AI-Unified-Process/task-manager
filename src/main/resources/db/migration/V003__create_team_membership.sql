-- Create sequence for TEAM_MEMBERSHIP primary key
create sequence team_membership_seq start with 1000;

-- Create TEAM_MEMBERSHIP table
create table team_membership
(
    id        bigint      not null primary key default nextval('team_membership_seq'),
    username  username    not null,
    team_id   bigint      not null,
    joined_at timestamp   not null,
    role      varchar(20) not null,

    constraint fk_team_membership_user foreign key (username) references "user" (username),
    constraint fk_team_membership_team foreign key (team_id) references team (id),
    constraint uq_team_membership_user_team unique (username, team_id),
    constraint ck_team_membership_role check (role in ('MEMBER', 'LEAD'))
);
