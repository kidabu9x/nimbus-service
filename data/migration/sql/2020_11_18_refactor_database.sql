alter table blog_user drop foreign key blog_authors_blogs_id_fk;
alter table blog_user drop foreign key blog_authors_users_id_fk;
alter table blog_user modify blog_id bigint not null;
alter table blog_user modify user_id bigint not null;

alter table blog_views drop foreign key blog_views_blogs_id_fk;
alter table blog_views modify id bigint auto_increment;
alter table blog_views modify blog_id bigint not null;
rename table blog_views to blog_view;

alter table blog_tag drop foreign key blog_tag_mappings_blogs_id_fk;
alter table blog_tag drop foreign key blog_tag_mappings_tags_id_fk;
alter table blog_tag modify tag_id bigint not null;
alter table blog_tag modify blog_id bigint not null;

alter table blog_contents drop foreign key blog_contents_blogs_id_fk;
alter table blog_contents modify id bigint auto_increment;
alter table blog_contents modify blog_id bigint not null;
rename table blog_contents to blog_content;

alter table blog_category drop foreign key blog_category_mappings_blogs_id_fk;
alter table blog_category drop foreign key blog_category_mappings_categories_id_fk;
alter table blog_category modify blog_id bigint not null;
alter table blog_category modify category_id bigint not null;

alter table tags modify id bigint auto_increment;
rename table tags to tag;

alter table categories modify id bigint auto_increment;
rename table categories to category;

alter table blogs modify id bigint auto_increment;
rename table blogs to blog;

alter table users modify id bigint auto_increment;
rename table users to user;


