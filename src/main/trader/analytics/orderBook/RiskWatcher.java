package analytics.orderBook;

import logic.calc.Math;
import logic.listeners.OrderBookEventListener;
import logic.schedulers.ScheduleEvent;
import logic.schedulers.ScheduleManager;
import model.OrderBookCache;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class RiskWatcher extends OrderBookEventListener {
    private OrderBookCache localOrderBookCache;
    private int threshold;
    private double price;
    private int signal;

    /**
     * Simple risk manager that generates message when threshold limit is reached
     */
    public RiskWatcher(int threshold, int interval, ScheduleManager scheduleManager) {
        super(scheduleManager, interval);
        this.threshold = threshold;
    }

    @Override
    public void handleEvent(OrderBookCache orderBookCache) {

        localOrderBookCache = orderBookCache;
        price = Math.calculateWeightedPrice(localOrderBookCache);
        if (price < threshold) {
            signal = -1;
        } else {
            signal = 0;
        }
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        List<String> scheduledCallbackTags;
        //gets list of scheduled callback tags ordered based on input order in super-constructor
        scheduledCallbackTags = super.getScheduledCallbackTags();
        String referenceTag = scheduleEvent.getReferenceTag();
        if (referenceTag == scheduledCallbackTags.get(0)) {
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
