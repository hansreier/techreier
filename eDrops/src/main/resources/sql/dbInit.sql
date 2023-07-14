# Run statements in this script after installation of MariaDB database.
# or change user to dbuser@localhost or dbuser@'host_ip_addess'.
# dbuser@host.docker.internal can be used for access to dockerized MariaDB container.
CREATE DATABASE edrops;
CREATE USER dbuser IDENTIFIED BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, SHOW VIEW ON edrops.* TO dbuser IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
