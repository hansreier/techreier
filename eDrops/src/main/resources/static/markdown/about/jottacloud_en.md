## Jottacloud is my primary backup service

### Backup of the entire VPS

This is done automatically every week by Hostinger.
But his backup service is too general, so I need more detailed backup services.

### Backup of directories, single files and database on the VPS.

Jottacloud was selected for this, partly because this is what I use for my private
PCs, and partly because database backup is relatively cheap using this provider.
Jottacloud is a Norwegian cloud provider with the data stored on Norwegian
servers. Jottacloud cli must be installed on Ubuntu to manage the backup process.
Cyberpanel backup could have been used for the database, but not that flexible 
and more expensive.

## Jottacloud backup

This section explains in detail how Jottacloud is used on my Hostinger VPS for backups.
The main purpose is database backup / SQL dump of MariaDb tables and contents.
In the future images can be synced from Jottacloud to a shared volume on the VPS for Docker too.
Manual backup of some important files to the Jottacloud archive is easy to do.

[Jottacloud CLI](https://docs.jottacloud.com/en/collections/178055-our-command-line-tool) is used, 
in addition to standard backup mechanisms of Jottacloud.
Please refer to [Jottacloud CLI commands](https://docs.jottacloud.com/en/collections/178055-our-command-line-tool).
Be aware of the 30-day storage limitation before Jottacloud removes old backups, this limitation 
can be a reason for looking at other cloud providers with similar services.
The VPS (Ubuntu) is set up to use the standard command line backup mechanism (mysqldunp) for the MariaDB database.
MariaDB is managed by Cyberpanel in my setup, and I could have used this paid service.
I prefer the Jottacloud backup, because it is more flexible and I already have paid for a Jottacloud account.
The Jottacloud archive can be used to avoid the 30-day limitation.
Jottacloud CLI can be used for long term storage to store in the archive. This solution is not set up yet.  

### Installation of the Jottacloud CLI

I have used the Jottacloud Ubuntu recipe. 
You need a [token](https://www.jottacloud.com/web/secure) to log in the first time
You have to name the backup device, I have called it techreier.

### Defining the backup service

The system and service manager for Linux is used (systemd).
Services set up to run automatically:  
- db-backup.service (service for database backup of MariaDB)
- db-backup.timer (when to run the db-backup.service)
- jottad-custom.service (service for sending backups to Jottacloud)

The files listed below is stored in the /etc/systemd/system folder.  

db-backup.service:
````
[Unit]
Description=Daily database backup

[Service]
Type=oneshot
ExecStart=/root/dbbackup.sh
````
dbbackup.sh is the bash script that performs the backup running mysqldump for MariaDB,
and the most complicated part of the solution.
- run mysqldump for the entire database
- save in a backup catalog db-backups
- delete old backups in db-backups after a given number of days
- jotta-cli handles the upload automatically because the db-backups folder is added  

dbbackup.sh is stored in the root users home folder.
The backups are stored in the db_backups folder for a given time period.
The Jottacloud deamon picks up changes there and copy changed files to the Jottacloud backup service.

db-backup.timer:
````
[Unit]
Description=Run database backup daily at 02:00

[Timer]
OnCalendar=*-*-* 02:00:00
Persistent=true

[Install]
WantedBy=timers.target
````  
jottad-custom.service:
````
[Unit]
Description=Jottacloud Custom Daemon
After=network-online.target
Wants=network-online.target

[Service]
ExecStart=/usr/bin/jottad
User=root
Type=simple
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target  
````
The backup folders are added using jotta-cli directly.
Some jotta-cli commands:
````
sudo jotta-cli login // Important to get started
sudo jotta-cli add /root/db_backups // Add folders to backup
sudo jotta-cli status // Get current status of jotta-cli
sudo jotta-cli scaninterval 15m // Interval of scanning files for backup.
sudo run-jottad // start the jottad deamon manually (not recommended, use systemctl instead)
````
It can be required to use sudo, even as a root user for jotta-cli.
The catalog /root/.jottad stores jotta cli sonfiguration and logs.
The file jottabackup.log is essential to study if a problem appears.
Note that this file can grow to be very big.
Documentation states that the file mostly will log errors.
If the file is too big, it can be deleted.
The safest is to restart the jottad daemon after delete.

Some important systemctl commands to instruct systemd:
````
// TIMER commands used for db-backup.timer
systemctl daemon-reload //always required after changes in files it uses
systemctl restart jottad-custom.service // start and stop the jottad deamon
systemctl enable db-backup.timer //mark timer to start on boot
systmctl start db-backup.timer //start timer immediately
systemctl restart db-backup.timer //restart timer immediately
systemctl status db-backup.timer //shows current timer status
journalctl -u db-backup.timer -xe //shows detailed log message for the timer

// SERVICE commands:
systemctl status jottad-custom.service //check status of the jottad service
systemctl status mariadb //check status of the mariadb service
systemctl start jottad-custom.service //manually start the jottad service
systemctl start db-backup.service //manually start the backup service
````
Systemd manages services and can be started, stopped and restarted independently.
Timers can be set up for scheduling the services.
Jottad-customer services does not use timer, because the jottad deamon runs all the time in the background.

db### Jottacloud manual archiving

The Jottacloud archive function is used. This stores the archived file in a permanent jottacloud archive.
The Jottacloud download function can be used for tranferring files into the VPS from archive.
For details, refer to Jottacload documentation. 

````
sudo jotta-cli archive db_backups --nogui --remote testkatalog2/backup //no GUI, use for automation
sudo jotta-cli archive db_backups --remote testkatalog2/backup //entire catalog
sudo jotta-cli archive dbbackup.sh //one file, contents written by default to techreier folder in archive
````

### Jottacloud automated archiving

The purpose of this is to set up a permanent backup. 
The Jottacloud backup service does not handle this since all backups are deleted
after 30 days and then moved to trash folder to be kept for another 30 days. 
The method is the same as above, but to set up a db-archive.service and db-archive.timer.
The service triggers /root/dbarchive.sh.  

dbarchive.sh is stored in the root users home folder.
The database backup are stored in the db_archive folder, until it is moved to the Jottacloud archive by the db-archive.service. 
Note that remaining files in this folder indicates that archiving at some point did not happen. 
Then you can copy the file manually to the Jottacloud archive.  

You can manually move files from Jottacloud backup to the Jottacloud archive using the manual web gui.
It is not with my knowlegde possible to automate this directly with jotta-cli. 
So the job is done simply by setting up a systemd archive service instead.  

The archive is never deleted, so have to delete files there manuelly.

### Defining the sync service

I plan to set this up for images, so the docker container can fetch this as a mounted volume. Not done.


