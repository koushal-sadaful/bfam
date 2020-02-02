package com.example.marketmaker;

import com.example.marketmaker.common.IPriceValidator;
import com.example.marketmaker.common.IRequest;
import com.example.marketmaker.common.PriceValidator;

import java.util.concurrent.ConcurrentHashMap;

public class PricingService implements IPricingService {
    private QuoteCalculationEngine calculationEngine;
    private ReferencePriceSource referencePriceSource;
    private IPriceValidator priceValidator;
    private ConcurrentHashMap<Integer, Double> pricingCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, ReferencePriceSourceListener> securityPriceSubscriptionMap = new ConcurrentHashMap<>();

    public PricingService(QuoteCalculationEngine calculationEngine, ReferencePriceSource referencePriceSource) {
        this.calculationEngine = calculationEngine;
        this.referencePriceSource = referencePriceSource;
        this.priceValidator = new PriceValidator();
    }

    public double getQuotePrice(IRequest request) {
        double referencePrice = getReferencePrice(request.getSecurityId());
        return calculationEngine.calculateQuotePrice(request.getSecurityId(), referencePrice, request.isBuy(), request.getOrderQuantity());
    }

    public double getReferencePrice(int securityId) {
        if (!pricingCache.containsKey(securityId)) {
            subscribeToReferencePrice(securityId);
            getAndCacheFirstPrice(securityId);
            return pricingCache.get(securityId);
        }
        return pricingCache.get(securityId);
    }

    private void subscribeToReferencePrice(int securityId) {
        if (securityPriceSubscriptionMap.containsKey(securityId))
            return;

        securityPriceSubscriptionMap.put(securityId, new ReferencePriceSourceListener() {
            @Override
            public void referencePriceChanged(int securityId, double price) {
                if (priceValidator.isValidPrice(price)) {
                    System.out.printf("[%d] Price Update : %s", securityId, price);
                    pricingCache.put(securityId, price);
                } else {
                    System.out.printf("[%d] Invalid Price Received: %s", securityId, price);
                }
            }
        });
        referencePriceSource.subscribe(securityPriceSubscriptionMap.get(securityId));
    }

    private void getAndCacheFirstPrice(int securityId) {
        double price = referencePriceSource.get(securityId);
        pricingCache.put(securityId, price);
    }
}
