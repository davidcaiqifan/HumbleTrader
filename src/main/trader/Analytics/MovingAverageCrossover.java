package Analytics;

import logic.calc.Math;
import logic.listeners.OrderBookEventListener;
import logic.schedulers.ScheduleManager;
import logic.calc.SimpleMovingAverage;
import logic.schedulers.ScheduleEvent;
import model.OrderBookCache;

public class MovingAverageCrossover implements OrderBookEventListener {
    private ScheduleManager scheduleManager;
    private OrderBookCache orderBookCache;
    private double priceSample;
    private SimpleMovingAverage simpleMovingAverage1;
    private SimpleMovingAverage simpleMovingAverage2;
    private int signal = 0;

    /**
     * Generates moving average crossover signal
     * @param period1 Period of first moving average.
     * @param period2 Period of second moving average.
     * @param window Window size for samples.
     * @param scheduleManager ScheduleManager for periodic callback purposes
     */
    public MovingAverageCrossover(int period1, int period2, int window, int signalInterval, ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
        this.scheduleManager.getEventManager().addOrderBookEventListener(this);
        this.simpleMovingAverage1 = new SimpleMovingAverage(window);
        this.simpleMovingAverage2 = new SimpleMovingAverage(window);
        //initialize periodic callbacks
        try {
            scheduleManager.periodicCallback(period1, "sma1");
            scheduleManager.periodicCallback(period2, "sma2");
            scheduleManager.periodicCallback(signalInterval, "sigint");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void handleOrderBookEvent(OrderBookCache orderBookCache) {
        this.orderBookCache = orderBookCache;
        this.priceSample = Math.calculateWeightedPrice(this.orderBookCache);
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        String referenceTag = scheduleEvent.getReferenceTag();
        if(referenceTag == "sma1") {
            simpleMovingAverage1.updateSamples(this.priceSample);
            //System.out.println("sma1 : " + simpleMovingAverage1.getSimpleMovingAverage());
        } else if (referenceTag == "sma2"){
            simpleMovingAverage2.updateSamples(this.priceSample);
            //System.out.println("sma2 : " + simpleMovingAverage2.getSimpleMovingAverage());
        } else if (referenceTag == "sigint") {
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
        if(sma1 == -1 || sma2 == -1) {
            this.signal = 0;
        } else if (sma1 < sma2) {
            this.signal = 1;
        } else {
            this.signal = -1;
        }
    }

    private void printSignal() {
        if(signal == 0) {
            System.out.println("Neutral");
        } else if(signal == 1) {
            System.out.println("To the moon!!");
        } else {
            System.out.println("Not stonks");
        }
    }
}
