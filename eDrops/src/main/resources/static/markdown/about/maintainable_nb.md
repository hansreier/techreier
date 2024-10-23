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
- Utvikling skal gjøres lokalt, ikke f.eks. direkte i skya.
- Sett opp flere driftsplattformer (utvikling, test, produksjon) avhengig av behov.
- I store organisasjoner: Et plattform team er nødvendig for å støtte tekniske aspekter på tvers av systemer.
- Splitt opp tjenester i flere systemer / delsystemer basert på funksjonalitet.
- Fornuftig modularisering innenfor hvert system (ikke for mange moduler)
- Tekninske aspekter på tvers i et system, håndteres på et sted, f.eks. token sjekk i filter.
- Bruk velkjente mønstre.
- Minimer tilstand i en sesjon.
- Ikke vær for rigid med å bruke enten funksjonell eller objekt orienterert stil. Begge har sine bruksområder.
- Kompankt kort kode er ikke alltid det beste (f.eks. Regex). Konseptene må kunne forstås.
- Reaktiv asynkron programmering kan være kompleks i Java/Kotlin.
  - Reaktiv asynkron kode kan erstattes av Spring Boot virtuelle tråder fra Java v 21.
  - Å kalle blokkerende kode er ikke anbefalt fra reaktiv kode.
  - For parallell prosessering i Kotlin kan Coroutines benyttes (alternativ til Spring virtuelle tråder).
- Ikke utvikle alt fra bunn av, rammeverk som Spring Boot er fordelaktige.
- Vær klar over at data er den mest stabile delen av et system.
  - En hel masse endringer i en datamodell kan være kostbart.
  - En dårlig datamodell er kostbart å rette, prøv å være grundig og gjør det mest mulig riktig til å starte med.
  - Tenk grundig når du velger type database. Den trenger ikke engang være relasjonsdatabase, eller kan kombinseres med noSQL.
  - Select database type with care, it need not even be relational or can be combined with noSQL.
  - Jeg advarer mot lagrede prosedyrer. Bruk dem bare hvis det er absolutt nødvendig for ytelse.
  - Velg database API med omtanke. Er en ORM som Hibernate / JPA virkelig nødvendig?
- Unngå for mange programmeringspråk i det samme prosjektet / systemet.
- I enkle eller interne web applikasjoner, så kan backend basert HTML generering vurderes.
  - Det er ikke alltid best å bruke frontend basert HTML med f.eks. React eller Angular.
- Konsistent bruk av godt designed og godt vedlikeholdte biblioteker med lite overlappende funksjonalitet.
  - Ikke lag et internt bibliotekt hvis det ikke kan vedlikeholdes langsiktig av organisajonen, som av et plattform team.
  - Mange lag av biblioteker er ikke hensiktsmessig.
  - Planlegg for jevline språk- og biblioteks- oppgraderinger
  - Selv om en ny hovedversjon av et bibliotek krever kode omskriving, så er det beste å gjøre det mend en gang. Senere blir det værre.
  - Hvis et bibliotek ikke vedlikeholdes mer, så må det før eller senere ersatattes.
- Systemet skal være testbart, manuelt og automatisk.
  - Planlegg for testing helt i starten av livssyklusen for systemet.
  - Velg testverktøy med omhu. Testene der må vedlikeholdes.
  - Overdreven bruk av GUI tester og test verktøy er ikke alltid å foretrekke.
  - Tenk på hva som skal testes, relater det til test pyramiden.
  - Å teste noe manuele eller ad hov er ikke nødvendigvis feil.
  - Måter å teste automatisk på: Enhetstester, integrasjonstester, system integrasjons tester, ytelses tester, sikkerhets tester.
  - Ytelsestest kritiske delere av systemet, i det minste manuelt.
  - Automatiserte tester øker kodekvalitet og hinder  feil, men binder kode og kan forsinke rask utvikling.
  - Identifiser kritiske deler av koden, der grundig testing er nødvendig.
  - Kode, GUI eller logikk som endrer seg hele tiden er ikke noen spesielt god kandidat for testautomatisering.
  - Ideelt sett skal testkode være integrert med kode repoet og i det samme språket.
  - For tester integrert i koden:
    - Enkle enhetstester har best ytelse.
    - Vurder kompleksitet og vedlikeholdbarhet på tester. Ende-til-ende tester med mocks kan bli kompliserte.
    - Minnebaserte databaser som H2 kan hvis mulig med fordel benyttes for integrasjons tester.
    - Et alternativ for integrasjonstester er kontainere med det samme database system som produksjon.
    - Vær klar over at test kontainere ikke er veldig gunstig for automatiserte tester inkludert i bygget.
    - Skriv gjerne integrasjonstester uten mocks som ikke inkluderes i bygget.
- Bruk automatiske kode sjekker som følger kode standarder, inkludert sikkerhets sjekker.
    - Men det kan bli litt for rigid hvis det er for mye av det.
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
- Implementer et logge-system, så feil lett kan plukkes opp av drift og utviklere.
- Bruk selvforklarende modul navn, variable navn, klasse navn og metode navn.
- Dokumentasjon ER påkrevet, uavhengig av clean code.
  - I readme filer laget sammen med kodebasen.
  - I kode, skriv korte kommentarer for å forklare det uventede.
  - I f.eks. Javadoc (hvis det er et bibliotek).
  - Forklar funksjonalitet, arkitektur og prinsipper (kan f.eks. bruke Wiki)
  - Oppstarts guide for utviklere.
  - Kode standard guide.
  - En utvikler kan erstattes av en annen når som helst. Hust dette når du koder uten å dokumentere.

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

