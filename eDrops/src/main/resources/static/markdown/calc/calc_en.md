How to calculate.

The easiest way to get started is simply to press "Calculate" in the quadratic equation example.
Under the question mark button, you will find an overview of available mathematical symbols, functions, constants, and predefined variables.

- **Formulas:** You can enter multiple formulas. A variable is assigned a result that can be used in subsequent formulas.
- **Variables:** Use the predefined variables, or define your own before the equals sign.
- **Separators:** You must ALWAYS place `;` between expressions. Semicolons are also used to separate parameters in functions that take multiple values.
- **Line breaks and comments:** Line breaks are ignored, and comments are supported.
- **Results:** All calculated variable values are displayed below the input field.

It is recommended to use copy and paste with a plain text editor.
All input and calculations are lost once your session expires or you leave the page.
You can use the same technique to copy from the results back into the input field.
Remember that everything is either functions or simple variable assignments.
The calculator is a floating-point calculator.
However, you still have access to logical operations that return true (1) or false (0).
You can continue calculating with true and false as if they were regular numbers.
Remember that values other than 1 or 0 will not be accepted in logical operations.
You can use Norwegian `,` or English `.` as the decimal separator.
Certain rules allow implicit multiplication in specific cases.  

***Remember that there cannot be any space between the minus sign and the number for negative numbers.
If you forget this, `-` will be interpreted as subtraction.***

Tip: `select (x > 0 ; y = sin(x) ; y = cos(x));`
The `select` function is how you achieve conditional calculations.

Trigonometric functions normally calculate in radians.

### About the calculator

Parts:
- Preparse: Simple syntax checks and tokenization
- Parse: Reordering tokens using a recursive parser into an instruction list for RPN
- Calculate: Executing RPN calculations on the stack using the RPN instruction list
- Variables: Extended to handle variables in parsing and calculation
- Board: A digital board where multiple expressions are calculated in sequence
- Result: Display of variables with calculated values from the board

The parser returns a compiled list of tokens to be used for subsequent calculations.
This provides the foundation for repeatable calculations without re-parsing mathematical formulas.
With repeatable calculations, it is easy to extend with time-based simulations based on physical laws.
Results are only stored temporarily and not in any database.
