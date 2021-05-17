package logic;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class BinanceGateway {
    private String symbol;
    private LinkedBlockingDeque<AggTradeEvent> tradeEventQueue;
    private LinkedBlockingDeque<DepthEvent> orderBookEventQueue;
    public BinanceGateway(String symbol) {
        this.symbol = symbol;
        this.tradeEventQueue = new LinkedBlockingDeque<>();
        this.orderBookEventQueue = new LinkedBlockingDeque<>();
    }
    public OrderBook getOrderBookSnapshot() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        OrderBook orderBook = client.getOrderBook(this.symbol.toUpperCase(), 10);
        return orderBook;
    };

    public List<AggTrade> getRecentTradeSnapshot() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        List<AggTrade> aggTrades = client.getAggTrades(this.symbol.toUpperCase());
        return aggTrades;
    }

    public void subscribeTradeEvents() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();
        client.onAggTradeEvent(this.symbol.toLowerCase(), response -> {
            tradeEventQueue.add(response);
        });
    }

    /**
     * Obtain trade event updates from websocket
     */
    public AggTradeEvent getTradeEvent() throws InterruptedException{
        try {
            return this.tradeEventQueue.take();
        } catch(Exception e) {
            throw e;
        }
    }

    public void subscribeOrderBookEvents() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();
        client.onDepthEvent(symbol.toLowerCase(), response -> {
            this.orderBookEventQueue.add(response);
        });
    }

    /**
     * Obtain order book event updates from websocket
     */
    public DepthEvent getOrderBookEvent() throws InterruptedException{
        try {
            return this.orderBookEventQueue.take();
        } catch(Exception e) {
            throw e;
        }
    }



}
