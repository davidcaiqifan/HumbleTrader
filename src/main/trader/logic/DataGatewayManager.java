package logic;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import model.Event;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class DataGatewayManager {
    private LinkedBlockingDeque<AggTradeEvent> tradeEventQueue;
    public DataGatewayManager() {
        this.tradeEventQueue = new LinkedBlockingDeque<>();
    }
    public void subscribeTradeEvents(String symbol) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();
        client.onAggTradeEvent(symbol.toLowerCase(), response -> {
            tradeEventQueue.add(response);
        });
    }
    public List<AggTrade> getTradeSnapshot(String symbol) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        List<AggTrade> aggTrades = client.getAggTrades(symbol.toUpperCase());
        return aggTrades;
    }
    public AggTradeEvent getTradeEvent() throws InterruptedException{
        try {
            return tradeEventQueue.take();
        } catch(Exception e) {
            throw e;
        }
    }
}
