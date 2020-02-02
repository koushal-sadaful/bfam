package test;

import com.example.marketmaker.common.PriceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PriceValidatorTest {

    @ParameterizedTest
    @ValueSource(doubles = {
            -11,
            -1.2,
            0.0
    })
    void isValidPriceShouldReturnFalseForInvalidPrices(double price) {
        assertFalse(new PriceValidator().isValidPrice(price));
    }
}