Temporary SVG rendering with a sine wave.

* First run: default axis bounds
* Next: dynamic min/max limits

Simple assumptions for ticks and labels.

### Project goals

* Module for drawing axes and graphs with SVG (completed)
* Module for mathematical functions / RPN-based (have a prototype from earlier)
* Mathematical function input by the user with min and max values
* Import from spreadsheet or API, for example for energy calculations 
* Consider storing data in the database for plotting as well
* Time-based simulations (requires WASM)  

Calculations run on backend. 
Client-side WASM for live animation later.
I think that the axis program can be used as a background for WASM animation.  

Since the project is ambitious, it will take time to get everything in place in spare time.
Since this is on the internet, error handling and security will be important.
