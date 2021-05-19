package logic.listeners;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public class ScheduledPriceManager implements OrderBookEventListener {

    private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;
    private Map<Integer, List<PriceEventListener>> priceEventListeners = new HashMap<>();
    private double price;

    public ScheduledPriceManager() {
    }

    @Override
    public void handleOrderBookEvent(Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache) {
        this.depthCache = depthCache;
        //System.out.println(this.depthCache.get("ASKS").lastEntry().getKey());
        this.price = calculateWeightedPrice();
        System.out.println(calculateWeightedPrice());
    }

    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache() {
        return depthCache;
    }

    public void publishPriceEvent(int interval) {
        // Notify everybody that may be interested.
        for (PriceEventListener pl : this.priceEventListeners.get(interval))
            pl.handlePriceEvent(this.price);
    }

    public double calculateWeightedPrice() {
        NavigableMap<BigDecimal, BigDecimal> asks = this.depthCache.get("ASKS");
        NavigableMap<BigDecimal, BigDecimal> bids = this.depthCache.get("BIDS");
        double totalAskPrice = 0;
        double totalAskQuantity = 0;
        double totalBidPrice = 0;
        double totalBidQuantity = 0;
        double weightedAskPrice = 0;
        double weightedBidPrice = 0;
        for (Map.Entry<BigDecimal, BigDecimal> entry: asks.entrySet()) {
            double quantity = entry.getValue().doubleValue();
            totalAskPrice += entry.getKey().doubleValue() * quantity;
            totalAskQuantity += quantity;
        }
        for (Map.Entry<BigDecimal, BigDecimal> entry: bids.entrySet()) {
            double quantity = entry.getValue().doubleValue();
            totalBidPrice += entry.getKey().doubleValue() * quantity;
            totalBidQuantity += quantity;
        }
        weightedAskPrice = totalAskPrice/totalAskQuantity;
        weightedBidPrice = totalBidPrice/totalBidQuantity;
        double weightedMidPrice = (weightedAskPrice + weightedBidPrice)/2;
        return weightedMidPrice;
    }

}
