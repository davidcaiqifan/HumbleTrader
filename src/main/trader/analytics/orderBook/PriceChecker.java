package analytics.orderBook;

import logic.listeners.OrderBookEventListener;
import logic.schedulers.ScheduleManager;
import logic.schedulers.ScheduleEvent;
import model.OrderBookCache;

import logic.calc.Math;

import java.util.List;
import java.util.Optional;

public class PriceChecker extends OrderBookEventListener {
    private OrderBookCache localOrderBookCache;
    private double price = -1;
    private Optional<Double> optionalPrice = Optional.empty();

    /**
     *
     * @param interval Interval between price displays
     * @param scheduleManager
     */
    public PriceChecker(int interval, ScheduleManager scheduleManager) {
        super(scheduleManager, interval);
    }

    @Override
    public void handleEvent(OrderBookCache orderBookCache) {
        localOrderBookCache = orderBookCache;
        price = Math.calculateWeightedPrice(localOrderBookCache);
        optionalPrice = Optional.of(price);
        //System.out.println(this.price);
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        List<String> scheduledCallbackTags;
        //gets list of scheduled callback tags ordered based on input order in super-constructor
        scheduledCallbackTags = super.getScheduledCallbackTags();
        String referenceTag = scheduleEvent.getReferenceTag();
        if (referenceTag == scheduledCallbackTags.get(0)) {
            //perform action
            System.out.println("price : " + this.price);
        }
    }

    public double getPrice() {
        //System.out.println(this.price);
        return this.price;
    }

    public Optional<Double> getOptionalPrice() {
        return optionalPrice;
    }

    public static void main(String[] args){

    }

}
