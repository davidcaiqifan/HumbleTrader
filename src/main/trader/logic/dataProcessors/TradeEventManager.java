package logic.dataProcessors;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import logic.EventManager;
import model.AggsTradeCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TradeEventManager {
    private EventManager<AggTradeEvent> eventManager;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public TradeEventManager(EventManager<AggTradeEvent> eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Called by binance gateway to push data to TradeEventManager.
     */

    public void handleTradeEvent(AggTradeEvent aggTradeEvent) {
        executor.submit(new Runnable() {
            public void run() {
                eventManager.publishEvent(aggTradeEvent);
            }
        });

        //System.out.println(updateAggTrade);
    }
}
