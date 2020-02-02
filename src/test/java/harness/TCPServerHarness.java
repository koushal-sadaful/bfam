package harness;

import marketmaker.PricingService;
import marketmaker.PricingServiceImpl;
import marketmaker.TCPServer;

public class TCPServerHarness {

    public static void main(String[] args) {
        PricingService pricingService = new PricingServiceImpl(new QuoteCalculationEngineFake(), new ReferencePriceSourceFake());
        TCPServer server = new TCPServer(pricingService);
        server.start();
    }

}
