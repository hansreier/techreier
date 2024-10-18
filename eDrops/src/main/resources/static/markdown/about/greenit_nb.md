## Grønn systemutvikling, hva er det?

Det grønne skiftet er betegnet som det grå skiftet av kritikere,
fordi det i praksis forbruker naturressurser i litt for stort tempo og ødelegger natur som binder C02.
Hvor godt faglig fundert er egentlig løsningene for å redde klima med vindturbiner, når det har denne bieffekten?
Det grønne skiftet innebærer ganske mye kynisme, fordi mye av kontrollen blir overført til store organisasjoner, som
tjener på grønn tankegang.

I sosiale medier bobler det over med oppgitthet over det grønne skiftet.
Hva kan du og jeg gjøre da? Økonomien for mange folk og småbedrifter er blitt dårligere. Det begrenser handlingsrommet.
Hvis vi som utviklere og IT arkitekter kunne hjelpe til med å lage mer energieffektive løsninger, så hadde det vært supert.
Det krever mye samarbeid og innovasjon på tvers av fagområder ut over ren IKT.

Digitalisering er ikke nødvendigvis bærekraftig i seg selv. Men det kan bli det hvis det gjøres riktig:
- Digital disrupsjon med energibesparende arbeidsmåter.
- Muliggjør jobb og tjenester hvor som helst.
- Automatisering og forenkling av manuelle prosesser
- Fjerne rutineoppgaver så man kan fokusere på mer verdiskapende arbeid.
- Minske energibruk og C02 utslipp ved smarte løsninger for styring av prosesser (IoT, data innsamling, analyse, ..)
- Vi må regne med miljø- og klimaavtrykket som digitalisering gir i det totale regnestykket.

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

### Energiforbruk ved digitale prosesser

En veldig forenklet generell verdikjede:

Manuell verdikjede:

☺ -> Manuell planlegging -> Oppskrift -> ☺   
☺ -> Manuelle operasjoner -> Resultat -> ☺

Heldigital verdikjede:

☺ -> Digital utvikling -> Datasystem -> ☺   
☺ -> Digitale operasjoner -> Resultat -> ☺

I praksis er disse verdikjedene mye lengre og involverer langt flere og som regel både automatiske og manuelle prosesser
i en blanding, men vi gjør det enkelt her.

Det er en grunn til at jeg har delt verdikjeden opp i to, der den siste er en produksjonskjede,
mens den første  innebærer utviklingsaktiviteter for verktøykassa (Digitale eller manuelle).
Slik er det jo i virkeligheten også, å planlegge på forhånd er noe som karakteriserer menneskelig aktivitet.
Oppgavene kan også være delvis digitalt utført, går ikke i slike detaljer i dette eksemplet.
Hvis inkludering av en digital operasjon ikke gir noen gevinst på noen måte målt av KPIer,
så er det selvsagt ingen vits. KPIer kan også relateres til FNs 17 bærekraftsmål.

Resultat inkluderer blant annet kost & nytte, energiforbruk, klima og miljøpåvirkning, nødvendig etterarbeid.
Dette er også pålagt for større prosjekter / organisasjoner, gjerne via ESG prinsipper (Environmental, Social, Governance).

Først så planlegger vi, koder, tester og retter, så blir systemet satt i produksjon.
Alternativt blir AI modellen først trent opp, så brukes prompt engineering til å få et resultat.
Begge disse fasene forbruker energi.
Det er vel kjent at trening av AI bruker mye energi, litt mindre vanlig å tenke på energiforbruk for en tradisjonell
systemutviklingsprosess. Uansett blir systemet satt i drift på tjenere, enten internt eller i skya. Det er
som regel testmiljøer som likner på produksjonsmiljø, som forbruker energi det og. Prosessering forbruker energi,
Datalagring forbruker energi, veldig avhengig av type datalagring: 
RAM, disk (SDD, HDD), fil-lagring, database, cache, semi permanent, permanent, backup.  

*Totalt energiforbruk er summen av energiforbruket for hvert enkelt ledd i verdikjeden*

Dette gjelder selvsagt uansett om prosessene i verdikjeden er manuell eller digitale. 
Produksjonskjeden forbruker energi hver gang den er i bruk.  

Det må også regnes med at produksjonsleddet blir utført mange ganger.
Det er energiforbruk knyttet til det fysiske lokasjonen og infrastruktur i tillegg:  Kontoret, fabrikken, datasentret, internett.  

Dette kan bety at et høyt energiforbruk pga AI trening og en AI tjeneste, likevel kan spare
energi totalt fordi man da klarer å optimalisere en industriprosess eller et kraftnett. Dette høres jo selvsagt ut.
Men det er et poeng at verktøy for å faktisk regne på dette ikke er så veldig tilgjengelige.

### Måling av energiforbruk

For å finne energiforbruket, så må det være målbart. Det mest nøyaktige for datasystemer er å måle forbruket direkte
med måleutstyr på fysiske tjenere. I skya så er ikke dette så enkelt som det høres ut, 
fordi relaterte virtuell maskiner og containere relatert til et system kan kjøre på ulike fysiske tjenere.
Da kan jo flere helt uavhengig systemer være installert på den samme tjeneren.
Vi er uansett avhengig av skyleverandørens verktøykasse her.

Den andre metoden er å estimere energiforbruket basert på målt CPU og forbrukt minne. I Java finnes det APIer for å
gjøre dette. Jeg har prøvd lokalt med Spring Boot med innebygget Tomcat, med å kalle APIet og beregne gjennomsnitt.
Spesielt måling av CPU er så ustabilt at det anbefales ikke. Se kodeeksempel lenger ned.

Energiforbruk [kWh] = ((Σ(%c * Ec) + ΣEm) * PUE)

%c = % av CPU brukt (kan f.eks. være CPU, GPU eller TPU)  
Ec = kWh forbrukt av all CPU  
Em =  kWh forbrukt av minne

PUE = Power Usage Effectiveness = Total energi brukt av datasentret / Energi brukt av IKT-utstyr.  
PUE = 1, all energi går til IKT-utstyr, neppe oppnåelig.  
PUE > 1.0. Jo høyere tall, dess mindre effektivt.

Et alternativet er sky baserte verktøy fra skyleverandører som  Azure, Google og Amazon for det samme.
Når Azure bare tilbyr et resultat som viser C02 utslipp, så har de bommet etter min mening.
Det er ikke denne omregningen som er det mest interessante. Men det er ikke å komme  unna at energi-
målinger er et komplekst fagområde uansett.

En annen indirekte svært unøyaktig metode er rett og slett å se på kostnadene for å utvikle, teste og drifte datasystemet.
Vi kan være ganske sikre på at skyleverandøren ikke vil tilby oss en løsning med et kostnadsnivå som ikke dekker
egne energikostnader.

## Konklusjon

Grønn systemutvikling er ikke mulig uten et helhetlig syn på verdikjeder, som også inkluderer IKT.
Dess større prosjekt, dess mer nødvendig er det å faktisk måle eller estimere totalt energiforbruk for en verdikjede.  

Det du som utvikler kan gjøre er å se gjennom [kodetipsene](greencode_nb.md) og legge til egen smart praksis.

En utfordring er at metodene for å faktisk måle resultater i form av redusert energiforbruk er for lite
utviklet og kjent blant utviklere, arkitekter og ledere.  

IKT-bransjen bør tenke på hvordan egne smarte løsninger er brukt internt.
Det er ingen fordel for eksempel for energiforbruk og C02 utslipp ved å tvinge utviklere tilbake til hovedkontoret.


