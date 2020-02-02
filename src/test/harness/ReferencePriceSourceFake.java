package test.harness;

import com.example.marketmaker.ReferencePriceSource;
import com.example.marketmaker.ReferencePriceSourceListener;

public class ReferencePriceSourceFake implements ReferencePriceSource {

    @Override
    public void subscribe(ReferencePriceSourceListener listener) {

    }

    @Override
    public double get(int securityId) {
        return 0;
    }
}
