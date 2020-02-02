package com.example.marketmaker.common;

public class PriceValidator implements IPriceValidator {

    public boolean isValidPrice(double price) {
        if (price <= 0)
            return false;
        else
            return true;
    }
}
