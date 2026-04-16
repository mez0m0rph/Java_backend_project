insert into users (email, username, password, role, full_name, created_at)
values
('admin@example.com', 'admin', '$2b$10$j1/7AGqCgSkYYbdhXm1XUej6c2.UpTRRzUr50lagclPkqrXU8UWZS', 'ROLE_ADMIN', 'Admin User', now()),
('manager@example.com', 'manager', '$2b$10$j1/7AGqCgSkYYbdhXm1XUej6c2.UpTRRzUr50lagclPkqrXU8UWZS', 'ROLE_MANAGER', 'Project Manager', now()),
('user@example.com', 'user', '$2b$10$j1/7AGqCgSkYYbdhXm1XUej6c2.UpTRRzUr50lagclPkqrXU8UWZS', 'ROLE_USER', 'Regular User', now());

insert into projects (code, name, description, owner_id, created_at)
values ('CORE', 'Core Platform', 'Internal task tracker demo project', 1, now());

insert into project_members (project_id, user_id, project_role, joined_at)
values (1, 1, 'OWNER', now()), (1, 2, 'MANAGER', now()), (1, 3, 'MEMBER', now());

insert into tasks (code, title, description, status, priority, project_id, assignee_id, reporter_id, created_at, updated_at, due_date)
values ('CORE-1', 'Prepare API foundation', 'Set up project structure, auth and DB migrations', 'IN_PROGRESS', 'HIGH', 1, 2, 1, now(), now(), now() + interval '7 day');

insert into comments (task_id, author_id, content, created_at)
values (1, 2, 'Initial implementation started', now());

insert into audit_logs (task_id, project_id, actor_id, action, message, created_at)
values
(1, 1, 1, 'PROJECT_CREATED', 'Project created: CORE', now()),
(1, 1, 1, 'TASK_CREATED', 'Task created: CORE-1', now()),
(1, 1, 2, 'COMMENT_ADDED', 'Comment added to task CORE-1', now());
