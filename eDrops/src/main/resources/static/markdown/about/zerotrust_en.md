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

4. **Internal Services (APIs)**  
   Authentication (MFA, SSO, tokens), authorization, logging, and monitoring.

5. **Open Services (APIs, banks, websites)**  
   MFA, tokens, authorization, consent, secure connection, and monitoring.  
   Secured both internally (by your own organization) and externally.

6. **Physical Location (office, company, country)**  
   Security controls and geopolitical risk may affect access.

### Consequences

The Zero Trust concept spans multiple practical levels,
but it’s easy to take it too extremes when all these layers are controlled and locked down.
The result is bureaucracy, inefficiency, poor collaboration, frustration, and little innovation.
Security training is often more effective than excessive restrictions, it teaches employees to make sound judgments.
If Zero Trust means distrust from management, employees will lose trust in the management.  

From my experience as a developer, there’s too much focus on fine-grained access control and the principle of least privilege.
It often results in unnecessary blockers and time-wasters, for example when local development requires access to blocked websites.  

Keeping libraries up-to-date sounds simple, but it takes proper DevSecOps to work in practice.
We spend a lot of time on updates for vulnerabilities that might not even affect us.
Security and development rarely collaborate well; both sides lack insight into each other’s day-to-day work.  

Least privilege and Zero Trust require a highly capable organization to manage access.
If not, we risk building a poorer and more expensive system.
A dedicated platform team with a security focus can help, with a few predefined access packages for developers.  

Good security costs more and must be planned from the start, alongside testing.
Risk analysis should be carried out so that efforts are focused where they matter most.  

Security requirements must be adapted to the environment:
- Local development machines: Flexible requirements tailored to development work.
- Shared test environment: Stricter requirements when systems and test data are shared.
- Production: Very strict requirements to protect data and ensure stable operations.

Should developers have root access on their own machine?
Without root access, valuable development time can be lost. Updates are more likely to be skipped, which weakens security.
The risk of misuse is often nearly as high without root access.
Always lock your machine to prevent attackers from causing harm locally.  

There is a cost before production deployment and a much larger cost when something goes seriously wrong.

The two most critical security risks are:
- A system that is inaccessible to users
- Hidden serious flaws that suddenly emerge.

Causes of an inaccessible system can include failures in security and operational procedures leading to
overload, network failures, hacker attacks, access control errors, or expired certificates.
Note that overly rigid security rules can sometimes themselves cause lockouts.

Hidden serious flaws can be due to design errors or poor code, which attackers can exploit.
Developers should receive security training focusing on OWASP and Zero Trust.
Being aware of different injection patterns is essential, as well as thorough QA, testing, and logging.
These aspects are far more important than Zero Trust measures aimed at blocking developers.

### Conclusion

***Proper security is not just about technology – it's about people, trust, and collaboration.***


