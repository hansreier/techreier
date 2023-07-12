## Databases, connections and Spring profiles

This project needs a database connection to start and uses Spring Boot standard configuration with profiles to
set up database connections.

Some parts of this webapp does not require a database. In the current setup a database connection at startup is
still required, due to the simple Spring Boot configuration. This markdown file is e.g. not stored in a database.
The blog part needs a database, even if markdown is used.

To select a Spring Boot database profile using environment variable:
SPRING_PROFILES_ACTIVE="profile".

## H2

H2 is mainly used for unit and integration tests

Spring boot profiles:
- h2: in memory profile
- h2disk: disk file storage. Current Docker setup is without mounting of the database files

H2 could even be used with the disk option in production for simplicity, but it is only recommended for
reading database instances and not writing by many clients. One simple option would be to mount the h2 database
files (volume or bind) to ensure permanent storage. This is not tested.

## MariaDB

MariaDB is selected as database to use in production. Mariadb is built on MySQL, but seems a better option
today due to licencing and other issues. PostgreSQL could be another option, but MariaDB has a smaller footprint,
is simpler and best suited for web applications. 

Spring boot profiles:
- mariadb - connect locally using mariadb installed on development PC
- mariadocker - connect locally using mariadb installed in docker container on development PC.

I installed MariaDB locally on PC, and it was almost as easy to set up as H2.
It was very easy to connect using the Database connect possibilities in Intellij.
- Use ordinary Windows installer
- Make sure that no port conflicts exist on the PC. If so, change port from default 3306.
- Define a new database schema:  edrops
- Define a new database user: dbuser
- Grant required permissions for dbuser.

As an alternative use MariaDb official image from Dockerhub. Create a container. Remember to enter port 
and set MARIADB_ROOT_PASSWORD as environment variable when using Docker Desktop or docker command.

Never use the root user and password for connection, but define a separate user: dbuser.
I have used an environment variable DB_PASSWORD, to avoid checking in db user password to github.  

Example of defining database schema, dbuser and granting priveleges is included in the dbinit.sql file.
Note that if you connect to dockerized MariaDB with dbuser outside of MariaDB container, 
you must allow for general external access when granting dbuser (or use IP address of local machine).
To run statements in this script is all you need to start the application with a fresh MariaDb instance.
Database tables if not existing are created automagically by Hibernate, and tables is populated with some initial data.

Note: How this is done with two docker containers running, MariaDB Docker container and application Docker container, is 
not yet tested !!

To connect from container to db-service on the host:
```
 url: jdbc:mariadb://host.docker.internal:3306/edrops?useSSL=false&useUnicode=yes&characterEncoding=UTF-8&serverTimezone=UTC
```
This only works using Docker Desktop. Docker Desktop cannot be run in Host mode, only in Brigde Mode (default).
Verify and eventuelly change port number!
