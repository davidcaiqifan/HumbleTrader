package Analytics;

import logic.calc.Math;
import logic.listeners.OrderBookEventListener;
import logic.schedulers.ScheduleEvent;
import logic.schedulers.ScheduleManager;
import model.OrderBookCache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RiskWatcher implements OrderBookEventListener {
    private OrderBookCache localOrderBookCache;
    private ScheduleManager scheduleManager;
    private int threshold;
    private double price;
    private int signal;
    private ScheduledExecutorService ExecutorServiceOne;
    private ScheduledExecutorService ExecutorServiceTwo;

    /**
     * Simple risk manager that generates message when threshold limit is reached
     */
    public RiskWatcher(int threshold, ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
        this.threshold = threshold;
        this.scheduleManager.getEventManager().addOrderBookEventListener(this);
        try {
            //we want risk manager to always have the latest price updates, so interval is 100ms(same as websocket interval)
            scheduleManager.periodicCallback(100, "riskwatcher");
            scheduleManager.periodicCallback(1000, "informuser");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void handleOrderBookEvent(OrderBookCache orderBookCache) {

        localOrderBookCache = orderBookCache;
        price = Math.calculateWeightedPrice(localOrderBookCache);
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        String referenceTag = scheduleEvent.getReferenceTag();
        if (referenceTag == "riskwatcher") {
            if (price < threshold) {
                signal = -1;
            } else {
                signal = 0;
            }

        } else if (referenceTag == "informuser") {
            if (signal == -1) {
                System.out.println("panik");
            } else {
                //System.out.println("kalm");
            }
        }
    }

    public double getThreshold() {
        return this.threshold;
    }
}
