package test.harness;

import com.example.marketmaker.QuoteCalculationEngine;

public class QuoteCalculationEngineFake implements QuoteCalculationEngine {

    int sleepTimer = 1100;

    @Override
    public double calculateQuotePrice(int securityId, double referencePrice, boolean buy, int quantity) {
        return referencePrice * quantity;
    }
}
