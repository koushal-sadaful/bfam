package harness;

import marketmaker.ReferencePriceSource;
import marketmaker.ReferencePriceSourceListener;

public class ReferencePriceSourceFake implements ReferencePriceSource {


    public void subscribe(ReferencePriceSourceListener listener) {
        return;
    }

    public double get(int securityId) {
        return 100;
    }
}
