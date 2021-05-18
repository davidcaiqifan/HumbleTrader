package logic.dataProcessors;

public class MarketDataManager {
    private BinanceGateway binanceGateway;
    private OrderBookManager orderBookManager;
    private TradeManager tradeManager;
    public MarketDataManager(String symbol) {
        this.binanceGateway = new BinanceGateway(symbol);
        this.orderBookManager = new OrderBookManager(this.binanceGateway);
        this.tradeManager = new TradeManager(this.binanceGateway);
    }

    public OrderBookManager getOrderBookManager() {
        return orderBookManager;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }
}
