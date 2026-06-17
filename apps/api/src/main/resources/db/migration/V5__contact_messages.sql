create table contact_messages (
  id varchar(32) not null primary key,
  name varchar(160) not null,
  company varchar(255),
  email varchar(255) not null,
  phone varchar(80),
  subject varchar(255),
  message text not null,
  source varchar(120) not null,
  status varchar(32) not null,
  created_at timestamp not null,
  notes text,
  notes_updated_at timestamp
);

create index idx_contact_messages_status on contact_messages (status);
create index idx_contact_messages_created_at on contact_messages (created_at);
create index idx_contact_messages_email on contact_messages (email);
