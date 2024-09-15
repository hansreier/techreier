## How to achieve maintainable computer systems.

This is just quick notes, to be expanded.  

I have as a software engineer for 40 years, developed all kinds of systems implemented in various languages:

Fortran, Visual Basic, C / C ++ / Pascal (several variants) / Java / Kotlin / JavaScript (not my favorite).
Including templating language: HTML, JSP, JSF, Thymeleaf, Terraform, XML / XML-schemas, JSON
And scripting languages (various).   

Sorry I do not remember the details of all of them, when not used them for a while.
Too much tech mixed together does not make a maintainable system.

What characterizes maintainable computer systems?

- Select a technology stack with enough available developers in the marketplace
- Check licencing and maintenance of development tools and libraries
- Split into maintainable code bases in centralized repo
- A product focused agile team maintaining the system(s)
  - Continuous development and maintenance of the system(s)
- A well planned and documented layered architecture:  Applies to both frontend and backend.
- A well planned deployment platform, cloud or other, to be selected by actual need and economics.
- A well planned deployment strategy
- I suggest using containers
  - latest version of operating system
  - latest versions of computer language
  - latest version of libraries.
  - latest security updates.
- Development done locally, not e.g. directly i cloud.
- Define multiple deployment platforms (development, test, production) according to needs.
- In large organizations: A platform team is required to support cross-cutting technical aspects
- A split of services into several systems / sub systems based on functionality
- Sensible modularization within each system (not too many modules)
- Cross-cutting concerns should be handled at one place, e.g. token checks in filter.
- Do not develop everything from scratch, frameworks like Spring Boot is desirable.
- Beware that the data and the database is the most stable part of the system, and a lot of changes can be expensive
- Not to many languages included in the same project / system or in collaborating microservices
- In simple and internal web based systems, backend based HTML generation should be considered. 
  - It is not always the best idea to use frontend based code generation with e.g. React or Angular.
- Consistent use of well-designed and well-maintained libraries with little overlapping functionality
  - Do not make an internal library if it cannot be maintained in the long term by the organization (e.g. by platform team)
  - Many layers of libraries is undesired
  - Plan for regular language and library updates
- The system should be testable, manually and automatic 
  - Plan for testing at the start of the lifetime of the system
  - Types of testing: unit tests, integration tests, system integration tests, performance tests.
  - Ideally testing should be done integrated with the code repo and in the same language
  - Select test tools with care. The automated tests there must be maintained
  - The tests increase quality and prevents errors, but binds the code and slows down initial development
  - Identify critical parts of the codebase where extensive testing is required
  - Code that change all the time is expensive to automate
  - Consider  test complexity and maintainability. End to end tests with mocks tends to be complex.
  - Extensive use of GUI test and test tools is not always desired.
- Use automated code checks to company development standards, including security checks.
    - But it can be too rigid if too much of this.
- Consistent error handling at the right level in the application
- A logging system, so errors is easily picked up.
- Use explainable module names, variable names, class names and method names.
- Documentation IS required, regardless of clean code.
  - In readme files saved within the codebase
  - In code, short comments to explain the unexpected
  - In e.g. Javadoc (if a library)
  - Explaining functionality, architecture and principles (can e.g. use Wiki)
  - Setup guide for developers
  - Code standard guide
  - A developer can be replaced with another, at any time. Remember this when documenting.

***WARNING***  

The most difficult system to maintain:
- The coders started without a well-defined architecture and methodology
- Be careful with too much code generation, adds complexity
    - even worse, the team developed itself the code and GUI generation, e.g. from domain model or database.
- A too generic system or library developed
- GUI, api, service and database code mixed together in tight coupling.
- Ad hoc discontinuous development when the sponsor have got the money
- Too many languages involved for too few developers. 
  - Watch out for one developer should know it all strategy. Backend, frontend, devops, iaC, cloud infrastructure

And sorry, I have seen it all and even been on the project with the most difficult system to maintain.
