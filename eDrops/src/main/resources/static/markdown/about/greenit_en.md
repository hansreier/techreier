## Green software development, what is it?

The green transition is seen as the grey transition by critics.
In practice, it consumes natural resources at a rapid pace and destroys nature that absorbs CO2. 
How well-founded are the solutions to save the climate with wind turbines when they have this side effect? 
The green transition involves a great deal of cynicism, 
because control is transferred to large organizations that profit from green thinking.

Social media in Norway is overflowing with frustration over the green transition.
What can you and I do, then? The economy has worsened for many people and small businesses, limiting their options.
If we, as developers and IT architects, could help create more energy-efficient solutions, that would be great.
It requires a lot of collaboration and innovation across disciplines beyond just IT.

Digitalization is not necessarily sustainable in itself. But it can be done right:
- Digital disruption for energy saving work practices.
- Enable work and services anywhere.
- Automate and simplify manual processes.
- Eliminate routine tasks to focus on more value-creating work.
- Reduce energy consumption and CO2 emissions through smart solutions for process control (IoT, data collection, analysis)
- The environmental- and climate- impact of digitalization need to be included in the total equation.

One pitfall that is easy to fall into is changing each individual manual subprocess to something supported digitally, 
without considering the whole picture. This is especially a significant challenge for public administration, which is highly regulate

There is no clear definition of what "green" system development actually is.
However, I encourage everyone in the industry to reflect on what it actually involves.
The bullet points abouve provide some guidance. 
An important point is that the reduction in energy consumption through ICT must be measurable. 
Without this, it becomes challenging to implement measures effectively.


## Advice for developers and IT architects

What can we do as developers?

There is one keyword that is essential:

*USE LESS ENERGY, APPLIES TO ICT SOLUTIONS AS WELL*

This way, we avoid environmental destruction. 
Reduced energy consumption translates into lower CO2 emissions through a conversion factor, 
largely depending on how dirty the electricity production is. 
Even if the power from your data center comes 100% from hydropower, 
it displaces energy used for other purposes, and thus indirectly contributes to CO2 emissions. 
Therefore, I prefer not to discuss reduced CO2 emissions as Microsoft does in its calculator, but rather reduced energy consumption

### Energy usage involved in digital processes:

A very simplified general value chain:

Manual value chain:

☺ -> Manual planning -> Tool -> ☺ ->  
☺ -> Manual planning with tool -> Result -> ☺   

Digital value chain:

☺ -> Digital development -> Software -> ☺ ->  
☺ -> Digital operations with software -> Result -> ☺  

In practice, these value chains are much longer and involve many more processes, 
typically both automated and manual in a mix, but we are keeping it simple here.

Det er en grunn til at jeg har delt verdikjeden opp i to, der den siste er en produksjonskjede, 
mens den første  innebærer utviklingsaktiviteter for verktøykassa (Digitale eller manuelle).
Slik er det jo i virkeligheten også, det er gjerne det som karakteriserer menneskelig aktivitet.
Oppgavene kan også være delvis digitalt utført, går ikke i slike detaljer i dette eksemplet.
Hvis ikke involveringen av en digital operasjon gir noen gevinst på noen måte i form av KPIer,
så er det selvsagt ingen vits. KPIer kan også relateres til FNs 17 bærekraftsmål.

Resultat inkluderer blant annet kost & nytte, energiforbruk, klima og miljøpåvirkning, nødvendig etterarbeid. 
Dette er også pålagt for større prosjekter / organisasjoner, gjerne via ESG prinsipper (Environmental, Social, Governance).

Først så koder vi noe, tester og retter, så blir systemet satt i produksjon.
Alternativt blir AI modellen først trent opp, så brukes prompt engineering til å få et resultat. 
Begge disse fasene forbruker energi. 
Det er vel kjent at trening av AI bruker mye energi, litt mindre vanlig å tenke på energiforbruk for en tradisjonell
systemutviklingsprosess. Uansett blir systemet satt i drift på tjenere, enten internt eller i skya. Det er
som regel testmiljøer som likner på produksjonsmiljø, som forbruker energi det og. Prosessering forbruker energi,
Datalagring forbruker energi. 
Dette er veldig avhengig av type datalagring, RAM, fillagring, database, semi permanent, permanent, arkiv, .. ).

*Total energy consumption is the sum of energy used for every individual element in the value chain*

Regardless of energy consumption being caused by digital or manual tools. 

Det må også regnes med at produksjonsleddet blir utført mange ganger.
Det er energiforbruk knyttet til det fysiske miljøet i tillegg (kontoret, fabrikken, datasentret, internettet).  

Dette kan for eksempel bety at et høyt energiforbruk pga AI trening og svar fra en AI tjeneste, likevel kan spare
energi totalt fordi man da klarer å optimalisere en industriprosess eller et kraftnett. Dette høres jo selvsagt ut.
Men det er et poeng at verktøy for å faktisk regne på dette ikke er så veldig tilgjengelige.  

### To measure energy usage

For å finne energiforbruket, så må det være målbart. Det mest nøyaktige for datasystemer er å måle forbruket direkte 
med måleutstyr på tjenere. I skya så er ikke dette så enkelt som det høres ut, fordi virtuell maskiner og containere kjører
en eller flere tjenere. Da kan jo flere helt uavhengig systemer være installert på den samme tjeneren.
Vi er uansett avhengig av skyleverandørens verktøykasse her.  

The other method is to estimate the energy usage based on CPU measurements and memory usage. In Java there is APIs that
can do this. I have tried locally with Spring Boot with embedded Tomcat, by calling the API and calculate the average.
The measurement of CPU is very unstable, so it is not recommended. Refer to below code sample.

Energy usage [kWh] = (Σ(%c * Ec) + ΣEm) * PUE

%c = % CPU used (can for example be CPU, GPU or TPU)  
Ec = kWh consumed for all CPU  
Em =  kWh used for memory

PUE = Power Usage Effectiveness = Total energy used in data center / Energy used by ICT-equipment

PUE = 1, all energi går til IT-utstyr, neppe oppnåelig.  
PUE > 1.0. Jo høyere tall, dess mindre effektivt.  

Et alternativet er sky baserte verktøy fra skyleverandører som  Azure, Google og Amazon for det samme. 
Når Azure bare tilbyr et resultat som viser C02 utslipp, så har de bommet etter min mening. 
Det er ikke denne omregningen som er det mest interessante. Men det er ikke å komme  unna at energi-
målinger er et komplekst fagområde uansett.  

En annen indirekte svært unøyaktig metode er rett og slett å se på kostnadene for å utvikle, teste og drifte datasystemet.
Vi kan være ganske sikre på at skyleverandøren ikke vil tilby oss en løsning med et kostnadsnivå som ikke dekker
egne energikostnader.

## Conclusion

What you as a developer can do is to review [kodetipsene](greencode_en.md) and add your own smart practices.

Green system development is impossible without a holistic view of entire value chains, including the ICT part.
The larger the project, the more relevant is it to actually measure or estimate the total energy consumption of a value chain.  

What you as a developer can do is to review [kodetipsene](greencode_en.md) and add your own smart practices.



En utfordring er at metodene for å faktisk måle resultater i form av redusert energiforbruk er for lite
utviklet og kjent blant utviklere, arkitekter og ledere.
IKT-bransjen bør tenke på hvordan egne smarte løsninger utnyttes på best mulig måte i egne prosesser. 
Det er ingen fordel for eksempel for energiforbruk og C02 utslipp ved å tvinge utviklere tilbake til hovedkontoret.





