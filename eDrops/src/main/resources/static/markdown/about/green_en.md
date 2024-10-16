## Green software development, what is it?

Sorry, the rest in Norwegian. TODO.

Det grønne skiftet er betegnet som det grå skiftet av kritikere, 
fordi det i praksis forbruker naturressurser i rasende tempo og ødelegger natur som binder C02.
Hvor godt faglig fundert er egentlig løsningene for å redde klima med vindturbiner, når det har denne bieffekten?
Det grå skiftet innebærer ganske mye kynisme, fordi kontrollen blir overført til store organisasjoner, som
tjener på grønn tankegang. 

I sosiale medier bobler det over med oppgitthet over det grønne skiftet.
Hva kan du og jeg gjøre da? Økonomien for mange folk og småbedrifter er blitt dårligere. Det begrenser handlingsrommet.
Hvis vi som utviklere og IT arkitekter kunne hjelpe til med å lage mer energieffektive løsninger, så hadde det vært supert.
Det krever mye samarbeid og innovasjon på tvers av fagområder ut over ren IKT.

Digitalisering er ikke nødvendigvis bærekraftig i seg selv. Men det kan bli det hvis det gjøres riktig:
- Digital disrupsjon for energibesparende arbeidsmåter
- Muliggjør jobb og tjenester hvor som helst
- Automatisering og forenkling av manuelle prosesser
- Fjerne rutineoppgaver så man kan fokusere på viktigere ting.
- Minske miljø- og C02 utslipp ved smarte løsninger for styring av prosesser (AI, big data, ..)
- Må regne med miljø- og klimaavtrykket som digitalisering gir i det totale regnestykket.

En felle det er lett å falle i er å endre hver enkelt manuelle delprosess til noe som støttes digitalt,
uten å tenke helheten. Spesielt for offentlig forvaltning som er veldig regelstyrt dette en stor utfordring.  

Det finnes ingen god definisjon på hva "grønn" systemutvikling er. Men jeg oppfordrer alle i bransjen
til å tenke litt selv hva det faktisk innebærer. Pinnepunktene over gir en pekepinn. 
Et viktig poeng er at reduksjon i energiforbruk ved hjelp av IKT, faktisk må kunne måles. Uten dette blir det
vanskelig å sette inn tiltak på riktig sted.

## Råd til utviklere og IT arkitekter

Hva kan vi gjøre som helt vanlige utviklere?

Det er ett stikkord som er helt vesentlig:

*BRUK MINDRE ENERGI, GJELDER OGSÅ IKT LØSNINGER*

Da slipper vi naturødeleggelsene. Mindre energiforbruk blir via en omregningsfaktor til mindre C02 utslipp, 
mye avhengig av hvor skitten produksjonen av elektrisk strøm er. Selv om strømmen fra ditt datasenter kommer
100% fra vannkraft, så fortrenger dette energibruk brukt til andre formål og dermed indirekte C02 utslipp.
Så jeg vil ikke snakke om redusert C02 utslipp som Microsoft gjør i sin kalkulator, men redusert energiforbruk.

### Energiforbruk ved digital transformasjon

En veldig forenklet generell verdikjede:

Manuell verdikjede:

☺ -> Manuell planlegging -> Verktøy -> ☺ ->  
☺ -> Manuell opppgave med verktøy -> Resultat -> ☺   

Heldigital verdikjede:

☺ -> Digital utvikling   -> Datasystem -> ☺ ->  
☺ -> Digital operasjoner i datasystem -> Resultat -> ☺  

I praksis er disse verdikjedene mye lengre og involverer langt flere og som regel både automatiske og manuelle prosesser
i en blanding, men vi gjør det enkelt her.  

Det er en grunn til at jeg har delt verdikjeden opp i to, der den siste er en produksjonskjede, 
mens den første  innebærer utviklingsaktiviteter for verktøykassa (Digitale eller manuelle).
Slik er det jo i virkeligheten også, det er gjerne det som karakteriserer menneskelig aktivitet.
Oppgavene kan også være delvis digitalt utført, går ikke i slike detaljer i dette eksemplet.
Hvis ikke involveringen av en digital operasjon gir noen gevinst på noen måte i form av KPIer,
så er det selvsagt ingen vits. KPIer kan også relateres til FNs 17 bærekraftsmål.

Resultat inkluderer blant annet kost & nytte, energiforbruk, klima og miljøpåvirkning, nødvendig etterarbeid. 
Dette er også pålagt for større prosjekter / organisasjoner, gjerne via ESG prinsipper (Environmental, Social, Governance).

Først så koder vi noe, tester og retter, så blir systemet satt i produksjon.
Alternativt blir AI modellen først trent opp, så brukes prompt engineering til å få et resultat. 
Begge disse fasene forbruker energi. 
Det er vel kjent at trening av AI bruker mye energi, litt mindre vanlig å tenke på energiforbruk for en tradisjonell
systemutviklingsprosess. Uansett blir systemet satt i drift på tjenere, enten internt eller i skya. Det er
som regel testmiljøer som likner på produksjonsmiljø, som forbruker energi det og. Prosessering forbruker energi,
Datalagring forbruker energi. 
Dette er veldig avhengig av type datalagring, RAM, fillagring, database, semi permanent, permanent, arkiv, .. ).

*Totalt energiforbruk er summen av energiforbruket for hvert enkelt ledd i verdikjeden*  

enten energiforbruket skyldes digitale eller manuelle verktøy.  

Det må også regnes med at produksjonsleddet blir utført mange ganger.
Det er energiforbruk knyttet til det fysiske miljøet i tillegg (kontoret, fabrikken, datasentret, internettet).  

Dette kan for eksempel bety at et høyt energiforbruk pga AI trening og svar fra en AI tjeneste, likevel kan spare
energi totalt fordi man da klarer å optimalisere en industriprosess eller et kraftnett. Dette høres jo selvsagt ut.
Men det er et poeng at verktøy for å faktisk regne på dette ikke er så veldig tilgjengelige.  

### Måling av energiforbruk

For å finne energiforbruket, så må det være målbart. Det mest nøyaktige for datasystemer er å måle forbruket direkte 
med måleutstyr på tjenere. I skya så er ikke dette så enkelt som det høres ut, fordi virtuell maskiner og containere kjører
en eller flere tjenere. Da kan jo flere helt uavhengig systemer være installert på den samme tjeneren.
Vi er uansett avhengig av skyleverandørens verktøykasse her.  

Den andre metoden er å estimere energiforbruket basert på målt CPU og forbrukt minne. I Java finnes det APIer for å 
gjøre dette. Jeg har prøvd lokalt med Spring Boot med innebygget Tomcat, med å logge dette og summere opp.
Spesielt måling av CPU er så ustabilt at det anbefales ikke. Se kodeeksempel lenger ned.  

Energiforbruk [kWh] = ((Σ(%c * Ec) + ΣEm) * PUE)

%c = % av CPU brukt (kan f.eks. være CPU, GPU eller TPU)  
Ec = kWh forbrukt av all CPU  
Em =  kWh forbrukt av minne  

PUE = Power Usage Effectiveness = Total energi brukt av datasentret / Energi brukt av IT-utstyr.  
PUE = 1, all energi går til IT-utstyr, neppe oppnåelig.  
PUE > 1.0. Dess høyere tall, desd mindre effektivt.  

Et alternativet er sky baserte verktøy fra skyleverandører som  Azure, Google og Amazon for det samme. 
Når Azure bare tilbyr et resultat som viser C02 utslipp, så har de bommet etter min mening. 
Det er ikke denne omregningen som er det mest interessante. Men det er ikke å komme  unna at energi-
målinger er et komplekst fagområde uansett.  

En annen indirekte svært unøyaktig metode er rett og slett å se på kostnadene for å utvikle, teste og drifte datasystemet.
Vi kan være ganske sikre på at skyleverandøren ikke vil tilby oss en løsning med et kostnadsnivå som ikke dekker
egne energikostnader.  

## Konklusjon

Det er ikke mulig med "grønn" systemutvikling uten å ha et reflektert syn på hele verdikjeder, som også inkluderer IKT.
Dess større prosjekt, dess mer relevant er det å faktisk måle eller estimere totalt energiforbruk for en verdikjede.
Det alle utviklere kan gjøre er å se gjennom kodetipsene under og legge til egen smart praksis. 

En utfordring er at metodene for å faktisk måle resultater i form av redusert energiforbruk er for lite
utviklet og kjent blant utviklere, arkitekter og ledere.
IKT-bransjen bør tenke på hvordan egne smarte løsninger utnyttes på best mulig måte i egne prosesser. 
Det er ingen fordel for eksempel for energiforbruk og C02 utslipp ved å tvinge utviklere tilbake til hovedkontoret.  

## Generelle tips for energieffektiv utvikling

- Se på hele arkitekturen samlet, inkludert flere systemer og grensesnitt.
- Identifiser energikritiske deler av koden (med dårlig ytelse) og optimaliser eller skriv på nytt.
- Optimaliser bruk av kontainere (max minne, max CPU, opp og nedskalering)
- Energivurder AI bruk
- Effektive algoritmer har lavere energiforbruk.
- Kode som eksekveres raskt uten ventetid har lavere energiforbruk.
- Enkel kode kan ha (men IKKE alltid) lavere energiforbruk.
- Unngå unødvendige beregninger, betinget logikk kan brukes.
- Spring Boot Virtual Threads bør brukes. Vær ellers oppmerksom på trådhåndtering i koden.
- Asynkron signalbehandling har ofte lavere energiforbruk
- Pakking av data hjelper (f.eks. vanlige teknikker for web-frontend)
- Krav til datalagring må ses på (umiddelbart RAM, cache, disk, arkiv, ..)
- Minimer I/O operasjoner

Det som er fint med dette, er at dette (som regel) er fullt i overenstemmelse med vanlige prinsipper for god og 
clean kode. Så det er dobbelt vinn: Enklere vedlikehold og lavere energiforbruk.
Valg av driftsplattform / skytjenester påvirker også dette, men gjøres jo som regel i forkant.  

### Et lite eksempel på optimalisering av overføring for store datamengder.

Et gammelt stor Java monolitt skulle flyttes over i skya.
Vi optimaliserte tildelt minne og CPU i deployment konfigurasjon av pod'er.
Det var gammel kode for filoverføring som var ekstremt ineffektivt.
Vi oppdaget problemene etter at hele systemet var flyttet over i skya.
Da måtte minnet skrus opp alt for mye dessverre. Uten dette fikk brukere i distriktet med dårlig nett problemer.
Løsningen var å skrive om kritiske deler av koden basert på nye APIer for filoverføring tilpasset nye Spring versjon.  

Hvordan finne kritiske deler av koden? Det aller enkleste verktøyet er rett og slett vanlig logging med
f.eks. logback. Se eksempel på bruk av MxBean lenger ned på siden. 
Så kan logge nivået  settes til debug eller trace senere når ikke relevant lenger. 
En vanlig stoppeklokke kan også brukes ved en GUI operasjon (eller bruk et GUI testverkøy).
Hvis responsen tar mer enn 2-3  sekunder fra GUI, så er det ikke bra. 
Profil-verktøy for Java / JVM kan også brukes for dette, men det er dyrere.  

I et senere prosjekt, testet jeg ut ny bedre kode for filoverføring av store datamengder.
Reaktiv kode i Java ble testet ut som fikset det med filstørrelser inntil 2GB. 
2GB er omtrent grensen for hva ordinær streng håndtering tåler (Char Array). Er det større en det krever det
spesialhåndtering. Det er viktig å tenke på også at biblioteker vi bruker må støtte det.
Nye versioner av Spring Boot og REST grensnitt takler ganske store datamengder omtrent inntil den grensen,
riktig konfigurert viste det seg.  Parametre som hvor store biter av bitstrømmen som leses av gangen, settes for optimalisering.
Den gamle koden hos forrige kunde taklet forresten ikke noe særlig mer enn 30MB.

Et annet krav var mellomlagring i database. Etter å ha sjekket ut både et vanlig database API og et reaktiv API og 
også lagring i BLOB type objekter, så kom vil til at en vanlig relasjonsdatabase ikke var egnet. Da ble det
lagret i et nøkkel/verdi lager i skya i stedet. Mye bedre ytelse viste det seg.  

Men var dette 2GB kravet en spesifikasjons-feil egentlig?  Hvorfor skal Så store datamengder overføres i en klump?
Her kan vi stille spørsmålstegn med hele arkitekturen som inkluderte flere systemer. I praksis var det ikke mer 
enn 200-300 MB. Det løste mye av problemet.

### Energieffektive miljøer med kontainer teknologi

Å bruke Kubernetes basert teknologi kan anbefales (men fikk i praksis mye hjelp av plattform team og overbygg).
Jeg jobbet også med en mer nettverksnær tjeneste i Azure (Azure App services), 
men denne var ikke nødvendigvis enklere  å sette opp med Ci/Cd og virtuelle nett (VNet).  

Optimaliser kontainere ved å minimere max minne, max CPU og parametre for opp og nedskalering av antall kontainere.  

Jeg prøvde i Openshift (Kubernetes) rett og slett å avbryte REST kallet før minnet gikk i taket for en pod,
og returnere en feilmelding til klienten om at lasten var for stor. I dette tilfellet var det snakk om en stor
engangs-last. Erfaringen derfra var at dette heller ikke var noen vits i. Det var bedre å la Kubernetes vanlig
kontainerhåndtering fikse det, dvs. drepe containeren automatisk og ta opp en ny.  

Ofte kan visuelle verktøy som Grafana være en stor hjelp her.  

### AI eller ikke AI

For en del typer anvendelser så kan fuzzy tekstsøk være vel så bra som et søk i en AI modell med "Prompt engineering".
Da brukes f.eks. "Elastic Search" eller fuzzy søk i objekter i databaser beregnet for større tekstmengder
(Både Oracle og PostgreSQL har dette).
Det er ikke alltid like gunstig heller med en naturlig språk modell for presentasjon av et resultat.
Det passer best for mennesker og ikke for videre maskinell behandling.
Det må vurderes i hvert enkelt tilfelle. AI trening og bruk regnes generelt som det mest energikrevende.  

Men det er viktig å se på hele verdikjeden når AI vurderes.  

### Valg av grensesnitt for effektiv prosessering

Det finnes ikke noe fasit svar. Det kommer an på anvendelsen.  

| Metode        |  Energi effektivitet | Bruksområder                                                | Programmeringsmodell |
|---------------|---------------------:|-------------------------------------------------------------|----------------------|
| Synkron REST  |      Lav til moderat | Liten til middels datamengde, enkel request-response        | Enkel                |
| Asynkron REST |      Moderat til høy | Stor datamengde, request håndtering ikke blokkerende        | Noe mer kompleks     |
| Kafka         | Høy (med forebehold) | Stort datavolum, skalerbart, hendelsesdrevet, mer komplekst | Enda mer kompleks    |

Virtuelle tråder gjør synkron REST mer effektiv, spesielt ved stor last (mange kall fra ulike klienter). 
For systemer som får inn mange meldinger,
og med store krav til ytelse og pålitelighet kan Kafka være fordelaktig.
Det må sies at jeg har enda ikke prøvd Kafka i praksis. Disse andre har jeg testet ut.  


### Konkrete kodetips

- Gjenbruk objekter i stedet for å rekreere dem.
- Bruk inline for små funksjoner i Kotlin.
- Velg datastruktur med omhu etter bruksområde. F.eks: Bruk Hashmap for raske oppslag, hvis rekkefølge er viktig LinkedHashMap
- Mye å spare på riktig SQL, konsistent datamodell og indeksering av tabeller
- Vurder relasjonsdatabase opp mot NO-SQL alternativer.
- Ordinær fillagring og filoverføring er tilsynelatende enkelt, men bør unngås, i hvertfall i skya.
- Valg av eventuell ORM teknologi som Spring Data JPA bør vurderes nøye
  - Mange alternativer tilbyr også enkle APIer for CRUD (Create, Read, Update, Delete)
  - For lagring i litt komplekse stukturer er ORM bra.
  - For store spørringer / rapporter, ikke bruk JPA / ORM.
  - Ytelses optimalisering av ORM kan være tidkrevende, hvis man ikke har erfaring
- Søk i lange tekstbeskrivelser krever helt egne metoder og datatyper i databaser.

#### Virtuelle tråder i JVM og Spring

Slik setter man "Virtual Threads" i Spring Boot i yaml fil:
```
spring.threads.virtual.enabled: true
```
#### I/O med mye data

Bruk ByteArray for I/O Operasjoner i stedet for å jobbe med tekststrenger.
```
fun readFile(file: File): ByteArray {
    return file.inputStream().use { it.readBytes() }
}
```

#### Effektive Collection operasjoner og lambda uttrykk

Minst effektivt alternativ:
```
val selectedCertifications = certifications.filter { s -> analyzeInactive || !s.disabled }.toMutableSet()

```
Det går også an å la være å filtere hvis ikke nødvendig. Eksempel:
```
  val selectedCertifications = if (analyzeInactive) 
    certifications 
else 
    certifications.filterNot { s -> s.disabled }.toMutableSet()
```

Enda mer effektivt med sequences:
```
val selectedCertifications = if (analyzeInactive) {
    certifications
} else {
    certifications.asSequence()
        .filterNot { it.disabled }
        .toMutableSet() // Evaluates lazily
}
```

For hvert trinn med en collection type skapes det nye midlertidig collections.
Dess flere kjedede slike operasjoner med Collections, dess mindre effektivt.
Hvert element i en sequence eksekveres gjennom hele kjeden,
beregning skjer helt i det siste trinnet hvis mulig. 
Dette er en fordel for store datasett, ikke helt slik for små.
Men trenger vi virkelig en Mutable Collection her. Det er et annet spørsmål.  

#### Effektive løkker i Kotlin

Den mest effektive varianten med forEach:
```
fun processList(items: List<Int>) {
    items.forEach {
        if (it == 5) return@forEach // Skips to the next iteration
        println(it)
    }
}
```

Den mest effektive varianten uansett er en tradisjonell for loop:
```
fun processList(items: List<Int>) {
    for (i in items) {
         if (i == 3) break // Skips right out of the loop
            println(i)
    }
}
```

Her er det noe med ekstra lambda abstraksjonslag og funksjonell kode som er litt mindre effektivt. 
Hvis det betyr noe i praksis da. Jeg minner om at lambdaer blir oversatt til slike strukturer uansett.
Det er jo en trend å gå mot funksjonell programmering, som i mange tilfeller gir mer konsis kode.
Akkurat her synes jeg heller ikke det. Men det er nok ikke dette jeg ville ha fokusert på for å energioptimalisere.  

#### Bruk av MXBean i Kotlin (Java)

Hensikten er å kunne logge minneforbruk, antall tråder og eventuelt CPU.  

```
fun mem(): MB {
    val memory: MemoryMXBean = ManagementFactory.getMemoryMXBean()
    val threads: ThreadMXBean = ManagementFactory.getThreadMXBean()
    // val cpu: OperatingSystemMXBean= ManagementFactory.getOperatingSystemMXBean()
    val mem = MB(
        memory.heapMemoryUsage.init,
        memory.heapMemoryUsage.used,
        memory.heapMemoryUsage.committed,
        memory.heapMemoryUsage.max,
        // cpu.systemCpuLoad * 100 //Percentage
        threads.threadCount,
        Thread.currentThread().isVirtual
    )
    return mem
}

data class MB(
    val init: Long, val used: Long, val committed: Long, val max: Long, val threads: Int,
    val virtual: Boolean
) {
    override fun toString(): String {
        return "init=${init / MBYTE}MB, used=${used / MBYTE}MB, committed=${committed / MBYTE}MB" +
                ", max=${max / MBYTE}MB, ${if (virtual) "vthreads=" else "threads="}$threads"
    }
}
```  

Parameterene kan brukes  f.eks. ved logging av REST kall i et filter.
``` 
logger.info("${req.method} ${req.servletPath} ${mem()}")
```
Resultat:
``` 
16:25:36.723 [tomcat-handler-0] INFO GET / init=254MB, used=72MB, committed=88MB, max=4040MB, vthreads=26 
```  

Utviklingen observeres på denne måten,om det er vekst i minneforbruk eller antall tråder.
Tilsvarende kan gjøres for kritiske deler av koden generelt.  

Jeg anbefaler å droppe CPU målinger i koden over, for ga ikke meg noe. I så fall må beregne et gjennomsnitt
over litt tid. Det viktige er å sjekke at minnebruken ikke blir for høy, og at den faktisk går ned igjen etterhvert etter
mye minnebruk. Det samme gjelder antall tråder. Om virtuelle tråder er i bruk vises også her.
Hvis antall tråder bare vokser er det feil programmering, det fikk jeg erfare en gang i hvertfall.  

### Bruk av Docker Stats kommandoen og egen kontainer for å måle energiforbruk

I stedet for den JVM baserte MxBean metoden beskrevet over, så bør  Docker Stats kommandoen brukes for containere.
Denne gir mer stabile målinger.  
```
docker stats
```
Resultat eksempel:
```
CONTAINER ID   NAME               CPU %     MEM USAGE / LIMIT     MEM %     NET I/O       BLOCK I/O   PIDS
5b0957e198f2   awesome_mahavira   1.24%     332.6MiB / 7.648GiB   4.25%     1.39kB / 0B   0B / 0B     34
```

Jeg tenker meg et oppsett i skya med en kontainer som kjører et API som kaller denne
og beregner energiforbruk på andre kontainere som er selve systemet. Det hadde vært morsomt å faktisk gjort
denne øvelsen. Jeg har ikke hatt tid eller mulighet.  




