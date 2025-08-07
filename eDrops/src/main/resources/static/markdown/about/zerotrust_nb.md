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

En problemstilling er om utviklere skal ha root-tilgang på egen maskin.
Uten rottilgang kan mye utviklingstid gå tapt, inkludert nødvendig installasjon av programvare.
Risiko for misbruk ofte er nesten like stor uten rot tilgang.
Lås uansett maskinen, så kan ikke angriper gjøre ugang via den lokale maskinen.

Men det er en kostnad før produksjonssetting og en større kostnad når noe går alvorlig galt.

Det to alvorligste sikkerhetsrisikoen er
- Et system som ikke kan nås av brukerne
- Skjulte alvorlige feil som plutselig oppstår

Årsak til ikke nåbart system kan være svikt i sikkerhets- og drifts-rutiner som kan gi
overbelastning, nettverksfeil, hackerangrep, feil med tilgangskontroll eller utgåtte sertifikater.

Skjulte alvorlige feil kan skyldes feildesign eller dårlig kode, som kan utnyttes av en angriper.
Utviklere bør ha sikkerhetsopplæring med fokus på OWASP og Zero Trust.
Det å være klar over ulike injection pattern er essensielt, samt grundig QA, testing og logging.
Dette er mye viktigere enn Zero Trust rettet mot utviklere i form av blokkeringer.

### Konklusjon

***God sikkerhet handler ikke bare om teknologi, men også om mennesker, tillit og samarbeid.***



