package logic.dataProcessors;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import logic.EventManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TradeManager {
    /**
     * Key is the aggregate trade id, and the value contains the aggregated trade data, which is
     * automatically updated whenever a new agg data stream event arrives.
     */
    private Map<Long, AggTrade> aggTradesCache;
    private EventManager<Map<Long, AggTrade>> eventManager;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public TradeManager(EventManager<Map<Long, AggTrade>> eventManager, List<AggTrade> tradeList) {
        this.eventManager = eventManager;
        executor.submit(new Runnable() {
            public void run() {
                initializeAggTradesCache(tradeList);
            }
        });
    }

    /**
     * Initializes the aggTrades cache by using the REST API.
     */
    private void initializeAggTradesCache(List<AggTrade> aggTrades) {
        this.aggTradesCache = new HashMap<>();
        for (AggTrade aggTrade : aggTrades) {
            aggTradesCache.put(aggTrade.getAggregatedTradeId(), aggTrade);
        }
    }

    /**
     * Called by binance gateway to push data to tradeManager.
     */

    public void handleTradeEvent(AggTradeEvent aggTradeEvent) {
        executor.submit(new Runnable() {
            public void run() {
                Long aggregatedTradeId = aggTradeEvent.getAggregatedTradeId();
                AggTrade updateAggTrade = aggTradesCache.get(aggregatedTradeId);
                if (updateAggTrade == null) {
                    // new agg trade
                    updateAggTrade = new AggTrade();
                }
                updateAggTrade.setAggregatedTradeId(aggregatedTradeId);
                updateAggTrade.setPrice(aggTradeEvent.getPrice());
                updateAggTrade.setQuantity(aggTradeEvent.getQuantity());
                updateAggTrade.setFirstBreakdownTradeId(aggTradeEvent.getFirstBreakdownTradeId());
                updateAggTrade.setLastBreakdownTradeId(aggTradeEvent.getLastBreakdownTradeId());
                updateAggTrade.setBuyerMaker(aggTradeEvent.isBuyerMaker());

                // Store the updated agg trade in the cache
                aggTradesCache.put(aggregatedTradeId, updateAggTrade);
                eventManager.publishEvent(aggTradesCache);
            }
        });

        //System.out.println(updateAggTrade);
    }

    /**
     * @return an aggTrades cache, containing the aggregated trade id as the key,
     * and the agg trade data as the value.
     */
    public Map<Long, AggTrade> getAggTradesCache() {
        return aggTradesCache;
    }


}
