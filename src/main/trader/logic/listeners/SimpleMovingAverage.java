package logic.listeners;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;

public class SimpleMovingAverage implements OrderBookEventListener{
    private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;
    private Queue<Double> priceBufferQueue = new ArrayDeque<>();
    private int period;
    private double totalValue;
    private int bufferSize;
    private int samples;
    private double price;
    private double sma;

    //takes in SMA period in milliseconds
    public SimpleMovingAverage(int period) {
        this.period = period;
        this.bufferSize = period/100;
        this.samples = 0;
        this.totalValue = 0;
    }

    @Override
    public void handleOrderBookEvent(Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache) {
        this.depthCache = depthCache;
        //System.out.println(this.depthCache.get("ASKS").lastEntry().getKey());
        this.price = calculateWeightedPrice();
        handlePrice(this.price);
        sma = this.totalValue/this.samples;
        printPrice();
    }

    public double calculateWeightedPrice() {
        NavigableMap<BigDecimal, BigDecimal> asks = this.depthCache.get("ASKS");
        NavigableMap<BigDecimal, BigDecimal> bids = this.depthCache.get("BIDS");
        double totalAskPrice = 0;
        double totalAskQuantity = 0;
        double totalBidPrice = 0;
        double totalBidQuantity = 0;
        double weightedAskPrice = 0;
        double weightedBidPrice = 0;
        for (Map.Entry<BigDecimal, BigDecimal> entry : asks.entrySet()) {
            double quantity = entry.getValue().doubleValue();
            totalAskPrice += entry.getKey().doubleValue() * quantity;
            totalAskQuantity += quantity;
        }
        for (Map.Entry<BigDecimal, BigDecimal> entry : bids.entrySet()) {
            double quantity = entry.getValue().doubleValue();
            totalBidPrice += entry.getKey().doubleValue() * quantity;
            totalBidQuantity += quantity;
        }
        weightedAskPrice = totalAskPrice / totalAskQuantity;
        weightedBidPrice = totalBidPrice / totalBidQuantity;
        double weightedMidPrice = (weightedAskPrice + weightedBidPrice) / 2;
        return weightedMidPrice;
    }

    public void handlePrice(double priceUpdate) {
        if(samples < bufferSize) {
            totalValue += priceUpdate;
            priceBufferQueue.add(priceUpdate);
            this.samples += 1;
        } else {
            totalValue -= priceBufferQueue.remove();
            priceBufferQueue.add(priceUpdate);
            totalValue += priceUpdate;
        }
    }

    public double getSma() {
        return sma;
    }

    public void printPrice() {
        if(samples == bufferSize) {
            System.out.println("SMA : " + period + "ms " + "Price :" + this.totalValue/samples);
        } else {
            System.out.println("Buffering :(");
        }
    }


}
