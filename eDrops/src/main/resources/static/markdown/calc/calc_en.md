To produce a graph from a function:

- First run: default axis bounds
- Next: dynamic min/max limits

Simple assumptions for ticks and labels.

### Project goals

- Module for drawing axes and graphs with SVG (completed)
- Module for mathematical functions / RPN-based (completed)
- Mathematical function input by the user with min and max values
- Import from spreadsheet or API, for example for energy calculations
- Consider storing data in the database for plotting as well
- Time-based simulations (requires WASM)

Calculations run on the backend.
Client-side WASM for live animation later.
I think that the axis program can be used as a background for WASM animation.

Since the project is ambitious, it will take time to get everything in place in spare time.
Since this is on the internet, error handling and security will be important.

### About the expression parser

Parts:
- Preparse: Simple syntax checks and tokenizer
- Parse: Reordering tokens using a recursive parser to an instruction list for RPN
- Calculate: Execute RPN calculations on the stack using the RPN instruction list

The parser returns a compiled list of tokens to be used for successive calculations.

To be used for simulations, I have extended the calculator to be able to do sequential parsing and calculation based on multiple expressions. This introduces the need for reading and writing to variables. This is supported both in the simple expression parser and in the extended multi-expression parser. Only predefined variables are allowed, not user-defined ones. This is both an advantage and a disadvantage. The variables do not need to be declared and can be fetched very quickly by the CPU. The disadvantage is that it is inflexible. I plan to predefine all variables really needed for a simulation, so it should not be a big problem.

### About the graph module

Parts:
- Input parameters on HTML form
- Calculate axis
- Draw axis
- Parse function
- Draw curve(s) based on stepwise input and calculation

Only one variable is allowed. You can select the x-axis variable among the predefined variables.
You define a function to be used for the y-axis.