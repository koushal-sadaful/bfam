package test;

import com.example.marketmaker.common.Request;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void shouldInstantiateARequestObjectCorrectlyGivenRequestParameters() {
        Request r = new Request(123, 1000, true);
        assertEquals(123, r.getSecurityId());
        assertEquals(1000, r.getOrderQuantity());
        assertTrue(r.isBuy());
        assertFalse(r.isDisconnectionRequest());
    }

    @Test
    void shouldInstantiateARequestObjectWith() {
        Request r = new Request(true);
        assertTrue(r.isDisconnectionRequest());
    }
}