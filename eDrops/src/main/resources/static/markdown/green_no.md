## Grønn systemutvikling hva er det?

Det grønne skiftet er betegnet som det grå skiftet av kritikere, 
fordi det i praksis forbruker naturressurser i rasende tempo og ødelegger natur som binder C02.
Hvor godt faglig fundert er egentlig løsningene for å redde klima som vindturbiner, når det har denne bieffekten?
Det grå skiftet innebærer ganske mye kynisme, fordi kontrollen blir overført til store organisasjoner, som
tjener på grå tankegang. 

I sosiale medier bobler det over med oppgitthet over det grønne skiftet.
Hva kan du og jeg gjøre da? Økonomien for mange folk og småbedrifter er blitt dårligere. Det begrenser handlingsrommet.
Hva kan vi gjøre som helt vanlige utviklere?

Det er ett stikkord her som er helt vesentlig:

*BRUK MINDRE ENERGI, GJELDER OGSÅ IKT LØSNINGER*

Da slipper vi naturødeleggelsene. Mindre energiforbruk blir via en omregningsfaktor til mindre C02 utslipp, 
mye avhengig av hvor skitten produksjonen av elektrisk strøm er. Selv om strømmen fra ditt datasenter kommer
100% fra vannkraft, så fortrenger dette energibruk brukt til andre formål og dermed indirekte C02 utslipp.
Så jeg vil ikke snakke om redusert C02 utslipp som Microsoft gjør i sin kalkulator, men redusert energiforbruk.

DIGITAL TRANSFORMASJON

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
Dette er også pålagt for større prosjekter / organisasjoner, gjerne via ESG prinsipper (Environmental, Sosial, Goverance).

Først så koder vi noe, tester og retter, så blir systemet deployet til produksjon. 
Alternativt blir AI modellen først trent opp, så brukes prompt engineering til å få et resultat. 
Begge disse fasene forbruker energi. 
Det er vel kjent at trening av AI bruker energi, litt mindre vanlig å tenke på energiforbruk for en tradisjonell
systemutviklingsprosess. Uansett blir systemet satt i drift på tjenere, enten internt eller i skya. Det er
som regel testmiljøer som likner på produksjonsmiljø, som forbruker energi det og. Prosessering forbruker energi,
Datalagring forbruker energi. 
Dette er veldig avhengig av type datalagring, RAM, fillagring, database, semi permanent, permanent, arkiv, .. ).

* Totalt energiforbruk er summen av energiforbruket for hvert enkelt ledd i verdikjeden *

enten energiforbruket skyldes digitale eller manuelle verktøy. 
Det må også regnes med at produksjonsleddet blir utført mange ganger.
Det er energiforbruk knyttet til det fysiske miljøet i tillegg (kontoret, fabrikken, datasentret, internettet).

Dette kan for eksempel bety at et høyt energiforbruk pga AI trening og svar fra en AI tjeneste, likevel kan spare
energi totalt fordi man da klarer å optimalisere en industriprosess eller et kraftnett. Dette høres jo selvsagt ut.
Men det er et poeng at verktøy for å faktisk regne på dette ikke er så veldig tilgjengelige.

For å finne energiforbruket, så må det være målbart. Det mest nøyaktige for datasystemer er å måle forbruket direkte 
med utstyr på tjenere. I skya så er ikke dette så enkelt som det høres ut, fordi virtuell maskiner og containere kjører
en eller flere tjenere. Da kan jo flere helt uavhengig systemer være installert på den samme tjeneren.
Vi er uansett avhengig av skyleverandørens verktøykasse her.

Den andre metoden er å estimere energiforbruket basert på målt CPU og forbrukt minne. I Java finnes det APIer for å 
gjøre dette. Jeg har prøvd lokalt med Spring Boot og en embedded tjener, med å logge dette og summere opp.
Spesielt måling av CPU er så ustabilt at det anbefales ikke.

Eksempel på bruk av MXBean i Kotlin (Java):

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
```
Parameterene kan brukes  f.eks. ved logging av REST kall i et filter.  

Jeg anbefaler å droppe CPU målinger egentlig i koden over, for ga ikke meg noe. I så fall må beregne et gjennomsnitt.
Det viktige er å sjekke at minnebruken ikke blir for høy, og at den faktisk går ned igjen etterhvert etter
mye minnebruk. Det samme gjelder antall tråder. Om virtuelle tråder er i bruk vises også her.
Hvis antall tråder bare vokser er det feil programmering, det fikk jeg erfare en gang i hvertfall.
Jeg prøvde i Openshift (Kubernetes) rett og slett å avbryte REST kallet før minnet gikk i taket for en Container,
og returnere en feilmelding til klienten om at lasten var for stor. I dette tilfellet var det snakk om en stor 
engangs last. Erfaringen derfra var at dette heller ikke var noen vits i. Det var bedre å la Kubernetes vanlig
kontainerhåndtering fikse det, dvs. drepe containeren automatisk og ta opp en ny.

Vi optimaliserte tildelt minne og CPU i deployment konfigurasjon.
Det var noe gammel kode for filoverføring som var ekstremt ineffektivt.
Da måtte minnet skrus opp dessverre alt for mye.
Løsningen var rett og slett å skrive om hele koden for filoverføring og problemet var vekk.
I detalj implementasjon har det noe med hvor store biter av bitstrømmen som leses av gangen.
Her går det an å bruke reaktiv kode. Gikk helt fint med filstørrelser intil 2Gb.
Dette er omtrent grensen for hva JVM tåler. Men var dette en design feil egentlig? 
Hvorfor skal så store datamengder overføres i en klump?

Energiforbruk [kWh] = ((Σ(%c * Ec) + ΣEm) * PUE)

%c = % av CPU brukt (kan f.eks. være CPU, GPU eller TPU)
Ec = kWh forbrukt av all CPU
Em =  kWh forbrukt av minne 

PUE = Power Usage Effectiveness = Total energi brukt av datasentret / Energi brukt av IT-utstyr.
PUE = 1, all energi går til IT-utstyr, neppe oppnåelig.
PUE > 1.0. Dess høyere tall, desd mindre effektivt.

I stedet for den JVM baserte MxBean metoden beskrevet over, så bør  Docker Stats kommandoen brukes for kontainere. 
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
denne øvelsen. Jeg har ikke hatt tid eller mulighet. Alternativet her er sky baserte verktøy fra skyleverandører som
Azure, Google og Amazon for det samme. Men når Azure bare tilbyr et resultat som viser C02 utslipp, så har de bommet
etter min mening. Det er ikke denne omregningen som er det mest interessante. Men det er ikke å komme  unna at energi-
målinger er et komplekst fagområde.

En annen indirekte unøyaktig metode er rett og slett å se på kostnadene for å utvikle, teste og drifte datasystemet.
Vi kan være ganske sikre på at skyleverandøren ikke vil tilby oss en løsning med et kostandsnivå som ikke dekker
egne energikostnader.

## Generelle tips for energieffektiv utvikling

- Effektive algoritmer har lavere energiforbruk.
- Kode som eksekveres raskt uten ventetid har lavere energiforbruk.
- Enkel kode har ofte (men IKKE alltid) lavere energiforbruk.
- Unngå unødvendige beregninger, f.eks. med betinget logikk.
- Spring Boot Virtual Threads bør brukes. Vær ellers oppmerksom på trådhåndtering i koden.
- Asynkron signalbehandling har ofte lavere energiforbruk
- Pakking av data hjelper (f.eks. vanlige teknikker for web-frontend)
- Krav til datalagring må ses på (umiddelbart RAM, cache, disk, arkiv, ..)
- Minimer I/O operasjoner
- Optimaliser bruk av kontainere (max minne, max CPU, opp og nedskalering)
- Energivurder AI bruk

Det som er fint med dette, er at dette (som regel) er fullt i overenstemmelse med vanlige prinsipper for god og 
clean kode. Så det er dobbelt vinn: Enklere vedlikehold og lavere energiforbruk.
Valg av driftsplattform /skytjenester påvirker også dette, men gjøres jo som regel i forkant.

### AI eller ikke AI

For en del typer anvendelser så kan fuzzy tekstsøk være vel så bra som et søk i en AI modell.
Da brukes f.eks. "Elastic Search" eller fuzzy søk i objekter i databaser beregnet for større tekstmengder
(Både Oracle og PostgreSQL har dette).
Det er ikke alltid like gunstig heller med en naturlig språk modell for presentasjon av et resultat.
Det må vurderes i hvert enkelt tilfelle. AI bruk regnes generelt som det mest energikrevende.

Det er ikke mulig med "grønn" systemutvikling uten å ha et reflektert syn på hva som står her.
Dess større prosjekt, dess mer relevant er det å faktisk måle eller estimere totalt energiforbruk for en verdikjede.
Det alle utviklere kan gjøre er å se gjennom kodetipsene og legge til egen smart praksis.

### Valg av grensesnitt for effektiv prosessering

Det finnes ikke noe fasit svar her, det kommer an på anvendelsen.

| Metode        |  Energi effektivitet | Bruksområder                                                | Programmeringsmodell |
|---------------|---------------------:|-------------------------------------------------------------|----------------------|
| Synkron REST  |      Lav til moderat | Liten til middels datamengde, enkel request-response        | Enkel                |
| Asynkron REST |      Moderat til høy | Stor datamengde, request håndtering ikke blokkerende        | Noe mer kompleks     |
| Kafka         | Høy (med forebehold) | Stort datavolum, skalerbart, hendelsesdrevet, mer komplekst | Enda mer kompleks    |

Virtuelle tråder gjør synkron REST mer effektiv, spesielt ved stor last (mange kall fra ulike klienter).
Det finnes ikke noe fasitsvar her. For systemer som får inn mange meldinger,
og med store krav til ytelse og pålitelighet kan Kafka være fordelaktig.
Det må sies at jeg har enda ikke prøvd Kafka i praksis. Disse andre har jeg testet ut.

### Tilslutt noen konkrete kodetips:

Slik setter man "Virtual Threads" i Spring Boot i yaml fil:
```
spring.threads.virtual.enabled: true
```
Bruk ByteArray for I/O Operasjoner i stedet for å jobbe med tekststrenger.
```
fun readFile(file: File): ByteArray {
    return file.inputStream().use { it.readBytes() }
}

```
Bruk effektive Collection operasjoner og lambda uttrykk
```
// Inefficient: Creates multiple temporary lists
val result = list.map { it * 2 }.filter { it > 10 }

// Efficient: Using sequence to avoid unnecessary temporary collections
val efficientResult = list.asSequence().map { it * 2 }.filter { it > 10 }.toList()

```

Effektive løkker:
```
fun processList(items: List<Int>) {
    items.forEach {
        if (it == 5) return@forEach // Skips to the next iteration
        println(it)
    }
}

```

- Gjenbruk objekter i stedet for å rekreere dem.
- Bruk inline for små funksjoner.
- Velg datastruktur med omhu etter bruksområde. F.eks: Bruk Hashmap for raske oppslag, hvis rekkefølge er viktig LinkedHashMap
- Mye å spare på riktig SQL, konsistent datamodell og indeksering av tabeller
- Vurder relasjonsdatabase opp mot NO-SQL alternativer.
- For store spørringer / rapporter, ikke bruk JPA / ORM.
- Søk i tekstbeskrivelser krever helt egne metoder og datatyper i databaser, hvis dette er et krav, eventuelt elasticSearch.
- For JPA/ORM er det et helt eget tema for å optimalisere lesing og skriving av relaterte objekter i databasen.






