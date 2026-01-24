-- Create sequence for TASK_AUDIT primary key
create sequence task_audit_seq start with 1000;

-- Create TASK_AUDIT table
create table task_audit
(
    id          bigint        not null primary key default nextval('task_audit_seq'),
    task_id     bigint        not null,
    changed_by  username      not null,
    changed_at  timestamp     not null,
    change_type varchar(50)   not null,
    old_value   varchar(500),
    new_value   varchar(500),
    description varchar(1000),

    constraint fk_task_audit_task foreign key (task_id) references task (id),
    constraint fk_task_audit_user foreign key (changed_by) references "user" (username),
    constraint ck_task_audit_change_type check (change_type in ('CREATED', 'STATUS_CHANGED', 'ASSIGNED', 'UPDATED', 'DELETED'))
);
