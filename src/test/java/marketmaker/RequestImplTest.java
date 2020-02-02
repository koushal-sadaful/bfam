package marketmaker;

import marketmaker.common.RequestImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestImplTest {

    @Test
    void shouldInstantiateARequestObjectCorrectlyGivenRequestParameters() {
        RequestImpl r = new RequestImpl(123, 1000, true);
        assertEquals(123, r.getSecurityId());
        assertEquals(1000, r.getOrderQuantity());
        assertTrue(r.isBuy());
        assertFalse(r.isDisconnectionRequest());
    }

    @Test
    void shouldInstantiateARequestObjectWith() {
        RequestImpl r = new RequestImpl(true);
        assertTrue(r.isDisconnectionRequest());
    }
}