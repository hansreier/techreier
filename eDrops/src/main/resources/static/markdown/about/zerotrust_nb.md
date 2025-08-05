## Zero Trust – Når sikkerhet blir et problem i seg selv

Dette har dukket opp som et fornuftig sikkerhetskonsept i skyen, fordi alt mulig rart av tjenester og objekter 
surrer rundt og kan i prinsippet ta kontakt med hverandre.
Dette prinsippet erstatter eller supplerer nettverkssegmentering med brannmurer.
Det er altså ikke lenger nok at vi stoler på alt som har kommet innenfor denne muren,
men vi stoler ikke på noen. Aldri.

### Zero trust – praktiske nivåer

1. **Enheter**  
   Sikring med Endpoint Protection, MDM, sertifikater og registrering.

2. **Nettverk (TCP/IP)**  
   Segmentering, VPN, TLS, brannmurer og IDS/IPS styrer hvem som får tilgang og på hvilken måte.

3. **Programvareforsyningskjeden (software supply chain)**  
   Sikring av alt fra biblioteker til containere, gjennom dependency-scanning, CI/CD-sikring og signering.

4. **Lukkede tjenester (interne APIer)**  
   Autentisering (MFA, SSO, token), autorisasjon, logging og overvåkning.

5. **Åpne tjenester (eksterne APIer, banker, nettsider)**  
   MFA, token, autorisasjon, samtykke, sikker tilkobling og overvåkning. Sikring både internt (egen organisasjon kan sperre) og eksternt.

6. **Fysisk lokasjon (kontor, firma, land)**  
   Sikkerhetskontroll og geopolitisk risiko kan påvirke tilgang.

### Konsekvenser

Zero Trust-konseptet omfatter flere praktiske nivåer,
men det er lett å dra det for langt når alle disse lagene skal kontrolleres og sikres.
Resultat er byråkrati, ineffektivitet, dårlig samhandling, mistrivsel og lite innovasjon.
Sikkerhetsopplæring er ofte bedre enn overdreven sperring, da lærer medarbeidere å ta fornuftige vurderinger.
Hvis Zero Trust betyr mistillit fra ledelsen, mister ansatte tilliten til ledelsen.  

Fra mine erfaringer som utvikler legges det for mye vekt på detaljert tilgangskontroll og minste privilegiums-prinsipp.
Det fører ofte til unødvendige blokkeringer og tidstyver, som når lokal utvikling krever tilgang til sperrede nettsteder.  

Å holde biblioteker oppdatert høres enkelt ut, men krever god DevSecOps for å fungere i praksis.
Vi bruker mye tid på oppdateringer for sikkerhetshull som kanskje ikke berører oss.
Samarbeidet mellom sikkerhet og utvikling fungerer sjelden godt; begge parter forstår for lite av hverandres hverdag.  

Minste privilegium og Zero Trust krever en svært oppegående organisasjon for å administrere tilgang.
Hvis ikke risikerer vi et dårligere og dyrere system.
Et eget plattformteam med sikkerhetsfokus kan hjelpe, med noen få forhåndsdefinerte tilgangspakker for utviklere.  

God sikkerhet koster mer og må planlegges inn fra starten sammen med testing.
Risikoanalyse bør gjøres slik at innsatsen settes inn der det teller.
Vi bør skille mellom
- Lokal utviklingsmaskin: 
- Felles testmiljø: Midt i mellom strengt.
- Produksjon: Svært strenge krav er på sin plass.

En problemstilling er om utviklere skal ha rot tilgang til egen maskin.
Egen erfaring er dessverre at mye utviklingtid ofte går tapt har jeg ikke det.
Installasjon av programvare er kanskje det største problemet.
Det krever mye innsats og omtanke av felles IT hvis alt det som en utvikler trenger skal installeres felles.
Hovedproblemet er om noen kan komme inn med rot tilgangen og skaffe seg tilgang til andre maskiner eller systemer.
Jeg påstår at problemet i praksis er nesten like stort uten rot tilgang. 

Men det er en kostnad før produksjonssetting og en større kostnad når noe går alvorlig galt.

Det to alvorligste sikkerhetsrisikoen er
- Et system som ikke kan nås av brukerne
- Skjulte alvorlige feil som plutselig oppstår

Årsak til feilet aksess kan være overbelastning, nettverksfeil, hackerangrep, menneskelig svikt, 
feil tilgangskontroll eller utgåtte sertifikater. 
Feilen kan også være relatert til mer omfattende sikkerhetsrutiner enn det som er håndterbart.

Årsak til alvorlige feil kan være design eller kode feil, eller en plantet feil av en angriper.
Mye av kan hindres med sikkerhetsfokus og kunnskap hos utviklere.
Det er mye bedre med utviklere med OWASP og Zero Trust fokus på egen kode, 
enn rigid Zero Trust anvendt på utviklerne som om de ikke er ansvarlige eller kan noe.
Det å være klar over ulike injection pattern og WCAG er viktig (HTML-injection, SQL injection)
Kode QA er svært viktig og fornuftig logging.
Flere nivåer av sikkerhet gjør det vanskeligere for en angiper.
Mye kan oppdages i test, unntakstester er viktig.
En regresjonstest kan oppdage uønskede endringer i koden.

### Konklusjon

***God sikkerhet handler ikke bare om teknologi, men også om mennesker, tillit og samarbeid.***



