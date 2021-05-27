package logic.dataProcessors;

import com.binance.api.client.domain.event.AggTradeEvent;
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
    private TradeEventManager tradeEventManager;
    public MarketDataManager(String symbol, EventManager<OrderBookCache> orderBookEventManager,
                             EventManager<AggTradeEvent> tradeEventmanager) {
        this.binanceGateway = new BinanceGateway(symbol);
        this.orderBookManager = new OrderBookManager(orderBookEventManager, binanceGateway.getOrderBookSnapshot());
        this.tradeEventManager = new TradeEventManager(tradeEventmanager);
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
        this.binanceGateway.subscribeTradeEvents(this.tradeEventManager);
    }
    public BinanceGateway getBinanceGateway() {
        return binanceGateway;
    }
}
