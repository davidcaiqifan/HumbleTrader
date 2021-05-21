import analytics.orderBook.PriceChecker;
import logic.EventManager;
import logic.schedulers.ScheduleManager;
import logic.dataProcessors.MarketDataManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        //BasicConfigurator.configure();
//        EventManager eventManager = new EventManager();
//        ExecutorService executor1 = Executors.newSingleThreadExecutor();
//        executor1.submit(() -> {
//            MarketDataManager marketDataManager = new MarketDataManager("BTCUSDT", eventManager);
//            marketDataManager.startOrderBookStreaming();
//        });
//        ScheduleManager scheduleManager;
//        try {
//            scheduleManager = new ScheduleManager(eventManager);
            //scheduleManager.periodicCallback(2000, "test1");
            //scheduleManager.periodicCallback(1000, "test");

//            ExecutorService executor2 = Executors.newSingleThreadExecutor();
//            executor2.submit(() -> {
//                MovingAverageCrossover movingAverageCrossover
//                        = new MovingAverageCrossover(500, 1000, 10, 1000, scheduleManager);
//            });
//            ExecutorService executor3 = Executors.newSingleThreadExecutor();
//            executor3.submit(() -> {
//                PriceChecker priceChecker
//                        = new PriceChecker(1000, scheduleManager);
//            });
//            ExecutorService executor4 = Executors.newSingleThreadExecutor();
//            executor4.submit(() -> {
//                RiskWatcher riskWatcher
//                        = new RiskWatcher(39600, scheduleManager);
//            });
//        } catch (Exception e) {
//        }
//
    }
}
