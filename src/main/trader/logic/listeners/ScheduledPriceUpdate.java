package logic.listeners;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

public class ScheduledPriceUpdate implements OrderBookEventListener {

    private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;

    public ScheduledPriceUpdate() {
    }

    @Override
    public void handleOrderBookEvent(Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache) {
        this.depthCache = depthCache;
        //System.out.println(this.depthCache.get("ASKS").lastEntry().getKey());
        System.out.println(calculateWeightedPrice());
    }

    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache() {
        return depthCache;
    }

//    public static void main(String[] args) {
//        EventManager eventManager = new EventManager(new MarketDataManager());
//        ScheduledPriceUpdate priceGenerator = new ScheduledPriceUpdate();
//        eventManager.getScheduleManager().periodicCallback(100, priceGenerator);
//        while(true) {
//            double price = priceGenerator.getPrice();
//            System.out.println(price);
//        }
//    }

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
