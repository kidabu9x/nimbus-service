create table hachium_category
(
    id bigint not null,
    url varchar(1000) not null,
    name varchar(255) not null
);

create unique index hachium_category_id_uindex
    on hachium_category (id);

alter table hachium_category
    add constraint hachium_category_pk
        primary key (id);


create table hachium_course
(
    id bigint auto_increment,
    title varchar(255) not null,
    url varchar(1000) not null,
    image varchar(1000) null,
    old_price bigint null,
    price bigint null,
    author varchar(255) null,
    hachium_category_id bigint null,
    constraint hachium_course_pk
        primary key (id)
);

CREATE TABLE category_mapping
(
    category_id bigint not null,
    hachium_category_id bigint not null,
    CONSTRAINT category_mapping
        PRIMARY KEY (category_id, hachium_category_id)
);

