create table catalog_content (
  id varchar(64) not null primary key,
  content_type varchar(40) not null,
  item_id varchar(64) not null,
  slug varchar(160),
  title varchar(255) not null,
  status varchar(32) not null default 'published',
  sort_order int not null default 0,
  payload_json text not null,
  updated_at timestamp not null
);

create unique index uk_catalog_content_type_item on catalog_content (content_type, item_id);
create index idx_catalog_content_type_sort on catalog_content (content_type, sort_order);
create index idx_catalog_content_type_slug on catalog_content (content_type, slug);

create table rfqs (
  id varchar(32) not null primary key,
  company varchar(255) not null,
  country varchar(120) not null,
  country_code varchar(16) not null,
  email varchar(255) not null,
  phone varchar(80),
  product varchar(160) not null,
  grade varchar(80) not null,
  qty varchar(120) not null,
  message text not null,
  status varchar(32) not null,
  created_at date not null,
  notes text
);

create index idx_rfqs_status on rfqs (status);
create index idx_rfqs_created_at on rfqs (created_at);

create table customers (
  id varchar(64) not null primary key,
  company varchar(255) not null,
  country varchar(120) not null,
  country_code varchar(16) not null,
  email varchar(255) not null,
  rfq_count int not null,
  last_contact date not null
);

create unique index uk_customers_email on customers (email);
create index idx_customers_last_contact on customers (last_contact);

create table admin_users (
  id varchar(64) not null primary key,
  username varchar(80) not null,
  display_name varchar(120) not null,
  email varchar(255) not null,
  role varchar(40) not null,
  password_hash varchar(255) not null,
  avatar varchar(500)
);

create unique index uk_admin_users_username on admin_users (username);

create table stored_files (
  id varchar(64) not null primary key,
  object_name varchar(500) not null,
  original_filename varchar(255) not null,
  content_type varchar(160) not null,
  size_bytes bigint not null,
  public_url varchar(1000) not null,
  created_at timestamp not null
);
