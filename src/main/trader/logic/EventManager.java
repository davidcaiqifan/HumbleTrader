package logic;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.DepthEvent;
import customWebSockets.BinanceCustomWebSocketClientImpl;
import model.Event;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;

import static com.binance.api.client.impl.BinanceApiServiceGenerator.getSharedClient;

public class EventManager {
    public HashMap<Integer, LinkedBlockingDeque<Event>> eventQueueMap;
    public EventManager() {
        this.eventQueueMap = new HashMap<>();
    }

    //initializes event stream based on input
    public int addEventStream(String eventType) {
        //write error manage code later
        if(eventType == "orderbook") {
            eventQueueMap.put(1, new LinkedBlockingDeque<Event>());
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    startDepthEventStreaming("BTCUSDT", 1);
                }
            });
            t1.start();
            return 1;
        } else if(eventType == "trade") {
            eventQueueMap.put(2, new LinkedBlockingDeque<Event>());
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    startAggTradesEventStreaming("BTCUSDT", 2);
                }
            });
            t2.start();
            return 2;
        }
            else {
            return 0;
        }
    }

    public LinkedBlockingDeque<Event> getEventQueue(int eventQueueIndex) {
        return eventQueueMap.get(eventQueueIndex);
    }

    /**
     * Begins streaming of depth events.
     */
    private void startDepthEventStreaming(String symbol, int eventQueueIndex) {
        BinanceApiWebSocketClient client = new BinanceCustomWebSocketClientImpl(getSharedClient());
        client.onDepthEvent(symbol.toLowerCase(), response -> {
            eventQueueMap.get(eventQueueIndex).add(new Event(response));
        });
    }

    /**
     * Begins streaming of agg trades events.
     */
    private void startAggTradesEventStreaming(String symbol, int eventQueueIndex) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();
        client.onAggTradeEvent(symbol.toLowerCase(), response -> {
            eventQueueMap.get(eventQueueIndex).add(new Event(response));
        });
    }
}
