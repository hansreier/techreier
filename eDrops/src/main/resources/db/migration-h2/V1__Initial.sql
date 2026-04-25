CREATE TABLE blog
(
    pos           integer       NOT NULL,
    blog_owner_id bigint        NOT NULL,
    changed       datetime(0)   NOT NULL,
    id            bigint        NOT NULL AUTO_INCREMENT,
    topic         bigint        NOT NULL,
    segment       varchar(30)   NOT NULL,
    subject       varchar(80)   NOT NULL,
    about         varchar(1000) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE blog_owner
(
    changed      datetime(0)  NOT NULL,
    created      datetime(0)  NOT NULL,
    id           bigint       NOT NULL AUTO_INCREMENT,
    country_code varchar(20)  NOT NULL,
    zip_code     varchar(20)  NOT NULL,
    username     varchar(40)  NOT NULL,
    address      varchar(255) NOT NULL,
    e_mail       varchar(255) NOT NULL,
    first_name   varchar(255) NOT NULL,
    last_name    varchar(255) NOT NULL,
    location     varchar(255) NOT NULL,
    password     varchar(255),
    phones       varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE blog_post
(
    blog_id bigint        NOT NULL,
    changed datetime(0)   NOT NULL,
    created datetime(0)   NOT NULL,
    id      bigint        NOT NULL AUTO_INCREMENT,
    segment varchar(30)   NOT NULL,
    title   varchar(80)   NOT NULL,
    summary varchar(1000) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE blog_text
(
    blog_post_id bigint NOT NULL,
    changed      datetime(0),
    text         TEXT   NOT NULL,
    PRIMARY KEY (blog_post_id)
) ENGINE = InnoDB;

CREATE TABLE language_code
(
    code     varchar(20)  NOT NULL,
    language varchar(255) NOT NULL,
    PRIMARY KEY (code)
) ENGINE = InnoDB;

CREATE TABLE topic
(
    pos           integer     NOT NULL,
    id            bigint      NOT NULL AUTO_INCREMENT,
    language_code varchar(20) NOT NULL,
    topic_key     varchar(30) NOT NULL,
    text          varchar(80),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE IF EXISTS blog_owner
    ADD CONSTRAINT uk_blog_owner_username UNIQUE (username);

ALTER TABLE IF EXISTS topic
    ADD CONSTRAINT uk_topic_topic_key_language_code UNIQUE (topic_key, language_code);

ALTER TABLE IF EXISTS blog
    ADD CONSTRAINT fk_blog_blog_owner
        FOREIGN KEY (blog_owner_id)
            REFERENCES blog_owner (id);

ALTER TABLE IF EXISTS blog
    ADD CONSTRAINT fk_blog_topic
        FOREIGN KEY (topic)
            REFERENCES topic (id);

ALTER TABLE IF EXISTS blog_post
    ADD CONSTRAINT fk_blog_post_blog
        FOREIGN KEY (blog_id)
            REFERENCES blog (id);

ALTER TABLE IF EXISTS blog_text
    ADD CONSTRAINT fk_blog_text_blog_post
        FOREIGN KEY (blog_post_id)
            REFERENCES blog_post (id);

ALTER TABLE IF EXISTS topic
    ADD CONSTRAINT fk_topic_language_code
        FOREIGN KEY (language_code)
            REFERENCES language_code (code);
