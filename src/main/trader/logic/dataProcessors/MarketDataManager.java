package logic.dataProcessors;

import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;
import logic.EventManager;
import model.AggsTradeCache;
import model.OrderBookCache;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

public class MarketDataManager {
    private BinanceGateway binanceGateway;
    private OrderBookManager orderBookManager;
    private TradeManager tradeManager;
    private EventManager eventManager;
    public MarketDataManager(String symbol, EventManager<OrderBookCache> orderBookEventManager,
                             EventManager<AggsTradeCache> tradeEventmanager) {
        this.binanceGateway = new BinanceGateway(symbol);
        this.eventManager = orderBookEventManager;
        this.orderBookManager = new OrderBookManager(orderBookEventManager, binanceGateway.getOrderBookSnapshot());
        this.tradeManager = new TradeManager(tradeEventmanager, binanceGateway.getRecentTradeSnapshot());
        startOrderBookStreaming();
        startTradeEventStreaming();
    }

    public OrderBookManager getOrderBookManager() {
        return orderBookManager;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }

    public void startOrderBookStreaming() {
        this.binanceGateway.subscribeOrderBookEvents(this.orderBookManager);
    }
    public void startTradeEventStreaming() {
        this.binanceGateway.subscribeTradeEvents(this.tradeManager);
    }
}
