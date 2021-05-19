package logic.listeners;

import logic.ScheduleManager;
import logic.calc.SimpleMovingAverage;
import logic.schedulers.ScheduleEvent;
import model.OrderBookCache;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;

public class MovingAverageCrossover implements OrderBookEventListener{
    private OrderBookCache orderBookCache;
    private double priceSample;
    private SimpleMovingAverage simpleMovingAverage1;
    private SimpleMovingAverage simpleMovingAverage2;

    //takes in SMA period in milliseconds
    public MovingAverageCrossover(int period1, int period2, int window, ScheduleManager scheduleManager) {
        this.simpleMovingAverage1 = new SimpleMovingAverage(10);
        this.simpleMovingAverage2 = new SimpleMovingAverage(10);
        //initialize periodic callbacks
        try {
            scheduleManager.periodicCallback(period1, "sma1");
            scheduleManager.periodicCallback(period2, "sma2");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void handleOrderBookEvent(OrderBookCache orderBookCache) {
        this.orderBookCache = orderBookCache;
        this.priceSample = calculateWeightedPrice();
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        String referenceTag = scheduleEvent.getReferenceTag();
        if(referenceTag == "sma1") {
            simpleMovingAverage1.updateSamples(this.priceSample);
            System.out.println("sma1 : " + simpleMovingAverage1.getSimpleMovingAverage());
        } else {
            simpleMovingAverage2.updateSamples(this.priceSample);
            System.out.println("sma2 : " + simpleMovingAverage2.getSimpleMovingAverage());
        }
    }

    public double calculateWeightedPrice() {
        double bestAskPrice = this.orderBookCache.getBestAsk().getKey().doubleValue();
        double bestAskQuantity = this.orderBookCache.getBestAsk().getValue().doubleValue();
        double bestBidPrice = this.orderBookCache.getBestBid().getKey().doubleValue();
        double bestBidQuantity = this.orderBookCache.getBestBid().getValue().doubleValue();;
        double totalQuantity = bestAskQuantity + bestBidQuantity;
        double weightedAskPrice = (bestAskPrice * bestAskQuantity)/totalQuantity;
        double weightedBidPrice = (bestBidPrice * bestBidQuantity)/totalQuantity;
        double weightedMidPrice = weightedAskPrice + weightedBidPrice;
        return weightedMidPrice;
    }



}
