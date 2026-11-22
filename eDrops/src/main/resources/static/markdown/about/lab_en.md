## My digial cyber laboratory

*(AI translated from Norwegian)*

This is an all-digital lab and junk room. My father had a physical lab like this at NTH.
This is my attempt at doing the same without cluttering up my entire home office.
Because of that, you will find a lot of odd and varied stuff here.
Not everything is finished, and much of it is still under development.

A note on the word "cyber": 
Before the IT security industry hijacked the term, *cyber* actually came from *cybernetics*—the 1940s science of control, 
feedback loops, and systems (from the Greek *kybernetes*, meaning steersman). 
I use it in its original meaning, I perfectly fits with the simulation engine I plan to build. 


### The goal of the lab

The original goal was just pure experimentation.
Collatz was something I wrote as a shared coding challenge at a previous job, where everyone tried to write the best code.
Fraction finds the best rational approximation, reimplemented from my university days. The algorithm is simply fascinating.
Graph is also reimplemented from Turbo Pascal, completely redesigned for the web.
The foundation for Calc was laid 12 years ago with a Java calculator that had a few too many bugs,
but the idea comes from old HP RPN (Reverse Polish Notation) calculators.
Back in high school, we competed to write the fastest prime number algorithm. My friend had a Texas Instruments calculator, and I used an HP.

I think I won because of a slightly smarter algorithm; the HP calculator wasn't the fastest.
For obvious reasons, I am not putting the prime number stuff online so I don't overload my VPS.
The core is still pure RPN, with an expression parser layered on top.
RPN is a bit awkward for a server-based solution, so that part is hidden until I solve it with WASM (WebAssembly).

The plan is to build simulations based on physical laws (Newton's 2nd law, etc.).
This is a reimplementation of a Turbo Pascal solution made for teaching at NTH (around 1989).

### An extended calculator with plotting and simulation capabilities

This is the actual goal.

Done:
- Module for drawing axes and graphs using SVG
- Math parser with preparsing and a recursive parsing algorithm
- RPN-based calculator (no GUI)
- Simple function evaluator and curve plotting
- Calculator with advanced math functions and variables

Not done, will it ever be finished?
- Pure plotting from raw data (spreadsheets or API), for instance for energy calculations
- Multiple curves in the same graph
- Better integration with the blog section
- Predefined templates for the function evaluator and simulations
- Storing templates in the database instead
- Backend simulations, not in real time
- Real-time, time-based simulations in the frontend (requires WASM)
- WASM calculator
- Displaying energy data and simple simulations

Since the project is ambitious, getting everything in place will take time.
Because this is on the internet, error handling, security, and performance are important.
User accounts, especially with login and data storage, are a challenge.
For now, everything is open and anonymous with nothing stored in a database, so GDPR is unnecessary.
You are encouraged to use copy & paste and Notepad or something similar.

### A reality check

If the targets for the green transition are not anchored in math and physics, economics, and ordinary people and businesses,
it is bound to go down the drain. The idea is to use tools from the lab to illustrate this.
When full-time researchers with advanced simulation software manage to conclude in 2021
that electricity prices in Norway would not increase by more than 0.02 NOK because of the cross-border power cables,
something is seriously wrong. Garbage in, garbage out, regardless of how advanced the calculations in the middle are.
The measures in the otherwise necessary green transition are far too often based on assumptions that do not hold up.
Even relatively simple calculations combined with public statistics can show this.
Unfortunately, this lack of a reality check keeps a whole crowd of politicians, bureaucrats, consultants, and parts of the power industry in business.  That makes it hard to turn things around, both in Norway and in the EU in general.

Unfortunately, it is the fault of these cross-border power cables that this project got started in the first place.