## Grønn systemutvikling hva er det?

Det grønne skiftet er også betegnet som det grå skiftet av kritikere, 
fordi det i praksis forbruker naturressurser i rasende tempo og ødelegger natur som binder C02.
Hvor godt faglig fundert er egentlig løsningene for å redde klima som vindturbiner, når det har denne bieffekten?
Det grå skiftet innebærer ganske mye kynisme, fordi kontrollen blir overført til store organisasjoner, som
tjener på grå tankegang. 

I sosiale medier bobler det over med oppgitthet over det grønne skiftet.
Hva kan du og meg gjøre da? Økonomien for mange folk og småbedrifter er blitt dårligere. Det begrenser handlingsrommet.
Hva kan vi gjøre som helt vanlige utviklere?

Det er ett stikkord her som er helt vesentlig:

*BRUK MINDRE ENERGI, GJELDER OGSÅ IKT LØSNINGER*

Da slipper vi naturødeleggelsene. Mindre energiforbruk blir via en omregningsfaktor til mindre C02 utslipp, 
mye avhengig av hvor skitten produksjonen av elektrisk strøm er. Selv om strømmen fra ditt datasenter kommer
100% fra vannkraft, så fortrenger dette energibruk brukt til andre formål og dermed inndirekte C02 utslipp.
Så jeg vil ikke snakke om redusert C02 utslipp som Microsoft gjør i sin kalkulator, men redusert energiforbruk.

DIGITAL TRANSFORMASJON

En veldig forenklet generell verdikjede:

Manuell verdikjede:

Menneske .> Manuell planlegging -> Verktøy -> Menneske ->
Menneske -> Manuell opppgave med verktøy -> Resultat -> Menneske

Heldigital verdikjede:

Menneske -> Digital utvikling   -> Datasystem -> Menneske ->
Menneske -> Digital operasjoner i datasystem -> Resultat -> Menneske

I praksis er disse verdikjedene mye lengre og involverer langt flere og som regel både automatiske og manuelle prosesser
i en blanding, men vi gjør det enkelt her.  

Det er en grunn til at jeg har delt verdikjeden opp i to, der den siste er en produksjonskjede, 
mens den første  innebærer utviklingsaktiviteter for verktøykassa (Digitale eller manuelle).
Slik er det jo i virkeligheten også, det er gjerne det som karakteriserer menneskelig aktivitet.
Oppgavene kan også være delvis digitalt utført, går ikke i slike detaljer i dette eksemplet.
Hvis ikke involveringen av en digital operasjon gir noen gevinst på noen måte i form av KPIer,
så er det selvsagt ingen vits. KPIer kan også relateres til FNs 17 bærekraftsmål, og  eksemplet.

Resultat inkluderer blant annet kost & nytte, energiforbruk, klima og miljøpåvirkning, nødvendig etterarbeid dette
er også pålagt for større prosjekter / organisasjoner, gjerne via ESG prinsipper (Environmental, Sosial, Goverance).

Først så koder vi noe, så blir systemet deployet til produksjon. Alternativt blir AI modellen først trent opp, 
så brukes prompt engineering til å få et resultat. Begge disse fasene forbruker energi. 
Det er vel kjent at trening av AI bruker energi, litt mindre vanlig å tenke på energiforbruk for en tradisjonell
systemutviklingsprosess. Uansett blir systemet satt i drift på servere, enten internt eller i skya. Det er også
som regel testmiljøer som likner på produksjonsmiljø, som forbruker energi det også. Prosessering forbruker energi,
Datalagring forbruker energi (veldig avhengig av type datalagring, minne, semi permanent, permanent, arkiv, .. ).

* Totalt energiforbruk er summen av energiforbruket for hvert enkelt ledd i verdikjeden *

enten energiforbruket skyldes digitale eller manuelle verktøy. 
Det må også regnes med at produksjonsleddet blir utført mange ganger.
Det er energiforbruk knyttet til det fysiske miljøet i tillegg (kontoret, fabrikken, datasentret, internettet).

Dette kan for eksempel bety at et høyt energiforbruk pga AI trening og svar fra en AI tjeneste, likevel kan spare
energi totalt fordi man da klarer å optimalisere en industriprosess eller et kraftnett. Dette høres jo selvsagt ut.
Men det er et poeng at verktøy for å faktisk finne ut av dette ikke er så veldig tilgjengelige.

For å finne energiforbruket, så må det være målbart. Det mest nøyaktige for datasystemer er å måle forbruket direkte 
med utstyr på servere. I skya så er ikke dette så enkelt som det høres ut, fordi virtuell maskiner og containere kjører
en eller flere servere. Da kan jo flere helt uavhengig systemer være installert på den samme tjeneren.
Vi er uansett avhengig av skyleverandørens verktøykasse her.

Den andre metoden er å estimere energiforbruket basert på målt CPU og forbrukt minne. I Java finnes det APIer for å 
gjøre dette. Jeg har prøvd lokalt med Spring Boot og en embedded tjener, med å logge dette og summere opp.
Spesielt måling av CPU er så ustabilt at det anbefales ikke.

Energiforbruk [kWh] = ((Pc * Cc + Pm + Pg* Cg) * Pue)/ 1000

Pc = % av CPU brukt
Cc = kWh forbrukt av all CPU
Pg = % of CPU brukt av GPU
Pm =  kWh forbrukt av minne
Cg =  kWh forbrukt av all GPU
Pue = Power Usage Effectiveness

Den andre bedre metoden er Docker Stats kommandoen, som gir noe mer stabile CPU målinger.
```
docker stats
```
Resultat eksempel:
```
CONTAINER ID   NAME               CPU %     MEM USAGE / LIMIT     MEM %     NET I/O       BLOCK I/O   PIDS
5b0957e198f2   awesome_mahavira   1.24%     332.6MiB / 7.648GiB   4.25%     1.39kB / 0B   0B / 0B     34
```

Jeg tenker meg der et oppsett i skya med en container som kjører et API som kaller denne.

denne og beregner energiforbruk på andre containere som er selve systemet. Det hadde vært morsomt å faktisk gjort
denne øvelsen. Jeg har ikke hatt tid eller mulighet. Alternativet her er sky baserte verktøy fra skyleverandører som
Azure, Google og Amazon for det samme. Men når Azure bare tilbyr et resultat som viser C02 utslipp, så har de bommet
etter min mening. Det er ikke denne omregningen som er det mest interssante. Men det er ikke å komme  unna at energi-
målinger er et komplekst fagområde.

En annen inndirekte unøyaktig metode er rett og slett å se på kostnadene for å utvikle, teste og drifte datasystemet.
Vi kan være ganske sikre på at skyleverandøren ikke vil tilby oss en løsning med et kostandsnivå som ikke dekker
egne energikostnader.

Tilslutt noen kodetips:

- Effektive algoritmer har lavere energiforbruk
- Kode som eksekveres raskt har lavere energiforbruk
- Enkel kode har ofte (men IKKE alltid) lavere energiforbruk.
- Spring Boot Virtual Threads bør brukes. Vær ellers oppmerksom på trådhåndtering i koden.
- Asynkron signalbehandling har ofte lavere energiforbruk
- Pakking av data hjelper (f.eks. vanlige teknikker for web-frontend)
- Krav til datalagring må ses på (umiddelbart RAM, disk, arkiv, ..)

Det som er fint med dette, er at dette (som regel) er fullt i overenstemmelse med vanlige prinsipper for god og 
clean kode. Så det er dobbelt vinn: Enklere vedlikehold og lavere energiforbruk.

Det er ikke mulig med "grønn" systemutvikling uten å ha et reflektert syn på hva som står her.
Dess større prosjekt, dess mer relevant er det å faktisk måle eller estimere totalt energiforbruk for en verdikjede.
Det alle utviklere kan gjøre er å se gjennom kodetipsene og legge til egen enkel smart praksis.

Generell effektivisering av grensesnitt:

| Metode        | Energi effektivitet | Bruksområder                                                                    | 
|---------------|--------------------:|---------------------------------------------------------------------------------|
| Synkron REST  |     Moderat til lav | Lite datamengde, enkel request-response                                         | 
| Asyncron REST |                 Høy | Stor datamengde, samtidig request håndtering ikke blokkerende                   | 
| Kafka         |      Høy (med sidee | Stor datamangde, skalerbart, hendelsesdrevet arkitektur, infrastruktur overhead | 

Virtual Threads gjør synkron REST mer effektiv.


Slik setter man Virtual Threads i Spring Boot i yaml fil:
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
- Velg datastruktur med omhu etter bruksområde. Bruk Hashmap for raske oppslag, hvis rekkefølge er viktig LinkedHashMap
- Mye å spare på riktig SQL og indeksering av tabeller
- Vurder relasjonsdatabase opp mot NO-SQL alternativer.
- For store spørringer / rapporter, ikke bruk JPA / ORM.
- For JPA/ORM er det et helt eget tema for å optimalisere.






