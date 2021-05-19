import logic.EventManager;
import logic.ScheduleManager;
import logic.dataProcessors.MarketDataManager;
import logic.listeners.ScheduledPriceManager;
import logic.listeners.SimpleMovingAverage;

public class Main {
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        ScheduleManager sched;
        EventManager eventManager = new EventManager(new MarketDataManager("BTCUSDT"));
        try {
            sched = new ScheduleManager(eventManager);
            sched.periodicCallback(100, new SimpleMovingAverage(1000));
            sched.periodicCallback(100, new SimpleMovingAverage(2000));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
