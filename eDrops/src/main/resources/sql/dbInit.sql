CREATE DATABASE edrops;
CREATE USER dbuser@localhost IDENTIFIED BY 'password';

# or specify dbuser@localhost or dbuser@'host_ip_addess'. Note: Localhost cannot be used outside of docker.
# dbuser@'host.docker.internal' can be used as user for access fram docker container to mariaDB outside docker ?
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, SHOW VIEW ON edrops.* TO dbuser IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
