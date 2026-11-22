Slik gjør du for å regne.

Det enkleste er bare å trykke «Beregne» i eksemplet for løsning av en annengradslikning. 
I spørsmålstegn-knappen finner du en oversikt over tilgjengelige matematiske symboler, funksjoner, konstanter og predefinerte variabler.

- **Formler:** Du kan ha flere. En variabel tilordnes ett resultat som kan brukes i etterfølgende formler.  
- **Variabler:** Bruk de predefinerte variablene, eller definer dine egne før likhetstegnet.
- **Skilletegn:** Du må ALLTID ha `;` mellom uttrykk. Semikolon brukes også mellom parametre i funksjoner som tar flere verdier.
- **Linjeskift og kommentarer:** Kalkulatoren bryr seg ikke om linjeskift, og du kan legge inn kommentarer i koden.
- **Resultater:** Alle beregnede variabelverdier vises under inntastingsfeltet.

Det anbefales å bruke kopier og lim inn med en enkel teksteditor.
All inndata og alle beregninger går tapt når økten utløper eller du forlater siden.
Du kan bruke samme teknikk for å kopiere fra resultatene tilbake til inndatafeltet.
Husk at alt enten er funksjoner eller enkle variabeltilordninger.
Kalkulatoren er en flyttallskalkulator.

Du har likevel tilgang til logiske operasjoner som returnerer true (1) eller false (0).
Du kan regne videre med true og false som om de var vanlige tall.
De enkle antagelsene er at >=0.5 er true, og <0.5 eller NaN er false.
Når verdier lagres i en boolsk variabel, normaliseres de til 0 og 1.
Foreløpig brukes dette kun for den forhåndsdefinerte radians-variabelen, som du kan sette til true eller false.  

***Husk at det ikke kan være skilletegn mellom minus og tallet for negative tall.
Hvis du glemmer dette blir - tolket som minus.***

Tips: `select (x > 0 ; y = sin(x), y = cos(x));`
Select funksjonen er måten du kan få betinget beregning på.

Trigonometriske funksjoner regnes med radianer.  

**Husk å avslutte hvert uttrykk med semikolon (;).**

Uten semikolon tolkes separate uttrykk som ett samlet uttrykk.

```
radians = false;
sin(90)
```

Uten semikolonet tolkes dette som `radians = false * sin(90)`,
noe som tilordner 0 (false) til radians i stedet for å beregne sin(90) i grader.  

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
Med reperbare beregninger er det lett å utvide med grafikk; inkludert tidsbaserte simuleringer basert på fysiske lover. 
Resultat lagres bare midlertidig og ikke i noen database.
