package logic.listeners;

import logic.ScheduleManager;
import logic.schedulers.ScheduleEvent;
import model.OrderBookCache;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public class ScheduledPriceManager implements OrderBookEventListener {

    private OrderBookCache orderBookCache;
    private Map<Integer, List<PriceEventListener>> priceEventListeners = new HashMap<>();
    private double price;
    private ScheduleManager scheduleManager;

    public ScheduledPriceManager(ScheduleManager scheduleManager, int interval) {
        this.scheduleManager = scheduleManager;
        try {
            scheduleManager.periodicCallback(interval, "scheduledPrice");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void handleOrderBookEvent(OrderBookCache orderBookCache) {
        this.orderBookCache = orderBookCache;
        this.price = calculateWeightedPrice();
        System.out.println(calculateWeightedPrice());
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
    }

    public double calculateWeightedPrice() {
        NavigableMap<BigDecimal, BigDecimal> asks = this.orderBookCache.getAsks();
        NavigableMap<BigDecimal, BigDecimal> bids = this.orderBookCache.getBids();
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
