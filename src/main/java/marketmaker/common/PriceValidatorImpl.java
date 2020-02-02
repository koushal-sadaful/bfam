package marketmaker.common;

public class PriceValidatorImpl implements PriceValidator {

    public boolean isValidPrice(double price) {
        if (price <= 0)
            return false;
        else
            return true;
    }
}
