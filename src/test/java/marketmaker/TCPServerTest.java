package marketmaker;

import harness.QuoteCalculationEngineFake;
import harness.ReferencePriceSourceFake;
import harness.TestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TCPServerTest {

    @BeforeEach
    void setUp() {
        try {
            PricingService pricingService = new PricingServiceImpl(new QuoteCalculationEngineFake(), new ReferencePriceSourceFake());
            TCPServer server = new TCPServer(pricingService);
            Thread serverThread = new Thread(server);
            serverThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    void canProcessValidRequestFromSingleClient() {
        TestClient testClient = new TestClient(3000);
        testClient.start();
        String response = testClient.executeInstruction("1223 BUY 80");
        assertEquals("8000.0", response);
        testClient.stop();
    }

    @Test
    void canProcessInvalidRequestFromSingleClient() {
        TestClient testClient = new TestClient(3000);
        testClient.start();
        String response = testClient.executeInstruction("12343");
        assertEquals("Invalid arguments supplied: {Instruction format is incorrect: {security ID} (BUY|SELL) {quantity} }", response);
        testClient.stop();
    }

    @Test
    void canProcessRequestFromMultipleClients() {
        TestClient testClient = new TestClient(3000);
        TestClient testClient1 = new TestClient(3000);
        testClient.start();
        testClient1.start();
        String response = testClient.executeInstruction("12343");
        String response1 = testClient1.executeInstruction("1212 BUY 1223");
        assertEquals("Invalid arguments supplied: {Instruction format is incorrect: {security ID} (BUY|SELL) {quantity} }", response);
        assertEquals("122300.0", response1);
        testClient.stop();
    }


}