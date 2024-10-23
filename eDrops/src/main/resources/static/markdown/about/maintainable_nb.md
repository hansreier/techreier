## Hvordan oppnå vedlikeholdbare datasystemer

Dette er bare raske notater, vil legge til mer etterhvert.

Jeg har vært utvikler i 40 år, og utviklet alle slags systemer implementert i ulike språk:

Fortran, Visual Basic, C / C ++ / Pascal (flere varianter) / Java / Kotlin / SQL / JavaScript (ikke min favoritt).
Inkludert malbaserte språk: HTML, JSP, JSF, Thymeleaf, Terraform, XML / XML-schemas, JSON, UML and skript-språk (flere).   

Unnskyld jeg husker ikke detaljene på alle språkene, når jeg ikke har brukt dem på en stund.
For mye forskjellig teknologi blandet sammen gir ikke et vedlikeholdbart system.

Hva karakteriserer vedlikeholdbare data systemer?

- Velg en teknologi stakk som er velkjent for en hel masse utviklere.
- Sjekk lisenser og vedlikehold av utviklingverktøy og biblioteker.
- Del inn i vedlikeholdbare kode baser i et sentralisert repo.
- Jeg foretrekker Microtjenester, men noen ganger kan det være nødvendig med felles database.
- Fokuser på veldefinert grensesnitt mellom systemer.
  - Kontrakt først er ikke alltid den beste opsjonen, det kan bli for rigid.
  - Samarbeid mellom team er superviktig.
- En godt planlagt og dokumentert lagdelt arkitektur: Gjelder både for frontend og backend.
- Em godt planlagt driftsplattform, i skya eller annet, velges ut fra behov og økonomi.
- En godt planlagt idriftssetting strategi (DevOps)
- Start med å verifisere ditt valgte teknologi konsept.
  - Lag en kjørbar og testbar prototype gjennom alle lag av applikasjonen.
  - Vær løar pver at mock og tjeneste virtualisering kan skjule overraskelser ved integrasjoner.
  - Ikke velt for lenge før viktige deler av et system eller samarbeidende systemer integreres.
- Bruk kryss funksjonelle smidige produkt team som er dedikert for programvare vedlikehold av system(ene).
  - Kontinuerig utvikling og vedlikehold av system(ene).
- Jeg foreslår å bruke kontainere, f.eks. Docker.
  - siste stabile versjon av operativ system.
  - latest stabile version of operating system.
  - siste statile versjon av programmeringsspråk.
  - siste stabile versjon av biblioteker.
  - siste sikkerhets-oppdateringer.
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
  - Plan for testing at the start of the lifetime of the system.
  - Think of what to test, relate  to the test pyramid.
  - To do some manual or ad hoc testing is not necessarily wrong.
  - Types of automated testing: unit tests, integration tests, system integration tests, performance tests, security tests.
  - Performance test critical parts of the system, at least manually.
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
- Be careful with AI assisted development. 
    - I have mixed experience with GitHub Copilot, quite often my intentions is not what Copilot expects.
    - You can very quickly generate a lot of code more difficult to maintain with AI.
    - ChatGpt4 is better for explaining concepts and writing code examples.
    - The devil is in the details, this is where AI generated code often fails.
    - Proper testing and QA is always required when using AI generated code.
    - The source of the code is often unknown when using AI.
    - Obviously do not feed AI with business secrets, unless you are 100% certain it will not be used.
    - Follow company guidelines when using AI.
    - Using AI will get you up and running on unfamiliar tech more quickly.
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

***ADVARSEL***  

Det vanskeligste systemet å vedlikeholde:
- Koderne startet uten en godt definert arkitektur og metodikk.
- Hvis funksjonell eller teknisk kunnskap er for begrenset i et smidig team, så kan det være skummelt.
- For begrenset teknisk og funksjonell dokumentasjon.
- Vær forsiktig med for mye kodegenerering, øker kompleksiteten.
  - Enda værre, teamet utviklet selv kode- eller GUI generatoren, f.eks. fra domene modell eller database.
- Et for generisk system eller bibliotek er utviklet og vedlikeholdes.
- GUI, API, tjenester, forretningslogikk, data-aksesslag og database blandet sammen med for tett kopling.
- For mye løst koplet funksjonalitet koplet sammen i et stort gammeldags system. 
- Ad hoc diskontinuerlig utvikling når sponsoren har penger.
- Å oppgradere fra veldig gamle rammeverks-versjoner kan vært kostbart, men er nødvendig, også referer til sikkerhet.
- For mange språk involvert kombinert med for få utviklere.
  - Pass opp for "en utvikler skal kunne alt strategien": Backend, frontend, devops, iaC, database, stored procedure, cloud infrastructure, ...

Jeg har sett alt dette og til og med deltatt på prosjeket med det vanskeligste systemet å vedlikeholde.
Noen ganger er en fullstendig eller delvis omskriving av hele systemet nødvendig, for det gamle systemet er ikke lenge håndterbar.
Det som vanligvis er å anbefale er å splitte opp systemet i flere mindre systemer med REST grensesnitt.

