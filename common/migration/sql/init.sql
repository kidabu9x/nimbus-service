CREATE TABLE IF NOT EXISTS users
(
    id int auto_increment,
    email varchar(50) not null,
    first_name varchar(100) null,
    last_name varchar(100) null,
    source enum('GOOGLE', 'FACEBOOK', 'NATIVE') default 'NATIVE' not null,
    avatar varchar(255) null,
    created_at datetime                                                     NULL,
    updated_at datetime                                                     NULL,
    CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE UNIQUE INDEX users_email_uindex ON users (email);

CREATE TABLE IF NOT EXISTS blogs
(
    id         int auto_increment PRIMARY KEY,
    title      varchar(255)                                                 NOT NULL,
    slug       varchar(255)                                                 NOT NULL,
    status     enum ('DELETED', 'PUBLISHED', 'DISABLED') default 'DISABLED' NOT NULL,
    thumbnail  varchar(255)                                                 NULL,
    created_at datetime                                                     NULL,
    updated_at datetime                                                     NULL,
    extra_data json                                                         NULL,
    CONSTRAINT blogs_slug_uindex unique (slug)
);

CREATE TABLE IF NOT EXISTS blog_contents
(
    id int auto_increment,
    blog_id int not null,
    type enum('HTML', 'TEXT', 'IMAGE') default 'HTML' not null,
    content text not null,
    CONSTRAINT blog_contents_pk PRIMARY KEY (id),
    CONSTRAINT blog_contents_blogs_id_fk FOREIGN KEY (blog_id) references blogs (id)
);

CREATE TABLE IF NOT EXISTS blog_user
(
    blog_id int not null,
    user_id int not null,
    CONSTRAINT blog_authors_pk
        PRIMARY KEY (blog_id, user_id),
    CONSTRAINT blog_authors_blogs_id_fk
        FOREIGN KEY (blog_id) references blogs (id),
    CONSTRAINT blog_authors_users_id_fk
        FOREIGN KEY (user_id) references users (id)
);

CREATE TABLE tags
(
    id int auto_increment,
    title varchar(50) not null,
    slug varchar(50) not null,
    created_at DATETIME null,
    updated_at DATETIME null,
    CONSTRAINT tags_pk
        PRIMARY KEY (id)
);

CREATE UNIQUE INDEX tags_slug_uindex ON tags (slug);

CREATE TABLE blog_tag
(
    tag_id int not null,
    blog_id int not null,
    CONSTRAINT blog_tag_mappings_pk
        PRIMARY KEY (tag_id, blog_id),
    CONSTRAINT blog_tag_mappings_blogs_id_fk
        FOREIGN KEY (blog_id) references blogs (id),
    CONSTRAINT blog_tag_mappings_tags_id_fk
        FOREIGN KEY (tag_id) REFERENCES tags (id)
);

create table categories
(
    id         int auto_increment,
    title      varchar(255) not null,
    slug       varchar(255) not null,
    created_at datetime     null,
    updated_at datetime     null,
    constraint categories_slug_uindex
        unique (slug),
    CONSTRAINT categories_pk
        PRIMARY KEY (id)
);

create table blog_category
(
    blog_id     int      not null,
    category_id int      not null,
    primary key (blog_id, category_id),
    constraint blog_category_mappings_blogs_id_fk
        foreign key (blog_id) references blogs (id),
    constraint blog_category_mappings_categories_id_fk
        foreign key (category_id) references categories (id)
);


