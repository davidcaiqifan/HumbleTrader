package logic.stats;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import logic.BinanceGateway;

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
    private BinanceGateway binanceGateway;
    private long lastTradeId;
    public TradeManager(BinanceGateway binanceGateway) {
        this.binanceGateway = binanceGateway;
        initializeAggTradesCache();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            startAggTradesEventStreaming();
        });

    }

    /**
     * Initializes the aggTrades cache by using the REST API.
     */
    private void initializeAggTradesCache() {
        List<AggTrade> aggTrades = this.binanceGateway.getRecentTradeSnapshot();
        this.aggTradesCache = new HashMap<>();
        for (AggTrade aggTrade : aggTrades) {
            lastTradeId = aggTrade.getAggregatedTradeId();
            aggTradesCache.put(aggTrade.getAggregatedTradeId(), aggTrade);
        }
    }

    /**
     * Begins streaming of agg trades events.
     */
    private void startAggTradesEventStreaming() {
        this.binanceGateway.subscribeTradeEvents();
        while(true) {
            try {
                AggTradeEvent response = this.binanceGateway.getTradeEvent();
                Long aggregatedTradeId = response.getAggregatedTradeId();
                AggTrade updateAggTrade = aggTradesCache.get(aggregatedTradeId);
                if (updateAggTrade == null) {
                    // new agg trade
                    updateAggTrade = new AggTrade();
                }
                updateAggTrade.setAggregatedTradeId(aggregatedTradeId);
                updateAggTrade.setPrice(response.getPrice());
                updateAggTrade.setQuantity(response.getQuantity());
                updateAggTrade.setFirstBreakdownTradeId(response.getFirstBreakdownTradeId());
                updateAggTrade.setLastBreakdownTradeId(response.getLastBreakdownTradeId());
                updateAggTrade.setBuyerMaker(response.isBuyerMaker());
                lastTradeId = aggregatedTradeId;
                // Store the updated agg trade in the cache
                aggTradesCache.put(aggregatedTradeId, updateAggTrade);
                //System.out.println(updateAggTrade);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
    }
    /**
     * @return an aggTrades cache, containing the aggregated trade id as the key,
     * and the agg trade data as the value.
     */
    public Map<Long, AggTrade> getAggTradesCache() {
        return aggTradesCache;
    }

    public double getLastTradePrice() {
        return Double.parseDouble(aggTradesCache.get(lastTradeId).getPrice());
    }

//    public static void main(String[] args) {
//        new TradeManager(new EventManager());
//    }
}
