package analytics.orderBook;

import org.quartz.SchedulerException;
import logic.calc.Math;
import logic.listeners.OrderBookEventListener;
import logic.schedulers.ScheduleEvent;
import logic.schedulers.ScheduleManager;
import model.OrderBookCache;

public class OrderImbalanceRiskWatcher implements OrderBookEventListener {
    private ScheduleManager scheduleManager;
    private double imbalance;
    private double threshold;
    private int signal;

    public OrderImbalanceRiskWatcher(double threshold, ScheduleManager scheduleManager) {
        this.threshold = threshold;
        this.scheduleManager = scheduleManager;
        this.scheduleManager.getEventManager().addEventListener(this);
        try {
            scheduleManager.periodicCallback(100, "orderImbalanceRiskWatcher");
            scheduleManager.periodicCallback(1000, "informUser");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleEvent(OrderBookCache orderBookCache) {
        imbalance = Math.getOrderImbalance(10, orderBookCache);
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        String referenceTag = scheduleEvent.getReferenceTag();
        if (referenceTag == "orderImbalanceRiskWatcher") {
            if (imbalance > threshold) {
                // imbalance in favour of bid side
                signal = 1;
            } else if (imbalance < -1 * threshold) {
                // imbalance in favour of sell side
                signal = -1;
            } else {
                signal = 0;
            }

        } else if (referenceTag == "informUser") {
            if (signal == -1) {
                System.out.println("Too many sell orders: " + imbalance);
            } else if (signal == 1) {
                System.out.println("Too many buy orders: " + imbalance);
            } else {
                System.out.println("Balanced Orders");
            }
        }
    }
}
