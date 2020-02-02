package marketmaker;

/**
 * Callback interface for {@link ReferencePriceSource}
 */
public interface ReferencePriceSourceListener {

    /**
     * Called when a price has changed.
     *
     * @param securityId security identifierca
     * @param price      reference price
     */
    void referencePriceChanged(int securityId, double price);
}
