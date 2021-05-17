package logic.stats;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import logic.EventManager;
import model.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class TradeManager {
    /**
     * Key is the aggregate trade id, and the value contains the aggregated trade data, which is
     * automatically updated whenever a new agg data stream event arrives.
     */
    private Map<Long, AggTrade> aggTradesCache;
    private EventManager eventManager;
    private LinkedBlockingDeque<Event> tradeEventQueue;
    private long lastTradeId;
//    public TradeManager(EventManager eventManager) {
//        this.eventManager = eventManager;
//        int index = eventManager.addEventStream("trade");
//        initializeAggTradesCache("BTCUSDT");
//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                startAggTradesEventStreaming(index);
//            }
//        });
//        t1.start();
//    }
//
//    /**
//     * Initializes the aggTrades cache by using the REST API.
//     */
//    private void initializeAggTradesCache(String symbol) {
//        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
//        BinanceApiRestClient client = factory.newRestClient();
//        List<AggTrade> aggTrades = client.getAggTrades(symbol.toUpperCase());
//
//        this.aggTradesCache = new HashMap<>();
//        for (AggTrade aggTrade : aggTrades) {
//            lastTradeId = aggTrade.getAggregatedTradeId();
//            aggTradesCache.put(aggTrade.getAggregatedTradeId(), aggTrade);
//        }
//    }
//
//    /**
//     * Begins streaming of agg trades events.
//     */
//    private void startAggTradesEventStreaming(int eventQueueIndex) {
//        this.tradeEventQueue = eventManager.getEventQueue(eventQueueIndex);
//        while(true) {
//            try {
//                AggTradeEvent response = this.tradeEventQueue.take().getTradeEvent();
//                Long aggregatedTradeId = response.getAggregatedTradeId();
//                AggTrade updateAggTrade = aggTradesCache.get(aggregatedTradeId);
//                if (updateAggTrade == null) {
//                    // new agg trade
//                    updateAggTrade = new AggTrade();
//                }
//                updateAggTrade.setAggregatedTradeId(aggregatedTradeId);
//                updateAggTrade.setPrice(response.getPrice());
//                updateAggTrade.setQuantity(response.getQuantity());
//                updateAggTrade.setFirstBreakdownTradeId(response.getFirstBreakdownTradeId());
//                updateAggTrade.setLastBreakdownTradeId(response.getLastBreakdownTradeId());
//                updateAggTrade.setBuyerMaker(response.isBuyerMaker());
//                lastTradeId = aggregatedTradeId;
//                // Store the updated agg trade in the cache
//                aggTradesCache.put(aggregatedTradeId, updateAggTrade);
//                //System.out.println(updateAggTrade);
//            } catch(InterruptedException e) {
//                System.out.println(e);
//            }
//        }
//
//    }
//    /**
//     * @return an aggTrades cache, containing the aggregated trade id as the key,
//     * and the agg trade data as the value.
//     */
//    public Map<Long, AggTrade> getAggTradesCache() {
//        return aggTradesCache;
//    }
//
//    public double getLastTradePrice() {
//        return Double.parseDouble(aggTradesCache.get(lastTradeId).getPrice());
//    }
//
//    public static void main(String[] args) {
//        new TradeManager(new EventManager());
//    }
}
