package com.example.marketmaker.common;

public interface IRequest {
    boolean isDisconnectionRequest();

    boolean isBuy();

    int getOrderQuantity();

    int getSecurityId();
}
