## How to achieve maintainable computer systems.

This is just quick notes, to be expanded.  

I have as a software engineer for 40 years, developed all kinds of systems implemented in various languages:

Fortran, Visual Basic, C / C ++ / Pascal (several variants) / Java / Kotlin / SQL / JavaScript (not my favorite).
Including templating languages: HTML, JSP, JSF, Thymeleaf, Terraform, XML / XML-schemas, JSON, UML and scripting languages (various).   

Sorry I do not remember the details of all of them, when not used them for a while.
Too much tech mixed together does not make a maintainable system.

What characterizes maintainable computer systems?

- Select a technology stack that is well known for a lot of developers.
- Check licencing and maintenance of development tools and libraries.
- Split into maintainable code bases in centralized repo.
- Focus on consistent interfaces between systems.
  - Contract first is not always the best option, it can be too rigid.
  - Collaboration between teams is super important.
- Use Microservices, but sometimes it can be required with a common database.
- A product focused agile team maintaining the system(s).
  - Continuous development and maintenance of the system(s).
- A well planned and documented layered architecture:  Applies to both frontend and backend.
- A well planned deployment platform, cloud or other, to be selected by actual need and economics.
- A well planned deployment strategy.
- I suggest using containers.
  - latest version of operating system.
  - latest versions of computer language.
  - latest version of libraries.
  - latest security updates.
- Development done locally, not e.g. directly in cloud.
- Define multiple deployment platforms (development, test, production) according to needs.
- In large organizations: A platform team is required to support cross-cutting technical aspects
- A split of services into several systems / sub systems based on functionality
- Sensible modularization within each system (not too many modules)
- Cross-cutting concerns should be handled at one place, e.g. token checks in filter.
- Use well known patterns.
- Minimize state in a session.
- Do not be too rigid about using either functional style or object-oriented style. Both has is uses.
- Compact short code is not always the best (e.g. Regex). The concepts must be understood.
- Reactive asynchronous programming can be complex in Java/Kotlin. 
  - Reactive programming can be replaced with Spring Boot Virtual Threads from Java v 21.
  - Calling blocking code is not recommended from reactive code.
- Do not develop everything from scratch, frameworks like Spring Boot is desirable.
- Beware that the database is the most stable part of a system.
  - A lot of changes in the data model can be expensive.
  - A bad data model is expensive to fix, try to be thorough and do it right.
  - Select database type with care, it need not even be relational or can be combined with noSQL.
  - I warn about stored procedures. Only use then if absolutely required for performance.
  - Select database API with caution. Is an ORM like Hibernate / JPA really desired?
- Avoid including too many languages in the same project / system.
- In simple or internal web based systems, backend based HTML generation should be considered. 
  - It is not always the best idea to use frontend based code generation with e.g. React or Angular.
- Consistent use of well-designed and well-maintained libraries with little overlapping functionality.
  - Do not make an internal library if it cannot be maintained long-term by the organization, such as by platform team.
  - Many layers of libraries is undesired.
  - Plan for regular language and library updates.
  - Even if a new major library version requires code rewrite, the best is just to do it ASAP. Later it gets worse.
  - If a library is not maintained anymore, sooner or later you have to replace it.
- The system should be testable, manually and automated.
  - To plan for some manual or ad hoc tests is not necessarily wrong.
  - Plan for testing at the start of the lifetime of the system.
  - Think of what to test, relate  to the test pyramid.
  - Types of automated testing: unit tests, integration tests, system integration tests, performance tests, security tests.
  - Ideally testing should be done integrated with the code repo and in the same language.
  - Select test tools with care. The automated tests there must be maintained.
  - The tests increase quality and prevents errors, but binds the code and slows down initial development.
  - Identify critical parts of the codebase where extensive testing is required.
  - Code that change all the time is expensive to automate.
  - Consider  test complexity and maintainability. End-to-end tests with mocks tends to be complex.
  - In memory databases like H2 could be used if possible for integration tests.
  - An alternative is containers with the same database system as production.
  - Beware that test containers can be bad for automated tests included in the build.
  - Simple unit tests is most performant.
  - Extensive use of GUI tests and test tools is not always desired.
- Use automated code checks to company development standards, including security checks.
    - But it can be too rigid if too much of this.
- Use consistent error handling at the right level in the application, e.g. in controllers.
- Throwing exceptions is for technical errors, not functional exceptions.
- Implement a logging system, so errors is easily picked up by operations and developers.
- Use explainable module names, variable names, class names and method names.
- Documentation IS required, regardless of clean code.
  - In readme files saved within the codebase.
  - In code, write short comments to explain the unexpected.
  - In e.g. Javadoc (if a library).
  - Explaining functionality, architecture and principles (can e.g. use Wiki).
  - Setup guide for developers.
  - Code standard guide.
  - A developer can be replaced with another, at any time. Remember this when writing code without documenting.

***WARNING***  

The most difficult system to maintain:
- The coders started without a well-defined architecture and methodology
- If functional or technical knowledge is too limited in the current agile team, it can be dangerous.
- Too limited technical and functional documentation.
- Be careful with too much code generation, adds complexity
    - even worse, the team developed itself the code or GUI generation, e.g. from domain model or database.
- A too generic system or library developed and maintained.
- GUI, api, service, repository and database code mixed together in tight coupling.
- Too much loosely related functionality in one large legacy system.
- Ad hoc discontinuous development when the sponsor has got the money.
- To upgrade from very old framework versions can be costly but is required, also refer to security issues.
- Too many languages involved for too few developers. 
  - Watch out for one developer should know it all strategy: Backend, frontend, devops, iaC, database, stored procedure, cloud infrastructure, ...

And sorry, I have seen it all and even been on the project with the most difficult system to maintain.
Sometimes a rewrite or partly rewrite of the whole system is required because the legacy system is unmanageable.
Usually splitting up the system in several smaller systems with REST interfaces is the best.
