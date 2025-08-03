## Zero Trust – When Security Itself becomes a problem

This started as a sensible security concept in the cloud,
where all sorts of services and components coexist and can in principle interact with one another.
This principle replaces or complements network segmentation with firewalls.
It’s no longer enough to trust what’s inside the wall. Instead, we trust no one, ever.

### Zero Trust – Practical Layers

1. **Devices**  
   Secured with Endpoint Protection, MDM, certificates, and registration.

2. **Network (TCP/IP)**  
   Segmentation, VPN, TLS, firewalls and IDS/IPS govern who gets access and how.

3. **Software Supply Chain**  
   Securing of everything from libraries to containers, through dependency scanning, CI/CD protection, and signing.

4. **Closed Services (internal APIs)**  
   Authentication (MFA, SSO, tokens), authorization, logging, and monitoring.

5. **Open Services (external APIs, banks, websites)**  
   MFA, tokens, authorization, consent, secure connection, and monitoring.  
   Secured both internally (by your own organization) and externally.

6. **Physical Location (office, company, country)**  
   Security controls and geopolitical risk may affect access.

### Consequences

The Zero Trust concept spans multiple practical levels,
but it’s easy to take it too far when all these layers are controlled and locked down.
The result is bureaucracy, inefficiency, poor collaboration, frustration, and little innovation.
Security training is often more effective than excessive restrictions – it teaches employees to make sound judgments.
If Zero Trust means distrust from leadership, employees will lose trust in leadership.  

From my experience as a developer, there’s too much focus on fine-grained access control and the principle of least privilege.
It often results in unnecessary blockers and time-wasters, for example when local development requires access to blocked websites.  

Keeping libraries up to date sounds simple, but it takes proper DevSecOps to work in practice.
We spend a lot of time on updates for vulnerabilities that might not even affect us.
Security and development rarely collaborate well; both sides lack insight into each other’s day-to-day work.  

Least privilege and Zero Trust require a highly capable organization to manage access.
If not, we risk building a poorer and more expensive system.
A dedicated platform team with a security focus can help, with a few predefined access packages for developers.  

Good security costs more and must be planned from the start, alongside testing.
Risk analysis should be done so that efforts are focused where they matter most.
There’s a cost before production and a bigger cost when something goes seriously wrong.
The worst is a system that's inaccessible to users,
whether the cause is overload, network failure, cyberattacks, human error, misconfigured access controls, or expired certificates.  

### Conclusion

***Proper security is not just about technology – it's about people, trust, and collaboration.***


