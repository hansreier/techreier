create table blog (
                      pos integer not null,
                      blog_owner_id bigint not null,
                      changed timestamp(0) not null,
                      id bigint not null auto_increment,
                      topic bigint not null,
                      segment varchar(30) not null,
                      subject varchar(80) not null,
                      about varchar(1000) not null,
                      primary key (id)
) engine=InnoDB;

create table blog_owner (
                            changed timestamp(0) null,
                            created timestamp(0) not null,
                            id bigint not null auto_increment,
                            country_code varchar(20) not null,
                            zip_code varchar(20) not null,
                            username varchar(40) not null,
                            address varchar(255) not null,
                            e_mail varchar(255) not null,
                            first_name varchar(255) not null,
                            last_name varchar(255) not null,
                            location varchar(255) not null,
                            password varchar(255),
                            phones varchar(255) not null,
                            primary key (id)
) engine=InnoDB;

create table blog_post (
                           blog_id bigint not null,
                           changed timestamp(0) null,
                           id bigint not null auto_increment,
                           segment varchar(30) not null,
                           title varchar(80) not null,
                           summary varchar(1000) not null,
                           primary key (id)
) engine=InnoDB;

create table blog_text (
                           blog_post_id bigint not null,
                           changed timestamp(0) null,
                           text TEXT not null,
                           primary key (blog_post_id)
) engine=InnoDB;

create table language_code (
                               code varchar(20) not null,
                               language varchar(255) not null,
                               primary key (code)
) engine=InnoDB;

create table topic (
                       pos integer not null,
                       id bigint not null auto_increment,
                       language_code varchar(20) not null,
                       topic_key varchar(30) not null,
                       text varchar(80),
                       primary key (id)
) engine=InnoDB;

alter table blog_owner
    add constraint uk_blog_owner_username unique (username);

alter table blog
    add constraint fk_blog_blog_owner
        foreign key (blog_owner_id)
            references blog_owner (id);

alter table blog
    add constraint fk_blog_topic
        foreign key (topic)
            references topic (id);

alter table blog_post
    add constraint fk_blog_post_blog
        foreign key (blog_id)
            references blog (id);

alter table blog_text
    add constraint fk_blog_text_blog_post
        foreign key (blog_post_id)
            references blog_post (id);

alter table topic
    add constraint fk_topic_language_code
        foreign key (language_code)
            references language_code (code);
