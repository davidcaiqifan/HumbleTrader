import logic.EventManager;
import logic.ScheduleManager;
import logic.dataProcessors.MarketDataManager;
import logic.listeners.MovingAverageCrossover;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        //BasicConfigurator.configure();
//        ScheduleManager sched;
//        EventManager eventManager = new EventManager(new MarketDataManager("BTCUSDT"));
//        try {
//            sched = new ScheduleManager(eventManager);
//            sched.periodicCallback(100, new SimpleMovingAverage(1000));
//            sched.periodicCallback(100, new SimpleMovingAverage(2000));
//        } catch (Exception e) {
//            System.out.println(e);
//        }

        EventManager eventManager = new EventManager();
        ExecutorService executor1 = Executors.newSingleThreadExecutor();
        executor1.submit(() -> {
            MarketDataManager marketDataManager = new MarketDataManager("BTCUSDT", eventManager);
            marketDataManager.startOrderBookStreaming();
        });
        ScheduleManager scheduleManager;
        try {
            scheduleManager = new ScheduleManager(eventManager);
            //scheduleManager.periodicCallback(2000, "test1");
            //scheduleManager.periodicCallback(1000, "test");

            ExecutorService executor2 = Executors.newSingleThreadExecutor();
            executor2.submit(() -> {
                MovingAverageCrossover movingAverageCrossover
                        = new MovingAverageCrossover(1000, 2000, 10, scheduleManager);
                eventManager.addOrderBookEventListener(movingAverageCrossover);
            });

        } catch (Exception e) {
        }

    }
}
