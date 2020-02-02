package harness;

import marketmaker.ReferencePriceSource;
import marketmaker.ReferencePriceSourceListener;

public class ReferencePriceSourceFake implements ReferencePriceSource {


    public void subscribe(ReferencePriceSourceListener listener) {

    }

    public double get(int securityId) {
        return 0;
    }
}
