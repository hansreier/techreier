ALTER TABLE blog_owner ADD COLUMN access_level integer NOT NULL DEFAULT 0;
ALTER TABLE blog_owner RENAME COLUMN e_mail to email;
ALTER TABLE blog_owner MODIFY COLUMN username varchar(15)  NOT NULL;