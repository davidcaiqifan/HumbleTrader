package logic;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import logic.stats.OrderBookManager;
import logic.stats.TradeManager;
import model.Event;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

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
