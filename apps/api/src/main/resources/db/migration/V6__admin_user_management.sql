alter table admin_users add column status varchar(32) not null default 'active';
alter table admin_users add column created_at timestamp null;
alter table admin_users add column updated_at timestamp null;
alter table admin_users add column last_login_at timestamp null;

update admin_users
set status = 'active',
    created_at = current_timestamp,
    updated_at = current_timestamp
where created_at is null;

create unique index uk_admin_users_email on admin_users (email);
create index idx_admin_users_status on admin_users (status);
