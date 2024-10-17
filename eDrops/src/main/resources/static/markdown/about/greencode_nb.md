## Energieffektiv kode

### Generelle tips for energieffektiv koding

- Se på hele IKT arkitekturen samlet, inkludert systemer og grensesnitt i verdikjeden.
- Identifiser energikritiske deler av koden (med dårlig ytelse) og optimaliser eller skriv på nytt.
- Optimaliser bruk av kontainere (max minne, max CPU, opp og nedskalering)
- Evaluer energibruk for AI, trening og vanlig bruk.
- Effektive algoritmer har lavere energiforbruk.
- Kode som eksekveres raskt uten ventetid har lavere energiforbruk.
- Enkel kode kan ha (men IKKE alltid) lavere energiforbruk. Verifiser i det minst.
- Fjern ubrukt kode og data
- Unngå unødvendige beregninger, betinget logikk kan brukes.
- Spring Boot Virtual Threads bør brukes. Vær ellers oppmerksom på trådhåndtering i koden.
- Reaktive (asynkron) objekthåndtering har ofte lavere energiforbruk
- Effektiv pakking av data hjelper
  - Vanlige teknikker for initiell last av web-frontend.
  - Digre ukomprimerte bilder? For ikke å snakke om animasjoner og video.
- Optimaliser datalagring(RAM, cache, disk, database, noSQL, midlertidig, semi permanent, permanent, backup
- Krav til datalagring må ses på (umiddelbart RAM, cache, disk, arkiv, ..)
- Trenger du virkelig å lagre dette?
- Minimer I/O operasjoner

Det som er fint med dette, er at dette (som regel) er fullt i overenstemmelse med vanlige prinsipper for god og
clean kode. Så det er dobbelt vinn: Enklere vedlikehold og lavere energiforbruk.
Valg av driftsplattform / skytjenester påvirker dette, men gjøres jo som regel i forkant.

### Et lite eksempel på optimalisering av overføring for store datamengder.

Et gammelt stor Java monolitt skulle flyttes over i skya.
Vi optimaliserte tildelt minne og CPU i deployment konfigurasjon av pod'er.
Det var gammel kode for filoverføring som var ekstremt ineffektivt (30MB maksimum)
Vi oppdaget problemene etter at hele systemet var flyttet over i skya.
Da måtte minnet skrus opp alt for mye dessverre. Uten dette fikk brukere i distriktet med dårlig nett problemer.
Løsningen var å skrive om kritiske deler av koden basert på nye APIer for filoverføring tilpasset nye Spring versjon.

Hvordan finne kritiske deler av koden? Det aller enkleste verktøyet er rett og slett vanlig logging med
f.eks. logback. Se eksempel på bruk av MXBean lenger ned på siden.
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

Et annet krav var mellomlagring i database. Etter å ha sjekket ut både et vanlig database API og et reaktiv API og
også lagring i BLOB type objekter, så kom vil til at en vanlig relasjonsdatabase ikke var egnet. Da ble det
lagret i et nøkkel/verdi lager i skya i stedet. Mye bedre ytelse viste det seg.

Men var dette 2GB kravet en spesifikasjons-feil egentlig?  Hvorfor skal Så store datamengder overføres i en klump?
Vi kan stille spørsmålstegn med hele arkitekturen som inkluderte flere systemer. I praksis var det ikke mer
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

| Metode        | Energi effektivitet  | Kapasitet | Mønster                            | Modell     |
|---------------|----------------------|-----------|------------------------------------|------------|
| Synkron REST  | Lav til medium       | Lav       | Request-response blokkerende       | Enkel      |
| Asynkron REST | Medium til høy       | Middels   | Request-response ikke blokkerende  | Middels    |
| Kafka         | Høy (med forebehold) | Høy       | Publish-subscribe skalerbar        | Kompleks   |

Virtuelle tråder gjør synkron REST mer effektiv og ikke like blokkerende, spesielt ved stor last (mange kall fra ulike klienter).
Vi kan spørre oss om trenger vi egentlig noe mer avansert enn synkron REST med virtuelle tråder.
Dette er jo den mest vanlige arkitekturen. For Spring 3 og framover så brukes typisk den nye RestClient for synkron Rest og WebFlux for 
asynkron REST. RestTemplate anbefales egentlig ikke og skal deprikeres, men fungerer fortsatt for eksisterende prosjekter.  

For systemer som får inn mange meldinger, og med store krav til ytelse og pålitelighet kan Kafka være fordelaktig.
Kafka har også innebygd feilhåndtering, replikering og lagring, som må programmeres manuelt for de ande typer grensesnitt.
Kafka er mer avhengig av hvordan den underliggende infrastrukturen er satt opp for hvor effektivt det er.
Det må sies at jeg har enda ikke prøvd Kafka i praksis. Disse andre har jeg testet ut.


### Konkrete kodetips

- Gjenbruk objekter i stedet for å rekreere dem.
- Bruk inline for små funksjoner i Kotlin.
- Velg datastruktur med omhu etter bruksområde. F.eks: Bruk Hashmap for raske oppslag, hvis rekkefølge er viktig LinkedHashMap
- Mye å spare på riktig SQL, konsistent datamodell og indeksering av tabeller
- Vurder relasjonsdatabase opp mot NO-SQL alternativer.
- Ordinær fillagring og filoverføring er tilsynelatende enkelt, men bør unngås, i hvertfall i skya.
- Valg av eventuell ORM teknologi som Spring Data JPA bør vurderes nøye
  - Mange alternativer tilbyr også enkle APIer for CRUD (Create, Read, Update, Delete) og er fortsatt type-sikkert.
  - For lagring i litt komplekse stukturer er ORM bra.
  - For store spørringer / rapporter, ikke bruk JPA / ORM.
  - Ytelses optimalisering av ORM kan være tidkrevende, hvis man ikke har erfaring
- Effektivt fri tekst søk krever helt egne metoder og datatyper i databaser.

#### Virtuelle tråder i JVM og Spring

Slik setter man "Virtual Threads" i Spring Boot i yaml fil:
```
spring.threads.virtual.enabled: true
```
#### I/O med mye data

Bruk ByteArray og ikke tekst strenger for I/O Operasjoner.
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
Det går også an å la være å filtrere hvis ikke nødvendig. Eksempel:
```
  val selectedCertifications = if (analyzeInactive) 
    certifications 
else 
    certifications.filterNot { s -> s.disabled }.toMutableSet()
```

Enda mer effektivt med Sequences:
```
val selectedCertifications = if (analyzeInactive) {
    certifications
} else {
    certifications.asSequence()
        .filterNot { it.disabled }
        .toMutableSet() // Evaluates lazily
}
```

For hvert trinn med en Collection type skapes det en ny midlertidig Collection.
Dess flere kjedede slike operasjoner med Collections, dess mindre effektivt.
Hvert element i en Sequence eksekveres gjennom hele kjeden,
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
Result:
``` 
16:25:36.723 [tomcat-handler-0] INFO GET / init=254MB, used=72MB, committed=88MB, max=4040MB, vthreads=26 
```  

Utviklingen observeres på denne måten,om det er vekst i minneforbruk eller antall tråder.
Tilsvarende kan gjøres for kritiske deler av koden generelt.

Jeg anbefaler å droppe CPU målinger i koden over, for ga ikke meg noe. I så fall må beregne et gjennomsnitt
over litt tid. Det viktige er å sjekke at minnebruken ikke blir for høy, og at den faktisk går ned igjen etterhvert etter
mye minnebruk. Det samme gjelder antall tråder. Om virtuelle tråder er i bruk vises også her.
Hvis antall tråder bare vokser er det feil programmering, det fikk jeg erfare en gang i hvertfall.

### Bruk av Docker Stats kommandoen og egen kontainer med tjeneste for å måle energiforbruk

I stedet for den JVM baserte MXBean metoden beskrevet over, så bør  Docker Stats kommandoen brukes for containere.
Denne gir mer stabile målinger.
```
docker stats
```
Resultat:
```
CONTAINER ID   NAME               CPU %     MEM USAGE / LIMIT     MEM %     NET I/O       BLOCK I/O   PIDS
5b0957e198f2   awesome_mahavira   1.24%     332.6MiB / 7.648GiB   4.25%     1.39kB / 0B   0B / 0B     34
```

Jeg tenker meg et oppsett i skya med en kontainer som kjører et API som kaller denne
og beregner energiforbruk på andre kontainere som er selve systemet. Det hadde vært morsomt å faktisk gjort
denne øvelsen. Jeg har ikke hatt tid eller mulighet.  
