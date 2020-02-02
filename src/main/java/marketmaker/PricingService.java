package marketmaker;
import marketmaker.common.Request;

public interface PricingService {
    double getQuotePrice(Request request);

    double getReferencePrice(int securityId);
}
