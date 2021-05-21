package logic;

import logic.calc.Math;
import logic.listeners.OrderBookEventListener;
import logic.listeners.TradeEventListener;
import logic.schedulers.ScheduleEvent;
import logic.schedulers.ScheduleJob;
import model.OrderBookCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.quartz.JobBuilder.newJob;

public class EventManager {
    private List<OrderBookEventListener> orderBookEventListeners = new ArrayList<>();
    private List<TradeEventListener> tradeEventListeners = new ArrayList<>();

    public EventManager() {
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
    public void addOrderBookEventListener(OrderBookEventListener orderBookEventListener) {
        orderBookEventListeners.add(orderBookEventListener);
    }

    public void publishOrderBookEvent(OrderBookCache orderBookCache) {
        //handle listener multithreading
        ExecutorService executor = Executors.newCachedThreadPool();
        // Notify everybody that may be interested.
        for (OrderBookEventListener ol : this.orderBookEventListeners) {
            executor.submit(new Runnable() {
                public void run() {
                    ol.handleOrderBookEvent(orderBookCache);
                }
            });
        }
    }

    public void publishScheduleEvent(ScheduleEvent scheduleEvent) {
        ExecutorService executor = Executors.newCachedThreadPool();
        for (OrderBookEventListener ol : this.orderBookEventListeners) {
            executor.submit(new Runnable() {
                public void run() {
                    ol.handleScheduleEvent(scheduleEvent);
                }
            });
        }
    }
}
