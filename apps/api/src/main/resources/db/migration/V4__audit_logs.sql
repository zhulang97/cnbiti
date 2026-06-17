create table audit_logs (
  id varchar(64) not null primary key,
  actor_id varchar(64),
  actor_name varchar(120) not null,
  action varchar(32) not null,
  target_type varchar(64) not null,
  target_id varchar(160),
  method varchar(12) not null,
  path varchar(500) not null,
  status_code int not null,
  ip_address varchar(80),
  user_agent varchar(500),
  created_at timestamp not null
);

create index idx_audit_logs_created_at on audit_logs (created_at);
create index idx_audit_logs_target on audit_logs (target_type, target_id);
create index idx_audit_logs_actor on audit_logs (actor_id);
