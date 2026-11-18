Plotting av funksjoner

- Første kjøring: Standard aksegrenser
- Neste: Dynamiske min/maks-grenser

Enkle antakelser for aksestreker og tekster.

### Målet for prosjektet

- Modul for å tegne akser og grafer med SVG (utført)
- Modul for matematiske funksjoner / RPN-basert (utført)
- Matematisk funksjon settes inn av bruker med min og max verdier
- Import fra regneark eller API, for eksempel for energiberegninger
- Vurdere å lagre data i databasen for plotting
- Tidsbaserte simuleringer (krever WASM)

Beregninger kjører på backend.
Klient-side WASM for live-animasjon senere.
Jeg tror at akseprogrammet kan brukes som bakgrunn for WASM-animasjon.

Siden prosjektet er ambisiøst, vil det ta tid å få alt på plass på fritiden.
Siden dette er på internett, vil feilhåndtering og sikkerhet være viktig.

### Om parseren

Deler:
- Preparse: Enkle syntakssjekker og tokenisering
- Parse: Omorganisering av tokens ved hjelp av en rekursiv parser til en instruksjonsliste for RPN
- Calculate: Utføre RPN-beregninger på stakken ved hjelp av RPN-instruksjonslisten

Parseren returnerer en kompilert liste med tokens som skal brukes til påfølgende beregninger.

Til bruk for simuleringer har jeg utvidet kalkulatoren til å kunne gjøre sekvensiell parsing og beregning basert på flere uttrykk. 
Dette introduserer behovet for å lese og skrive til variabler. 
Dette støttes både i den enkle uttrykksanalysatoren og i den utvidede flertrykksanalysatoren. 
Bare forhåndsdefinerte variabler er tillatt, ikke brukerdefinerte. 
Dette er både en fordel og en ulempe. Variablene trenger ikke å deklareres og kan hentes veldig raskt av CPU-en.
Ulempen er at det er ufleksibelt. Jeg planlegger å forhåndsdefinere alle variabler som virkelig trengs for en simulering,
så det burde ikke være et stort problem.

### Om grafmodulen

Deler:
- Input-parametre på HTML-skjema
- Beregne akse
- Tegne akse
- Parse funksjon
- Tegne kurve(r) basert på trinnvis input og beregning

Bare én variabel er tillatt. Du kan velge x-aksevariabel blant de forhåndsdefinerte variablene.
Du definerer en funksjon som skal brukes for y-aksen.