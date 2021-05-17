package logic;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import customWebSockets.BinanceCustomWebSocketClientImpl;
import model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import static com.binance.api.client.impl.BinanceApiServiceGenerator.getSharedClient;

public class EventManager {
    /**
     * Key is the aggregate trade id, and the value contains the aggregated trade data, which is
     * automatically updated whenever a new agg data stream event arrives.
     */
    private Map<Long, AggTrade> aggTradesCache;
    private DataGatewayManager dataGatewayManager;

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }
    private ScheduleManager scheduleManager;
    private List<TradeEventListener> tradeEventlisteners = new ArrayList<TradeEventListener>();
    public EventManager(DataGatewayManager dataGatewayManager) {
        this.dataGatewayManager = dataGatewayManager;
        this.scheduleManager = new ScheduleManager();
        initializeAggTradesCache("BTCUSDT");
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                startAggTradesEventStreaming("BTCUSDT");
            }
        });
        t1.start();
    }

    /**
     * Initializes the aggTrades cache by using the REST API.
     */
    private void initializeAggTradesCache(String symbol) {
        List<AggTrade> aggTrades = dataGatewayManager.getTradeSnapshot(symbol);
        this.aggTradesCache = new HashMap<>();
        for (AggTrade aggTrade : aggTrades) {
            aggTradesCache.put(aggTrade.getAggregatedTradeId(), aggTrade);
        }
    }

    /**
     * Begins streaming of agg trades events.
     */
    private void startAggTradesEventStreaming(String symbol) {
        this.dataGatewayManager.subscribeTradeEvents(symbol);
        while(true) {
            try {
                AggTradeEvent response = this.dataGatewayManager.getTradeEvent();
                publishTradeEvent(this.scheduleManager, response);
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
                // Store the updated agg trade in the cache
                aggTradesCache.put(aggregatedTradeId, updateAggTrade);
                System.out.println(updateAggTrade.getPrice());
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }

    }

    /**
     * Add trade event listeners.
     */
    public void addTradeEventListener(TradeEventListener toAdd) {
        tradeEventlisteners.add(toAdd);
    }

    public void publishTradeEvent(ScheduleManager scheduleManager, AggTradeEvent tradeEvent) {
        // Notify everybody that may be interested.
//        for (TradeEventListener tl : tradeEventlisteners)
//            tl.handleTradeEvent();
        scheduleManager.updateTradeEvent(tradeEvent);
    }

    public static void main(String[] args) {
        new EventManager(new DataGatewayManager());
    }

}
