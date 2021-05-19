package logic.dataProcessors;

import logic.EventManager;

public class MarketDataManager {
    private BinanceGateway binanceGateway;
    private OrderBookManager orderBookManager;
    private TradeManager tradeManager;
    public MarketDataManager(String symbol) {
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
