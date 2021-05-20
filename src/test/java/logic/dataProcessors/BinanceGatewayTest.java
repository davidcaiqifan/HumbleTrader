package logic.dataProcessors;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import customWebSockets.BinanceCustomWebSocketClientImpl;
import org.junit.Test;

import static com.binance.api.client.impl.BinanceApiServiceGenerator.getSharedClient;
import static org.junit.Assert.assertEquals;

public class BinanceGatewayTest {
    private String symbol = "BTCUSDT";
    private BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();;
    private BinanceApiRestClient restClient = this.factory.newRestClient();
    private BinanceCustomWebSocketClientImpl webSocketClient = new BinanceCustomWebSocketClientImpl(getSharedClient());
    private BinanceGateway binanceGateway = new BinanceGateway(symbol);
    @Test
    public void testGetOrderBookSnapshot() {
        assertEquals(binanceGateway.getOrderBookSnapshot().getClass(),
                restClient.getOrderBook(this.symbol.toUpperCase(), 10).getClass());
    }

    @Test
    public void testGetRecentTradeSnapshot() {
    }

    @Test
    public void testSubscribeTradeEvents() {
    }

    @Test
    public void testSubscribeOrderBookEvents() {
    }

}