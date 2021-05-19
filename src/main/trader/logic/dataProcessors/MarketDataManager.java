package logic.dataProcessors;

import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import logic.EventManager;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

public class MarketDataManager {
    private BinanceGateway binanceGateway;
    private OrderBookManager orderBookManager;
    private TradeManager tradeManager;
    private EventManager eventManager;
    public MarketDataManager(String symbol, EventManager eventManager) {
        this.binanceGateway = new BinanceGateway(symbol);
        this.eventManager = eventManager;
        this.orderBookManager = new OrderBookManager(eventManager, binanceGateway.getOrderBookSnapshot());
        //this.tradeManager = new TradeManager(this.binanceGateway);
        startOrderBookStreaming();
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
}
