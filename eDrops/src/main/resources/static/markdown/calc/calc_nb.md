Slik gjør du for å regne.

Det enkleste er bare å trykke «Beregne» i eksemplet for løsning av en annengradslikning. 
I spørsmålstegn-knappen finner du en oversikt over tilgjengelige matematiske symboler, funksjoner, konstanter og predefinerte variabler.

- **Formler:** Du kan ha flere. En variabel tilordnes ett resultat som kan brukes i etterfølgende formler.  
- **Variabler:** Bruk de predefinerte variablene, eller definer dine egne før likhetstegnet.
- **Skilletegn:** Du må ALLTID ha `;` mellom uttrykk. Semikolon brukes også mellom parametre i funksjoner som tar flere verdier.
- **Linjeskift og kommentarer:** Kalkulatoren bryr seg ikke om linjeskift, og du kan legge inn kommentarer i koden.
- **Resultater:** Alle beregnede variabelverdier vises under inntastingsfeltet.

Det anbefales å bruke klipp og lim mot en enkel teksteditor.
Når din sesjon er slutt og du navigerer bort så vil du miste inndata og beregninger.
Du kan bruke samme teknikk for å kopiere fra resultater og inn i tekstfeltet.
Husk at alt er funksjoner eller enkle tilordninger av variable.
Kalkulatoren er en flyttalls kalkulator.
Du har likevel tilgang til logiske operasjoner som vil returnere true (1) eller false (0)
Du kan regne videre med true og false som det var vanlige verdier.
Husk at verdier forskjellig fra 1 eller 0, ikke vil bli godtatt i logiske operasjoner.
Du kan bruke norsk , eller engelsk . som desimalskilletegn.
Det er regler som tillater implisitt multiplikasjon i visse tilfeller.  

***Husk at det ikke kan være skilletegn mellom minus og tallet for negative tall.
Hvis du glemmer dette blir - tolket som minus.***

Tips: select (x > 0 ; y = sin(x), y = cos(x));
Select funksjoen er måten du kan få betinget beregning på.

Trigonometriske funksjoner regnes med radianer.  

### Om kalkulatoren

Deler:
- Preparse: Enkle syntaks-sjekker og tokenisering
- Parse: Omorganisering av tokens ved hjelp av en rekursiv parser til en instruksjonsliste for RPN
- Beregne: Utføre RPN-beregninger på stakken ved hjelp av RPN-instruksjonslisten
- Variable: Det er utvidet til å takle variable i parser og beregning
- Tavle: En digital tavle der fleruttrykk beregnes i sekvens
- Resultat: Visning av variabler med beregnede verdier fra tavla

Parseren returnerer en kompilert liste med tokens som skal brukes til påfølgende beregninger.
Dette gir grunnlaget for repeterbare beregninger uten ny parsing av matematiske formler. 
Med reperbare beregninger er det lett å utvide med tidsbaserte simuleringer basert på fysiske lover. 
Resultat lagres bare midlertidig og ikke i noen database.
