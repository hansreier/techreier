## Å beregne beste brøk for et desimaltall

1. Velg et desimaltall.
2. Sett inn maksimum verdi på nevner og maksumum avvik
3. Funksjonen returerer brøken med taller og nevner som er nærmest til desimaltallet.

Algoritme for kjedebrøker brukes til beregningene.  

Algoritmen kjører i sløyfe som avsluttes når:
- Differansen mellom verdien på brøken og desimaltallet er mindre enn max avvik. 
- Max verdi på nevner er overskredet (gir feilmelding)
- Beregningsfeil. Kan oppstå hvis avvik settes til 0.0 (gir feilmelding)
- Max antall tilatte iterasjoner er overskredet (usannsynlig, gir feilmelding)

