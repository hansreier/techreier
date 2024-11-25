## Databases, connections and Spring profiles

This project needs a database connection to run and uses Spring Boot standard configuration with profiles to
set up database connections.

Some parts of this webapp does not require a database. In the current setup a database connection at startup is
still required, due to the simple Spring Boot configuration. This markdown file is e.g. not stored in a database.
The blog part needs a database, even if markdown is used.

To select a Spring Boot database profile using environment variable:
SPRING_PROFILES_ACTIVE="profile".

## H2

H2 is mainly used for unit and integration tests.  

### About unit tests using Spring Boot testing and H2

In earlier versions the entire Spring Context was reloaded and tables recreated for every unit test.
In the new version the tables is cleared instead. Note that other database configuration is not cleared.
Identifier numbers will continue to increase through all the tests, so take care when doing asserts.  

Tables are created using Flyway. This can easily be changed in config, so Hibernate/JPA is used instead if desired.  

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

## MariaDB

MariaDB is selected as database to use in production. Mariadb is built on MySQL, but seems a better option
today due to licencing and other issues. PostgreSQL could be another option, but MariaDB has a smaller footprint,
is simpler and best suited for web applications. TODO: Temporarily H2 and the h2-prod profile is used in production.

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


## Order of clearing tables

- BLOG_TEXT (can be done in any order)
- BLOG_ENTRY
- BLOG
- BLOG_OWNER
- LANGUAGE_CODE
- FLYWAY_SCHEMA_HISTORY (If existing)
