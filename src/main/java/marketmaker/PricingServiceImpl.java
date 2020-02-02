package marketmaker;

import marketmaker.common.PriceValidator;
import marketmaker.common.Request;
import marketmaker.common.PriceValidatorImpl;

import java.util.concurrent.ConcurrentHashMap;

public class PricingServiceImpl implements PricingService {
    private QuoteCalculationEngine calculationEngine;
    private ReferencePriceSource referencePriceSource;
    private PriceValidator priceValidator;
    private ConcurrentHashMap<Integer, Double> pricingCache = new ConcurrentHashMap<Integer, Double>();
    private ConcurrentHashMap<Integer, ReferencePriceSourceListener> securityPriceSubscriptionMap = new ConcurrentHashMap<Integer, ReferencePriceSourceListener>();

    public PricingServiceImpl(QuoteCalculationEngine calculationEngine, ReferencePriceSource referencePriceSource) {
        this.calculationEngine = calculationEngine;
        this.referencePriceSource = referencePriceSource;
        this.priceValidator = new PriceValidatorImpl();
    }

    public double getQuotePrice(Request request) {
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
