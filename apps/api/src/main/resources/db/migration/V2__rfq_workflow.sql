alter table rfqs add column status_updated_at timestamp null;
alter table rfqs add column notes_updated_at timestamp null;
alter table rfqs add column attachments_json text;

update rfqs set status_updated_at = created_at where status_updated_at is null;
update rfqs set attachments_json = '[]' where attachments_json is null;
