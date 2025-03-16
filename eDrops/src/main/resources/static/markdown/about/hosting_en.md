## Docker web hosting

Docker was selected to be able to have complete freedom of software versions for Java and Tomcat and related libraries.
More and more hosting companies offer hosting of Docker containers.

### VPS or Cloud ?

Docker hosting is either VPS hosting or cloud hosting.
In general, you have less control if cloud hosting is selected and the yearly bill is usually more unpredictable and
higher. On the other hand, a VPS does not scale that well, and you are bound to one server location.
A VPS is also more limited when it comes to managing a lot of docker containers, e.g. with Kubernetes.
For a small personal project like this VPS was selected due to simplicity and cost control.
My usual customers have been large organizations or companies, then cloud hosting is preferred.

Remark that I have written the app with cloud in mind, so state should in general be kept as small as possible and
not kept between sessions. But you do not switch between containers in my simple VPS setup.

### To select a VPS vendor

A VPS (Virtual Private Server) can be managed or unmanaged.
My previous website was half managed, so I had log into the server and do some work myself.
The experience from the previous website was that updating Java and Tomcat required manual work.
SSL is now an absolute requirement. I had problems with the setup,
first manually, then bought as a service from the company. Renewal was a hassle.
A managed VPS is too expensive for my budget. So I selected to go unmanaged.
Remark that handling of a VPS, even unmanaged, can be more or less automated.
Adding a control panel to the VPS, can simplify the devops tasks.
I tried to find a company that offered unmanaged VPS with mostly automated processes, free control panels,
sufficient documentation, guides and customer service and not too expensive.

I gave up Norwegian vendors and the .no domain, because the VPS they offered was either
managed and expensive, or unmanaged with lack of documentation. So I feared I had to
set up everything on the server myself. After some search I decided to try Hostinger VPS with Cyberpanel.
The result was some manual work to set up the VPS, using mostly automated processes, and manageable. 
Since I used Cyberpanel, there is not that much need to actually log into the VPS server and perform Linux commands.

### To onboard my app

How I did it is briefly described here:

By automated processes initiated by GUI (initial setup, Hostingers HPanel and Cyberpanel):
- Selecting VPS plan including operating system, Cyberpanel and a new domain.
- Setting up VPS security (SSH, login, firewall ..)
- Defining my website in Cyberpanel
- Setting up server firewall in Cyberpanel
- Setting up Docker image(s) an Docker container(s) using Cyberpanel
- Setting up SSL Lets encrypt for my web site with Cyberpanel. Renewal are automatic.

A list of manual work I had to do on the server:
- Defining two child name servers for DNS resolution of my domain. Cyberpanel can probably do it, but did not understand it.
- Setting up a reverse proxy to point at the Docker container, else Cyberpanel landing page is viewed on techreier.com
- Some minor software update
The process that really should have been automated, but was not was reverse proxy.

### To set up a reverse proxy

Why? What is it?

It was caused by the same type of problems I had with the previous website and another hosting company.
To set up SSL was tricky, even with some help.
My previous company used Apache Web Server in front of Tomcat. So SSL should have been set up on Apache
that I did not control. I did not think of it first, but using CyberPanel actually added another web server to
my VPS: OpenLiteSpeed (fre version of LiteSpeed Web Server). With CyberPanel I could set up SSL, but it was actually
done on OpenLiteSpeed and the CyberPanel landing page that hijacked my domain techreier.com. 
Required of cause, since SSL was not initially set up on CyberPanel.
But still Http was exposed to the public on my Docker Container with app. 

After googling including the CyberPanel community, the simplest solution was to set up a reverse proxy. 
Then I could use the SSL issued by CyberPanel to protect my website. And it worked nearly as expected, but with some
manual configuration both on the VPS and in CyberPanel. The remaining problem was that http still was not blocked for
internet access. You can set up a firewall in CyberPanel. For an unknown reason, I could not block http port 8080 that
I used when starting the Docker container. But I managed to block it with another firewall set up at VPS level with the
HPanel. The reverse proxy point at the http and port set up when starting the Docker container.
Reverse proxy also required two child name servers to be set up, according to Hostinger guides.
Refer to next section.

One observation I did was that some functionality of the HPanel are duplicated by Cyber panel, and it was not
easy to understand what to use. I had to read several guides to understand it.

Alternative to reverse proxy:

To set up SSL with Lets Encrypt directly on the Docker container. But this does not automatically handle renewal
every 90 days of the SSL certificates. Certbot can be set up on another Docker container to handle this.
The whole process is more complex than the reverse proxy method, so I did not do it since CyberPanel provided 
issuing SSL certificates and automatic renewal.

### DNS resolution

This is the process of mapping a fixed IP address to a domain name. The process is time-consuming.
Every time you change DNS records, you have to repeat the process. I have done this twice.
The last time at Christmas it used more than 48 hours to populate my changes all over the world 
and letting every server know that techreier.com is associated with IP adress at Hostinger.

DNS resolution and create Nameserver can be configured with Cyberpanel and with the H2 panel combined. Confusing.
But again Hostinger guides helped me.

- Define NameServer, DNS Zone and DNS Records. Did it initially with Cyberpanel. But resulted in DNS not beeing resolved.
- So I did the same directly on the VPS server, including adding two child name servers in H-Panel. It worked.

What I did not understand initially was that I had to replace the original Hosting name servers with the two
child name servers I had defined. It was not explained anywhere in the guides. I think that the original
method actually had worked if I did this. Since the whole process of DNS resolution used about 48 hours,
I will not redo this unless I have to reset the entire VPS.

### Final words

Http, SSH and Docker setup I had done in August 2023. So I could reach my website unsecured from Internet.
I used a lot of hours during Christmas 2023 to set everything up correctly including domain resolution and SSL.
It was not that easy, but was helped with guides, AI assistance, Cyberpanel forum and customer service.
It is noted that the technical knowledge of my selected VPS solution including Docker was not very deep at customer service.
I guess that they are more used to serving shared web hosting. But they answered quickly and politely.

The process was actually not more time-consuming compared with what I did 15 years ago, with Tomcat installed directly
on a VPS. The technical solution is much better using Docker and Spring Boot embedded Tomcat server.
Java version upgrades and other upgrades are much easier to do.

Alternativ solutions I did not try:
- A lot of companies offers VPS with Docker.
- A lot of companies offers Cloud solutions with Docker
- Some solutions include a more automated deploy pipeline, could be an advantage (with and without Docker)
- Amazon (Docker or other solutions)
- Azure (Docker or other solution)
- IBM cloud (have tried automated Spring Boot deploy at IBM, no Docker)

Cloud payment models look a bit complex and unpredictable to me.
Overall I am satisfied with the solution well suited for a small private project.
Using e.g. Azure I risk to pay 5 times more than the current cost, when the trial period expires.
And it is not only an estimate, I have checked with other developers on similar projects.
What I have not done yet is installing MariaDb on the VPS.  

Preliminary solution uses H2.