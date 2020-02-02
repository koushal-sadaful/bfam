package com.example.marketmaker;

import com.example.marketmaker.common.IRequest;

public interface IPricingService {
    double getQuotePrice(IRequest request);

    double getReferencePrice(int securityId);
}
