import marketmaker.common.PriceValidatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static java.lang.Double.parseDouble;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceValidatorImplTest {

    @ParameterizedTest
    @ValueSource(doubles = {
            -11,
            -1.2,
            0.0
    })
    void isValidPriceShouldReturnFalseForInvalidPrices(double price) {
        assertFalse(new PriceValidatorImpl().isValidPrice(price));
    }

    @ParameterizedTest
    @ValueSource(doubles = {
            0.1,
            100.201,
            12210.1
    })
    void isValidPriceShouldReturnTrueForValidPrices(double price) {
        assertTrue(new PriceValidatorImpl().isValidPrice(price));
    }

}