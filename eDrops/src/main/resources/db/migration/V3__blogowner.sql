ALTER TABLE blog_owner
    RENAME COLUMN e_mail to email,
    ADD COLUMN access_level integer NOT NULL DEFAULT 0,
    MODIFY COLUMN username varchar(15)  NOT NULL;
