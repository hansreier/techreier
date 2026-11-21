Slik gjør du for å regne.

Det enkleste er er bare å trykke "Beregne" i eksemplet for løsning av en annengradslikning.
Du får også litt hjelp med liste som viser tilgjengelige matematiske symboler, funksjoner, konstanter og predefinerte variable.
Du kan bruke de predefinerte variablene eller definere dine egne før likhetstegnet.
Variable brukes til mellomregning, så rekkefølgen på uttrykk er helt vesentlig. 
Muigheten for kommentarer er der også.
Det er viktig å huske på at du ALLTID må ha ; mellom uttrykk. 
Semikolon brukes også som skilletegn i funksjoner som takler flere parametre. 
Den bryr seg ikke om linjeskift.
Resultatene vises under med alle beregnede variabelverdier.

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
Husk at det ikke kan være skilletegn mellom minus og tallet for negative tall.
Hvis du glemmer dette blir - tolket som minus.

Tips: select (x > 0 ; y = sin(x), y = cos(x));
Select funksjoen er måtem du kan få betinget beregning på.

Trigonometriske funksjoner regner vanligvis med radianer.

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
Det anbefales foreløpig å bruke klikk og lim inn i en enkel teksteditor.