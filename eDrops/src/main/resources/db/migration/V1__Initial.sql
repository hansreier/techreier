CREATE TABLE blog
(
    pos           INTEGER      NOT NULL,
    blog_owner_id BIGINT       NOT NULL,
    changed       TIMESTAMP(0) NOT NULL,
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    topic         BIGINT       NOT NULL,
    segment       VARCHAR(30)  NOT NULL,
    subject       VARCHAR(50)  NOT NULL,
    about         VARCHAR(400) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE blog_post
(
    blog_id BIGINT       NOT NULL,
    changed TIMESTAMP(0) NULL,
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    segment VARCHAR(30)  NOT NULL,
    title   VARCHAR(50)  NOT NULL,
    summary VARCHAR(400) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE blog_owner
(
    changed      TIMESTAMP(0) NULL,
    created      TIMESTAMP(0) NOT NULL,
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    country_code VARCHAR(15)  NOT NULL,
    zip_code     VARCHAR(15)  NOT NULL,
    username     VARCHAR(60)  NOT NULL,
    address      VARCHAR(255) NOT NULL,
    e_mail       VARCHAR(255) NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    location     VARCHAR(255) NOT NULL,
    password     VARCHAR(255),
    phones       VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE blog_text
(
    blog_post_id BIGINT       NOT NULL,
    changed       TIMESTAMP(0) NULL,
    text          TEXT         NOT NULL,
    PRIMARY KEY (blog_post_id)
) ENGINE = InnoDB;

CREATE TABLE language_code
(
    code     VARCHAR(15)  NOT NULL,
    language VARCHAR(255) NOT NULL,
    PRIMARY KEY (code)
) ENGINE = InnoDB;

CREATE TABLE topic
(
    pos           INTEGER     NOT NULL,
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    language_code VARCHAR(15) NOT NULL,
    topic_key     VARCHAR(30) NOT NULL,
    text          VARCHAR(50),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE blog_owner
    ADD CONSTRAINT uk_blog_owner_username UNIQUE (username);

ALTER TABLE blog
    ADD CONSTRAINT fk_blog_blog_owner
        FOREIGN KEY (blog_owner_id)
            REFERENCES blog_owner (id);

ALTER TABLE blog
    ADD CONSTRAINT fk_blog_topic
        FOREIGN KEY (topic)
            REFERENCES topic (id);

ALTER TABLE blog_post
    ADD CONSTRAINT fk_blog_post_blog
        FOREIGN KEY (blog_id)
            REFERENCES blog (id);

ALTER TABLE blog_text
    ADD CONSTRAINT fk_blog_text_blog_post
        FOREIGN KEY (blog_post_id)
            REFERENCES blog_post (id);

ALTER TABLE topic
    ADD CONSTRAINT fk_topic_language_code
        FOREIGN KEY (language_code)
            REFERENCES language_code (code);
