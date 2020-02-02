package com.example.marketmaker.common;

public class Request implements IRequest {
    private boolean isBuy;
    private int orderQuantity;
    private int securityId;
    private boolean isDisconnectionRequest;

    public Request(int id, int quantity, boolean buy) {
        isBuy = buy;
        securityId = id;
        orderQuantity = quantity;
        isDisconnectionRequest = false;
    }

    public Request(boolean isDisconnectionRequest) {
        this.isDisconnectionRequest = isDisconnectionRequest;
    }

    public boolean isDisconnectionRequest() {
        return isDisconnectionRequest;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public int getSecurityId() {
        return securityId;
    }
}
