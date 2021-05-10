create table slug_pool
(
	slug varchar(100) not null,
	type varchar(50) not null,
	target_id bigint not null
);

create unique index slug_pool_slug_uindex
	on slug_pool (slug);

alter table slug_pool
	add constraint slug_pool_pk
		primary key (slug);

alter table blog modify slug varchar(255) null;

alter table category modify slug varchar(255) null;
