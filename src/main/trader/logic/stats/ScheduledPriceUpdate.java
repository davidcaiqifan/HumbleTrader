package logic.stats;

import com.binance.api.client.domain.event.AggTradeEvent;
import logic.listeners.OrderBookEventListener;
import logic.listeners.ScheduledListener;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

public class ScheduledPriceUpdate implements ScheduledListener, OrderBookEventListener {
    private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;

    private double price;
    @Override
    public void handleScheduledEvent() {
        System.out.println(depthCache.get("ASKS").lastEntry().getKey() );
    }

    @Override
    public void handleOrderBookEvent(Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache) {
        this.depthCache = depthCache;
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

}
