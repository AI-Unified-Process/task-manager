-- Create sequence for TASK primary key
create sequence task_seq start with 1000;

-- Create TASK table
create table task
(
    id          bigint        not null primary key default nextval('task_seq'),
    team_id     bigint        not null,
    title       varchar(200)  not null,
    description varchar(2000),
    status      varchar(20)   not null,
    assigned_to username,
    created_by  username      not null,
    created_at  timestamp     not null,
    updated_at  timestamp     not null,

    constraint fk_task_team foreign key (team_id) references team (id),
    constraint fk_task_assigned_to foreign key (assigned_to) references "user" (username),
    constraint fk_task_created_by foreign key (created_by) references "user" (username),
    constraint ck_task_status check (status in ('DRAFT', 'OPEN', 'ASSIGNED', 'DONE'))
);
