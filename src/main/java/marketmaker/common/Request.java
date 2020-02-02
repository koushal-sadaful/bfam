package marketmaker.common;

public interface Request {
    boolean isDisconnectionRequest();

    boolean isBuy();

    int getOrderQuantity();

    int getSecurityId();
}
