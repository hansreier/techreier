## My setup of persistent storage, including MariaDB

I have selected to use MariaDB, a fork of MySQL.
Mainly because it is Open Source and supported  directly by Cyberpanel that I use. 
For complex applications and special needs I will advise to use PostgreSQL.

I have tried both simpler databases like H2, SQL lite end even Firebird.
It is tempting because administration is simpler. 
The first two alternatives really does not support write transactions very well with high volumes.
The last is very badly adapted with Spring Data JPA, so forget it.  

## JPA and Hibernate

JPA / Hibernate was selected because it was industry standard for Java and a lot of people uses it.
Refer to a more detailed discussion [here](../about/jpa_en.md)

## Persistent storage of text and pictures

To store a lot of huge text documents including pictures in regular relational tables is not really advisable.
Direct text search in a lot of huge documents requires special care. A better approach would be to use
another kind of storage designed for documents and text search. The structured metadata could still
be stored in relational DB. This approach was used in a project at work, where Amazon S3 was used. But
the use case was different, since no advanced text search was required. I have professionally tried
Oracle Text for this purpose. Since this is a blog system, we really do not need huge document sizes.

To make it simple I use the internal file system for my initial blogs and a table in MariaDb to store
the majority of the blogs. I have used tables to store metadata about the blog text, including summary text.
In 2024, I added the possibility to store the blogs in the database, as an alternative. In addition,
a simple GUI was required to enter the blog structure and blog text. I have gradually added this to the system.
Before adding this GUI it was no need for any user administration. So Spring Security and very simple login
is set up to be able to enter blogs directly in the GUI.

Pictures (and other multimedia) will not be stored in the database but on the file system on the server. 
A Docker volume is mapped to the picture folder location. It is referenced from the blog text as links.
A simple synchronization service will be used from Jottacloud to maintain pictures. 

## Persistent storage of metadata and other structured data

I always store metodata in the relational database in addition to hierarchical blog data structure.  

Entity hierarchy:  
- BlogOwner: The owner of the blog, including control of write access for the blogs
- Blog: Really a collection blog posts with a short summary.
- Topic: Categorization of a blog, used to organize a dropdown menu
- LanguageCode: A topic (and blog) can be written in English or Norwegian. 
- BlogPost: The dated article contained in a blog with a short summary and metadata.
- BlogText: The markdown text of an article  

LanguageCode and Topic are stored with UniqueConstraint in the database.
I have preferred not to define other unique constraints because it makes the database less flexible.
Unique synthetic keys are used, except for LanguageCode id.  

### MariaDB persistent storage

MariaDB is selected as database to use in production. Mariadb is built on MySQL, but seems a better option
today due to licencing and other issues. PostgreSQL could be another option, but MariaDB has a smaller footprint,
is simpler and best suited for web applications. TODO: Temporarily H2 and the h2-prod profile is used in production.
If you use Flyway, it is much easier to clear MariaDB database tables,
with PostgreSQL usually the whole database must be deleted.

## Temporary database storage using H2

Before 2025 H2 in memory was used for production.  This means that blogs are not really stored permanently.
I set up initial data in the blog system quite simply using Kotlin and Hibernate.
 
The final step with permanent database storage in MariaDB was implemented late in 2025.
GUI is now used to feed all the blogs and most of the metadata into the database.

### About unit tests using Spring Boot testing and H2

H2 is used in automated integration tests. This is good enough even for most professional projects,
if you take care not using all the database platform specific features.
If you can avoid test DB-containers or libraries like Zonky (for PostgreSQL) it is adviceable. 

In earlier versions the entire Spring Context was reloaded and tables recreated for every unit test.
In the new version the tables is cleared instead. Note that other database configuration is not cleared.
Identifier numbers will continue to increase through all the tests, so take care when doing asserts.

## Databases, connections and Spring profiles

This project needs a database connection to run and uses Spring Boot standard configuration with profiles to
set up database connections.

Some parts of this webapp does not require a database. In the current setup a database connection at startup is
still required, due to the simple Spring Boot configuration. This markdown file is e.g. not stored in a database.
The blog part needs a database, even if markdown is used.

To select a Spring Boot database profile using environment variable:
SPRING_PROFILES_ACTIVE="profile".

### Profiles

Spring boot H2 profiles:
- h2: In memory. Uses Flyway for table generation, else JPA/Hibernate for other DB operations.
- h2-jpa-generate: H2 in memory: Uses JPA/Hibernate for all DB operations.
- h2-prod: Uses Flyway for table generation, else JPA/Hibernate for other DB operations. Temporary prod profile.
- h2-disk: H2 in local disk. Uses Flyway for table generation, else JPA/Hibernate for other DB operations.
- test: In memory for unit tests.  Uses Flyway for table generation, else JPA/Hibernate for other DB operations.
- gensql: Profile for generating sql from JPA/Hibernate to be used as V1__initial.sql for Flyway.

H2 could even be used with the disk option in production for simplicity, but it is only recommended for
reading database instances and not writing by many clients. One simple option would be to mount the h2 database
files (volume or bind) to ensure permanent storage. This is not tested.

### Profiles

Spring boot MariaDB profiles:
- local-mariadb: Mariadb in local disk. 
- mariadb-dockerized - connect dockerized mariadb (locally or from docker)
- docker-mariadb - connect from local docker image to local mariadb
- global-mariadb - connect globally to dockerized mariadb

All these profile uses Flyway for table generation, else JPA/Hibernate for other DB operations.

### Installing MariaDB locally

I installed MariaDB locally on PC, and it was almost as easy to set up as H2.
It was very easy to connect using the Database connect possibilities in Intellij.
- Use ordinary Windows installer
- Make sure that no port conflicts exist on the PC. If so, change port from default 3306.
- Define a new database schema:  edrops
- Define a new database user: dbuser
- Grant required permissions for dbuser.

```
CREATE DATABASE edrops;
CREATE USER dbuser IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON edrops.* TO 'dbuser'@'%' IDENTIFIED BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, SHOW VIEW ON edrops.* TO dbuser IDENTIFIED BY 'password';
```
refer to script dbInit.sql. The bottom grant statement is better to use, gives more limited access.

### Installing MariaDB on docker image

I have tested this, but I really like local installation better because it is easier. 
It is not that complicated if required to clear the tables, you just have to do it in the correct order.  

Use MariaDb official image from Dockerhub. Create a container. Remember to enter port 
and set MARIADB_ROOT_PASSWORD as environment variable when using Docker Desktop or docker command.
Sign in.  

Note: The db port is changed to 3308 in config for local Docker.  

Never use the root user and password for connection, a separate user: dbuser is defined for the purpose.
I have used an environment variable DB_PASSWORD, to avoid checking in db user password to github.  

- Local connect to dockerized MariaDB: Docker Desktop has a special host name host.docker.internal.
  Note: localhost refers to localhost on the Docker image and not on your development PC.
  I recommend to use host.docker.internal when docker containers is used locally with Docker Desktop.
- External connect to dockerized MariaDB:
  Change the configuration of the official Docker image. I have not yet manages to do that.
- Docker network connect to dockerized MariaDB: Both containers must be defined on the same docker network.
  Sorry but the same problems applied to this as with external connect.
- Docker container connect to local MariaDB: Use IP-address directly or even better host.docker.internal.

Example of defining database schema, dbuser and granting privileges is included in the dbinit.sql file.
Note that if you connect to dockerized MariaDB with dbuser outside of MariaDB container, 
you must allow for general external access when granting dbuser (or use IP address of local machine).
To run statements in this script is all you need to start the application with a fresh MariaDb instance.
Database tables if not existing are created automagically by Hibernate, and tables is populated with some initial data.

To connect from container to db-service on the host:
```
 url: jdbc:mariadb://host.docker.internal:3308/edrops?useSSL=false&useUnicode=yes&characterEncoding=UTF-8&serverTimezone=UTC
```
This only works using Docker Desktop. Docker Desktop cannot be run in Host mode, only in Brigde Mode (default).
Verify and eventuelly change port number

### Installing MariaDB on the VPS using Cyberpanel

With the Cyberpanel GUI we create the database tech_edrops with the user tech_edrops.

But this is not enough. A user must be created specifically for the Docker internal network.
Note that could have used just a wild card here and not started with IP address. Approach below is safer.

```
CREATE USER 'tech_edrops'@'172.17.%' IDENTIFIED BY 'YOUR_PASSWORD_HERE';
GRANT CREATE, ALTER, DROP, INDEX, INSERT, UPDATE, DELETE, SELECT, SHOW VIEW ON tech_edrops.* TO 'tech_edrops'@'172.17.%';
FLUSH PRIVILEGES;
```

And you have to check the MariaDb character set.

```
SELECT @@character_set_database, @@collation_database;
```

If the result is utf8mb3, it is wrong! Alter database to use the correct character set before flyway creates the tables.
If the tables exist, delete them (refer to next section).

```
ALTER DATABASE tech_edrops CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
```
Start the container! Final check for all individual tables:

```
SELECT table_name, table_collation 
FROM information_schema.tables 
WHERE table_schema = 'tech_edrops';
```

## Order of clearing tables

- BLOG_TEXT (can be done in any order)
- BLOG_POST
- BLOG
- BLOG_OWNER
- TOPIC
- LANGUAGE_CODE
- FLYWAY_SCHEMA_HISTORY (If existing)
