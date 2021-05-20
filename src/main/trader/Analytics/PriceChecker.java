package Analytics;

import logic.listeners.OrderBookEventListener;
import logic.schedulers.ScheduleManager;
import logic.schedulers.ScheduleEvent;
import model.OrderBookCache;

import logic.calc.Math;

public class PriceChecker implements OrderBookEventListener{

    private OrderBookCache orderBookCache;
    private double price;
    private ScheduleManager scheduleManager;

    public PriceChecker(int interval, ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
        this.scheduleManager.getEventManager().addOrderBookEventListener(this);
        try {
            scheduleManager.periodicCallback(interval, "price");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void handleOrderBookEvent(OrderBookCache orderBookCache) {
        this.orderBookCache = orderBookCache;
        this.price = Math.calculateWeightedPrice(this.orderBookCache);
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        String referenceTag = scheduleEvent.getReferenceTag();
        if(referenceTag == "price") {
            System.out.println("price : " + this.price);
        }
    }


}
