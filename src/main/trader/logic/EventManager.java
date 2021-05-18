package logic;

import com.binance.api.client.domain.event.AggTradeEvent;
import logic.listeners.OrderBookEventListener;
import logic.listeners.ScheduledListener;
import logic.listeners.TradeEventListener;
import logic.stats.ScheduledPriceUpdate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util .Map;
import java.util.NavigableMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventManager {
    private MarketDataManager marketDataManager;
    private ScheduleManager scheduleManager;
    private List<OrderBookEventListener> orderBookEventListeners = new ArrayList<>();
    private List<TradeEventListener> tradeEventListeners = new ArrayList<>();

    public EventManager(MarketDataManager marketDataManager) {
        this.marketDataManager = marketDataManager;
        this.scheduleManager = new ScheduleManager();
    }


    /**
     * Add trade event listeners.
     */
    public void addTradeEventListener(TradeEventListener toAdd) {
        tradeEventListeners.add(toAdd);
    }

    /**
     * Add order book event listeners.
     */
    public void addOrderBookEventListener(OrderBookEventListener toAdd) {
        orderBookEventListeners.add(toAdd);
    }

    /**
     * Add scheduled order book event listeners.
     */
    public void addScheduledOrderBookEventListener(OrderBookEventListener toAdd, int interval) {
        orderBookEventListeners.add(toAdd);
        this.scheduleManager.periodicCallback(interval, (ScheduledListener)toAdd);
    }

    public void startPublishingOrderBookEvents() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            while(true) {
                Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;
                depthCache = this.marketDataManager.getOrderBookManager().getDepthCache();
                publishOrderBookEvent(depthCache);
                //System.out.println("BEST ASK: " + toDepthCacheEntryString(depthCache.get("ASKS").lastEntry()));
                //System.out.println("BEST BID: " + toDepthCacheEntryString(depthCache.get("BIDS").firstEntry()));
            }
        });
    }

    public void publishTradeEvent(ScheduleManager scheduleManager, AggTradeEvent tradeEvent) {
        // Notify everybody that may be interested.
//        for (TradeEventListener tl : tradeEventlisteners)
//            tl.handleTradeEvent();
        //scheduleManager.updateTradeEvent(tradeEvent);
    }

    public void publishOrderBookEvent(Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache) {
        // Notify everybody that may be interested.
        for (OrderBookEventListener ol : this.orderBookEventListeners)
            ol.handleOrderBookEvent(depthCache);
        //scheduleManager.updateTradeEvent(tradeEvent);
    }

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }


    /**
     * Pretty prints an order book entry in the format "price / quantity".
     */
    public static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
        return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
    }

    public static void main(String[] args) {
        EventManager eventManager = new EventManager(new MarketDataManager("BTCUSDT"));
        ScheduledPriceUpdate generator = new ScheduledPriceUpdate();
        //idk why but must be done in this order publish -> add to observer list
        eventManager.startPublishingOrderBookEvents();
        eventManager.addScheduledOrderBookEventListener(generator, 800);
//        eventManager.startPublishingOrderBookEvents();
//        eventManager.addOrderBookEventListener(generator);
//        eventManager.scheduleManager.periodicCallback(800, generator);
    }

}
