import analytics.orderBook.PriceChecker;
import com.binance.api.client.domain.market.AggTrade;
import logic.EventManager;
import logic.schedulers.ScheduleManager;
import logic.dataProcessors.MarketDataManager;
import model.OrderBookCache;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        //BasicConfigurator.configure();
        EventManager orderBookEventManager = new EventManager<OrderBookCache>();
        EventManager tradeEventManager = new EventManager<Map<Long, AggTrade>>();
        executorService.submit(() -> {
            MarketDataManager marketDataManager
                    = new MarketDataManager("BTCUSDT", orderBookEventManager, tradeEventManager);
            marketDataManager.startOrderBookStreaming();
        });
        try {
            ScheduleManager scheduleManager;
            ScheduleManager tradeScheduleManager;
            scheduleManager = new ScheduleManager(orderBookEventManager);
            tradeScheduleManager = new ScheduleManager(tradeEventManager);
            PriceChecker priceChecker = new PriceChecker(100, scheduleManager);
        } catch (Exception e) {
        }
    }
}
