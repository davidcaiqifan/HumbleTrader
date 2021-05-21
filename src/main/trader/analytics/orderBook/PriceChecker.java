package analytics.orderBook;

import logic.listeners.OrderBookEventListener;
import logic.schedulers.ScheduleManager;
import logic.schedulers.ScheduleEvent;
import model.OrderBookCache;

import logic.calc.Math;

public class PriceChecker implements OrderBookEventListener {

    private OrderBookCache localOrderBookCache;
    private double price;
    private ScheduleManager scheduleManager;

    public PriceChecker(int interval, ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
        this.scheduleManager.getEventManager().addEventListener(this);
        //Creates two threads for this listener
        try {
            scheduleManager.periodicCallback(interval, "price");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void handleEvent(OrderBookCache orderBookCache) {

        localOrderBookCache = orderBookCache;
        price = Math.calculateWeightedPrice(localOrderBookCache);

        //System.out.println(this.price);
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        String referenceTag = scheduleEvent.getReferenceTag();
        if (referenceTag == "price") {
            //System.out.println("price : " + this.price);
        }
    }

    public double getPrice() {
        //System.out.println(this.price);
        return this.price;
    }

}
