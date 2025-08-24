## Calculating best fraction from a decimal number

1. Select a decimal number.
2. Add maximum denominator and maximum deviation
3. The function returns the fraction closest to the decimal number 

The continued fraction algorithm is used to implement this.

The algorithm runs in a loop that terminates when:

- The difference between the value of the fraction and the decimal number is less than the maximum deviation.
- The maximum denominator value is exceeded (raises an error).
- A calculation error occurs. This may happen if the deviation is set to 0.0 (raises an error).
- The maximum number of allowed iterations is exceeded (unlikely, raises an error).

