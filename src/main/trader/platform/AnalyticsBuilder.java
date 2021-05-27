package platform;

import com.binance.api.client.domain.event.AggTradeEvent;
import logic.EventManager;
import logic.EventManagerFactory;
import logic.dataProcessors.BinanceGateway;
import logic.dataProcessors.MarketDataManager;
import logic.schedulers.ScheduleManager;
import model.OrderBookCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyticsBuilder {
    private String symbol;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private EventManagerFactory eventManagerFactory = new EventManagerFactory();
    private EventManager<OrderBookCache> orderBookCacheEventManager;
    private EventManager<AggTradeEvent> tradeEventManager;
    private MarketDataManager marketDataManager;
    private BinanceGateway binanceGateway;

    public ScheduleManager getOrderBookScheduleManager() {
        return orderBookScheduleManager;
    }

    private ScheduleManager orderBookScheduleManager;

    public ScheduleManager getTradeScheduleManager() {
        return tradeScheduleManager;
    }

    private ScheduleManager tradeScheduleManager;

    /**
     * Build's analytics platform given a symbol.
     * @param symbol
     */
    public AnalyticsBuilder(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Creates a new instance of EventManager<OrderBookCache>. Only one instance is allowed to exist.
     */
    public AnalyticsBuilder withOrderBook() {
        this.orderBookCacheEventManager = this.eventManagerFactory.getEventManager(OrderBookCache.class);
        try {
            this.orderBookScheduleManager = new ScheduleManager(orderBookCacheEventManager);
        } catch(Exception e) {
            System.out.println(e);
        }
        return this;
    }

    /**
     * Creates a new instance of EventManager<OrderBookCache>. Only one instance is allowed to exist.
     */
    public AnalyticsBuilder withAggsTrade() {
        this.tradeEventManager = this.eventManagerFactory.getEventManager(AggTradeEvent.class);
        try {
            this.tradeScheduleManager = new ScheduleManager(tradeEventManager);
        } catch(Exception e) {
            System.out.println(e);
        }
        return this;
    }

    //should not multi-thread for some reason
    /**
     * initializes market data manager
     */
    public void initialize() {
        //executorService.submit(() -> {
            this.marketDataManager
                    = new MarketDataManager("BTCUSDT", orderBookCacheEventManager, tradeEventManager);
            marketDataManager.startOrderBookStreaming();
        //});
    }

    public BinanceGateway getBinanceGateway() {
        //executorService.submit(() -> {
            binanceGateway = marketDataManager.getBinanceGateway();
        //});
        return this.binanceGateway;
    }
}
