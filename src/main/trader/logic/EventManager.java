package logic;

import logic.listeners.OrderBookEventListener;
import logic.listeners.TradeEventListener;
import logic.dataProcessors.MarketDataManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.quartz.JobBuilder.newJob;

public class EventManager {
    private MarketDataManager marketDataManager;
    private ScheduleManager scheduleManager;
    private Map<Integer, List<OrderBookEventListener>> orderBookEventListeners = new HashMap<>();
    private List<TradeEventListener> tradeEventListeners = new ArrayList<>();
    public Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;

    public EventManager(MarketDataManager marketDataManager) {
        this.marketDataManager = marketDataManager;
        //this.scheduleManager = new ScheduleManager();
        startPublishingOrderBookEvents();
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
    public void addOrderBookEventListener(OrderBookEventListener toAdd, int interval) {
        if(!orderBookEventListeners.containsKey(interval)) {
            orderBookEventListeners.put(interval, new ArrayList<>());
        }
        orderBookEventListeners.get(interval).add(toAdd);
    }

    /**
     * Add scheduled order book event listeners.
     */
//    public void addScheduledOrderBookEventListener(OrderBookEventListener toAdd, int interval) throws SchedulerException{
//        orderBookEventListeners.add(toAdd);
//        ScheduledEvent se = (ScheduledEvent)toAdd;
//        this.scheduleManager.periodicCallback(interval, se.getJob());
//    }
    public void startPublishingOrderBookEvents() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            while (true) {
                Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;
                depthCache = this.marketDataManager.getOrderBookManager().getDepthCache();
                this.depthCache = depthCache;
                //publishOrderBookEvent(depthCache);
                //System.out.println("BEST ASK: " + toDepthCacheEntryString(depthCache.get("ASKS").lastEntry()));
                //System.out.println("BEST BID: " + toDepthCacheEntryString(depthCache.get("BIDS").firstEntry()));
            }
        });
    }

//    public void publishTradeEvent(ScheduleManager scheduleManager, AggTradeEvent tradeEvent) {
//        // Notify everybody that may be interested.
//       for (TradeEventListener tl : tradeEventlisteners)
//            tl.handleTradeEvent();
//        //scheduleManager.updateTradeEvent(tradeEvent);
//    }

    public void publishOrderBookEvent(int interval) {
        // Notify everybody that may be interested.
        for (OrderBookEventListener ol : this.orderBookEventListeners.get(interval))
            ol.handleOrderBookEvent(depthCache);
    }

//    public ScheduleManager getScheduleManager() {
//        return scheduleManager;
//    }


    /**
     * Pretty prints an order book entry in the format "price / quantity".
     */
    public static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
        return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
    }

    public static void main(String[] args) {
//        BasicConfigurator.configure();
//        try {
//        EventManager eventManager = new EventManager(new MarketDataManager("BTCUSDT"));
//        ScheduleManager scheduleManager;
//            //scheduleManager = new ScheduleManager(eventManager);
//            OrderBookEventListener listener = new ScheduledPriceUpdate();
//            eventManager.addOrderBookEventListener(listener);
//            eventManager.scheduleManager.periodicCallback(500);
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        ScheduledPriceUpdate generator = new ScheduledPriceUpdate();
//        //idk why but must be done in this order publish -> add to observer list
//        eventManager.addScheduledOrderBookEventListener(generator, 800);
//        eventManager.startPublishingOrderBookEvents();
//        eventManager.addOrderBookEventListener(generator);
//        eventManager.scheduleManager.periodicCallback(800, generator);
    }

}
