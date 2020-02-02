Specs:
1. Handle Multiple Clients simultaneously (Limit client connections gracefully)
2. Manage price subscription for instruments efficiently
    - dont dup subscriptions, maintain a cache
3. Quote Calculation Slowness - Handle in requests
4. Stress test with large requests, large requests x multiple clients
    - will need to dev harness and setup data
5. TCP Instructions - Validations
6. Client disconnects abrubtly, handle gracefully
7. Calc logic and Reference price logic is out of scope of dev

Assumptions:
1. Only 1 instance of QuoteCalculationEngine can exist for the server
2. Client can quit server
3. referencePriceSource always gets the price of instrument
4. ReferencePriceSource doesnt always return a correct price (bad price?)
5. First price from RPS.get is always a valid price.
6. All the classes implementing interfaces provided handle their errors gracefully and wont throw exceptions
7. Security ID set is limited so subscriptions won't grow drastically
8. Implementations of QCE and RPS, RPSL are abstracted
9. QCE Engine always returns a correct quote

Personal Thought Notes:
1. To reduce subscriptions and lookup, reuse cache across clients
2. Change ConcurrentHashMap to a LRU Cache for improved performance
3. Using blocking calls in threads vs JAVA NIO -> actually not super performant
4. Right now, creating a new thread for each client -> change to threadpool with
   optimum number of threads for better resource management
5. Use of interfaces decouples objects -> makes my life easier for testing
6. Why use double when there is bigdecimal? -> Inacurracies in calc
7.

Principles:
1. Test Driven Dev - Dont mock types you dont own
2.