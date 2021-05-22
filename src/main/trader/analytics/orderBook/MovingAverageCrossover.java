package analytics.orderBook;

import logic.calc.Math;
import logic.listeners.OrderBookEventListener;
import logic.schedulers.ScheduleManager;
import logic.calc.SimpleMovingAverage;
import logic.schedulers.ScheduleEvent;
import model.OrderBookCache;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class MovingAverageCrossover extends OrderBookEventListener {
    private OrderBookCache localOrderBookCache;
    private double priceSample;
    private SimpleMovingAverage simpleMovingAverage1;
    private SimpleMovingAverage simpleMovingAverage2;
    private int signal = 0;

    /**
     * Generates moving average crossover signal
     *
     * @param period1         Period of first moving average.
     * @param period2         Period of second moving average.
     * @param window          Window size for samples.
     * @param scheduleManager ScheduleManager for periodic callback purposes
     */
    public MovingAverageCrossover(int period1, int period2, int window, int signalInterval, ScheduleManager scheduleManager) {
        super(scheduleManager, period1, period2, signalInterval);
        this.simpleMovingAverage1 = new SimpleMovingAverage(window);
        this.simpleMovingAverage2 = new SimpleMovingAverage(window);
    }

    @Override
    public void handleEvent(OrderBookCache orderBookCache) {
        localOrderBookCache = orderBookCache;
        priceSample = Math.calculateWeightedPrice(localOrderBookCache);
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        List<String> scheduledCallbackTags;
        //gets list of scheduled callback tags ordered based on input order in super-constructor
        scheduledCallbackTags = super.getScheduledCallbackTags();
        String referenceTag = scheduleEvent.getReferenceTag();
        if (referenceTag == scheduledCallbackTags.get(0)) {
            simpleMovingAverage1.updateSamples(priceSample);
            //System.out.println("sma1 : " + simpleMovingAverage1.getSimpleMovingAverage());
        } else if (referenceTag == scheduledCallbackTags.get(1)) {
            simpleMovingAverage2.updateSamples(priceSample);
            //System.out.println("sma2 : " + simpleMovingAverage2.getSimpleMovingAverage());
        } else if (referenceTag == scheduledCallbackTags.get(2)) {
            generateSignal();
            printSignal();

        }
    }

    /**
     * Generates moving average crossover signal represented by -1, 0 or 1.
     */
    private void generateSignal() {
        double sma1 = simpleMovingAverage1.getSimpleMovingAverage();
        double sma2 = simpleMovingAverage2.getSimpleMovingAverage();
        //Generate neutral signal if moving averages are still buffering data
        if (sma1 == -1 || sma2 == -1) {
            this.signal = 0;
        } else if (sma1 < sma2) {
            this.signal = 1;
        } else {
            this.signal = -1;
        }
    }

    private void printSignal() {
        if (signal == 0) {
            System.out.println("Neutral");
        } else if (signal == 1) {
            System.out.println("To the moon!!");
        } else {
            System.out.println("Not stonks");
        }
    }

    public double getFirstAverage() {
        return simpleMovingAverage1.getSimpleMovingAverage();
    }

    public double getSecondAverage() {
        return simpleMovingAverage2.getSimpleMovingAverage();
    }
}
