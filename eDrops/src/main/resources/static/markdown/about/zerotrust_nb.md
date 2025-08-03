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
   Segmentering, VPN, TLS, brannmurer og IDS/IPS styrer hvem og hvordan.

3. **Programvareforsyningskjeden (software supply chain)**  
   Sikring av hva som installeres og kjøres fra biblioteker til containere, gjennom dependency-scanning, CI/CD-sikring og signering.

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
Det fører ofte til blokkeringer og tidstyver, for eksempel når lokal utvikling krever tilgang til sperrede nettsteder.  

Å holde biblioteker oppdatert høres enkelt ut, men krever god DevSecOps for å fungere i praksis.
Vi bruker mye tid på oppdateringer for sikkerhetshull som kanskje ikke berører oss.
Samarbeidet mellom sikkerhet og utvikling fungerer sjelden godt; begge parter forstår for lite av hverandres hverdag.  

Minste privilegium og Zero Trust krever en svært oppegående organisasjon for å administrere tilgang.
Hvis ikke risikerer vi et dårligere og dyrere system.
Et eget plattformteam med sikkerhetsfokus kan hjelpe, med noen få forhåndsdefinerte tilgangspakker for utviklere.  

God sikkerhet koster mer og må planlegges inn fra starten sammen med testing.
Risikoanalyse bør gjøres slik at innsatsen settes inn der det teller.
Det er en kostnad før produksjonssetting og en større kostnad når noe går alvorlig galt.
Det verste er et system som ikke kan nås av brukerne.
Uansett om årsak er overbelastning, nettverksfeil, hackerangrep, menneskelig svikt, feil tilgangskontroll eller utgåtte sertifikater.  

### Konklusjon

***God sikkerhet handler ikke bare om teknologi, men også om mennesker, tillit og samarbeid.***



