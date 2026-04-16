create table users (
    id bigserial primary key,
    email varchar(120) not null unique,
    username varchar(60) not null unique,
    password varchar(255) not null,
    role varchar(20) not null,
    full_name varchar(120) not null,
    created_at timestamptz not null
);

create table projects (
    id bigserial primary key,
    code varchar(100) not null unique,
    name varchar(150) not null,
    description text,
    owner_id bigint not null references users(id),
    created_at timestamptz not null
);

create table project_members (
    id bigserial primary key,
    project_id bigint not null references projects(id) on delete cascade,
    user_id bigint not null references users(id) on delete cascade,
    project_role varchar(20) not null,
    joined_at timestamptz not null,
    unique(project_id, user_id)
);

create table tasks (
    id bigserial primary key,
    code varchar(50) not null unique,
    title varchar(200) not null,
    description text,
    status varchar(20) not null,
    priority varchar(20) not null,
    project_id bigint not null references projects(id) on delete cascade,
    assignee_id bigint references users(id),
    reporter_id bigint not null references users(id),
    created_at timestamptz not null,
    updated_at timestamptz not null,
    due_date timestamptz
);

create table comments (
    id bigserial primary key,
    task_id bigint not null references tasks(id) on delete cascade,
    author_id bigint not null references users(id),
    content text not null,
    created_at timestamptz not null
);

create table audit_logs (
    id bigserial primary key,
    task_id bigint references tasks(id) on delete cascade,
    project_id bigint references projects(id) on delete cascade,
    actor_id bigint not null references users(id),
    action varchar(40) not null,
    message text not null,
    created_at timestamptz not null
);

create index idx_tasks_project_id on tasks(project_id);
create index idx_tasks_assignee_id on tasks(assignee_id);
create index idx_tasks_status on tasks(status);
create index idx_tasks_priority on tasks(priority);
create index idx_comments_task_id on comments(task_id);
create index idx_audit_logs_task_id on audit_logs(task_id);
