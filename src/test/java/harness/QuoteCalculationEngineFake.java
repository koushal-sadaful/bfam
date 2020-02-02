package harness;

import marketmaker.QuoteCalculationEngine;

public class QuoteCalculationEngineFake implements QuoteCalculationEngine {

    @Override
    public double calculateQuotePrice(int securityId, double referencePrice, boolean buy, int quantity) {
        return referencePrice * quantity;
    }
}
