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
- mariadb

I installed MariaDB locally on PC, and it was almost as easy to set up as H2.
It was very easy to connect using the Database connect possibilities in Intellij.
- Use ordinary Windows installer
- Change port to 3307 to not conflict with Docker default setup
- Define a new database schema:  edrops
- Define a new database user: dbuser
- Grant required permissions for dbuser.

Never use the root user and password for connection, but define a separate user: dbuser.  
Example of grant:
```
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, SHOW VIEW ON `edrops`.* TO `dbuser`@`localhost`
```