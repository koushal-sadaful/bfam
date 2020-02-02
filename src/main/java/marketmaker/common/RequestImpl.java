package marketmaker.common;

public class RequestImpl implements Request {
    private boolean isBuy;
    private int orderQuantity;
    private int securityId;
    private boolean isDisconnectionRequest;

    public RequestImpl(int securityId, int orderQuantity, boolean isBuy) {
        this.isBuy = isBuy;
        this.securityId = securityId;
        this.orderQuantity = orderQuantity;
        this.isDisconnectionRequest = false;
    }

    public RequestImpl(boolean isDisconnectionRequest) {
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
