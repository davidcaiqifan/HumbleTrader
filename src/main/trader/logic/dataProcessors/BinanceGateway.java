package logic.dataProcessors;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;
import customWebSockets.BinanceCustomWebSocketClientImpl;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import static com.binance.api.client.impl.BinanceApiServiceGenerator.getSharedClient;

public class BinanceGateway {
    private String symbol;
    private BinanceApiClientFactory factory;
    private BinanceApiRestClient restClient;
    private BinanceCustomWebSocketClientImpl webSocketClient;

    public BinanceGateway(String symbol) {
        this.symbol = symbol;
        this.factory = BinanceApiClientFactory.newInstance();
        this.restClient = this.factory.newRestClient();
        this.webSocketClient = new BinanceCustomWebSocketClientImpl(getSharedClient());
    }
    public OrderBook getOrderBookSnapshot() {
        OrderBook orderBook = restClient.getOrderBook(this.symbol.toUpperCase(), 10);
        return orderBook;
    };

    public List<AggTrade> getRecentTradeSnapshot() {
        List<AggTrade> aggTrades = restClient.getAggTrades(this.symbol.toUpperCase());
        return aggTrades;
    }

    public void subscribeTradeEvents(TradeManager tradeManager) {
        webSocketClient.onAggTradeEvent(this.symbol.toLowerCase(), response -> {

        });
    }

    public void subscribeOrderBookEvents(OrderBookManager orderBookManager) {
        webSocketClient.onDepthEvent(symbol.toLowerCase(), response -> {
            orderBookManager.handleOrderBookEvent(response);
        });
    }
}
