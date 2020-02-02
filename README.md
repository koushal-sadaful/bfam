# Interview Exercise

Implement a server that responds to quote requests.

## Requirements

The server should accept TCP connections from one or more clients and respond to their requests.  Requests are sent on a 
single line, in the format of:

    {security ID} (BUY|SELL) {quantity}

Where `security ID` and `quantity` are integers.

For example:

    123 BUY 100

Is a request to buy 100 shares of security 123.

This should be responded to with a single line with a single numeric value representing the quoted price.

To calculate the quote price, two interfaces have been provided.

* `QuoteCalculationEngine` - to calculate the quote price based on a security, buy/sell indicator, requested
quantity and reference price.
* `ReferencePriceSource` - source of reference prices for the `QuoteCalculationEngine`.

The server should be capable of handling a large number of quote requests and be able to respond in a timely manner.

## Assumptions

This specification is intentionally vague, and numerous assumptions need to be made.  These assumptions should be 
documented and justified.

## Testing

Evidence should be provided that the server works correctly.
