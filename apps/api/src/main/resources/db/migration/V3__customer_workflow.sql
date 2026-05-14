alter table customers add column notes text;
alter table customers add column notes_updated_at timestamp null;

update customers set notes = '' where notes is null;
