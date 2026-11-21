## Om lab

Dette er en heldigital lab og roterom. Min far hadde en slik fysisk labb på NTH.
Dette er mitt forsøk som ikke roter til hele hjemmekontoret.
Derfor vil det være mye rart og forskjellig her.
Alt er ikke ferdig og mye er under utvikling.

### Målet for labben

Det opprinnelige målet var bare ren eksperimentering.
Collatz var noe jeg laget som en fellesoppgave på tidligere jobb, alle skulle lage beste kode.
Fraction finner beste brøk, noe som er reimplementert fra studietiden. Algoritmen er bare fasinerende.
Graf er også reimplementert fra Turbo Pascal i helt ny drakt tilpasset web.
Calc grunnlaget ble lagt for 12 år siden med en Java kalkulator med litt for mange bugs,
men ideen kommer fra gamle HP RPN (Reverse Polish Notation) kalkulatorer.
Vi konkurrerte på gymnaset om å lage raskeste algoritme for primtall,
kameraten hadde en Texas Instruments kalkulator, jeg brukte HP.  

Jeg tror jeg vant på grunn av en litt smartere algoritme, HP kalkulatoren var ikke raskest. 
Legger av forståelige grunner ikke primtalls greiene ut på nett for å ikke overbelaste VPS. 
Kjernen er fortsatt ren RPN, men det er lagt en uttrykks-parser oppå.
RPN passer litt dårlig for en tjener basert løsning så denne delen er skjult,
inntil jeg løser det med WASM (Web assembly)

Planen er å lage simuleringer basert på fysiske lover (Newtons 2 lov etc.).
Det er en reimplementering av Turbo Pascal løsning for NTH til undervisning (ca 1989) 

### En utvidet kalkulator med plotteprogram og simuleringsmuligheter.

Dette er det egentlige målet. 

Ferdig: 
- Modul for å tegne akser og grafer med SVG.
- Matteparser med preparsing og en rekursivt basert parse algoritme
- RPN basert kalkulator (ikke GUI)
- Enkel funksjonsregner og kurveplott
- Kalkulator med avanserte mattefunksjoner og variable

Ikke ferdig, vil det noensinne blir ferdig? 
- Rent plott fra grunnlagsdata (regneark eller API), for eksempel for energiberegninger
- Flere kurver i samme graf
- Bedre integrasjon med blogg delen
- Predefinerte maler for funksjonsregner og simuleringer
- Lagring av maler i databasen i stedet
- Simuleringer backend, ikke i sanntid
- Sanntid tidsbaserte simuleringer i frontend (krever WASM)
- WASM kalkulator
- Visning av energidata og enkle simuleringer.


Siden prosjektet er ambisiøst, vil det ta tid å få alt på plass.
Siden dette er på internett, vil feilhåndtering, sikkerhet og ytelse være viktig.
Brukere, spesielt med innlogging og datalagring er en utfordring.
Foreløpig er alt bare åpent og anonymt og ikke lagret i database, så GDPR er unødvendig. 
Du oppfordres til å bruke cut & paste og notepad eller noe.

### En realitetsorientering

Hvis mål for det grønne skiftet ikke er forankret i matte og fysikk, samfunnsøkonomi og folk og bedrifter flest,
så må det gå i dass. Tanken er å bruke noe fra labben til å illustrere dette.
Når de klarer det kunststykket med heltidsansatte forskere og avanserte simuleringsprogrammer,
å fastslå i 2021 at strømprisen i Norge ikke kommer til å øke mer enn 2 øre på grunn av utenlandskablene,
så er det noe alvorlig galt. Søppel inn gir søppel ut, uansett hvor avanserte beregninger som 
er i midten. Virkemidlene i det helt nødvendige grønne skiftet er i for stor grad basert på
forutsetninger som ikke holder. Selv relativt enkle beregninger sammen med offentlig statistikk
kan vise det. Dessverre holder denne mangelen på realitetsorientering liv i en hel gjeng med
politikere, byråkrater, konsulenter og deler av kraftbransjen. Så det er vanskelig å snu, 
både i Norge og i EU generelt.  

Det er utenlandskablene sin skyld at dette prosjektet mitt kom i gang, dessverre. 



