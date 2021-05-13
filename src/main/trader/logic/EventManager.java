package logic;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.DepthEvent;
import customWebSockets.BinanceCustomWebSocketClientImpl;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;

import static com.binance.api.client.impl.BinanceApiServiceGenerator.getSharedClient;

public class EventManager {
    public HashMap<Integer, LinkedBlockingDeque<DepthEvent>> eventQueueMap;
    public EventManager() {
        this.eventQueueMap = new HashMap<>();
    }

    //initializes event stream based on input
    public int addEventStream(String eventType) {
        //write error manage code later
        if(eventType == "trade") {
            eventQueueMap.put(1, new LinkedBlockingDeque<DepthEvent>());
            startDepthEventStreaming("BTCUSDT", 1);
            return 1;
        } else {
            return 0;
        }
    }

    public LinkedBlockingDeque<DepthEvent> getEventQueue(int eventQueueIndex) {
        return eventQueueMap.get(eventQueueIndex);
    }

    /**
     * Begins streaming of depth events.
     */
    private void startDepthEventStreaming(String symbol, int eventQueueIndex) {
        BinanceApiWebSocketClient client = new BinanceCustomWebSocketClientImpl(getSharedClient());
        client.onDepthEvent(symbol.toLowerCase(), response -> {
            eventQueueMap.get(eventQueueIndex).add(response);
        });
    }
}
