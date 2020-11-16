alter table blogs add description text null;

create table blog_views
(
    id int auto_increment,
    blog_id int not null,
    created_at datetime null,
    updated_at datetime null,
    constraint blog_views_pk
        primary key (id),
    constraint blog_views_blogs_id_fk
        foreign key (blog_id) references blogs (id)
            on update cascade on delete cascade
);
