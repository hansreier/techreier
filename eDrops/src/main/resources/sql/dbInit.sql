CREATE DATABASE edrops;
CREATE USER dbuser@localhost IDENTIFIED BY 'password';

# or specify dbuser@localhost or dbuser@'host_ip_addess'. Note: Localhost cannot be used outside of docker.
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, SHOW VIEW ON edrops.* TO dbuser IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
