package model;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;

public class Event {
    private DepthEvent depthEvent;
    private AggTradeEvent tradeEvent;
    public Event(DepthEvent depthEvent) {
        this.depthEvent = depthEvent;
    }

    public Event(AggTradeEvent tradeEvent) {
        this.tradeEvent = tradeEvent;
    }
    public DepthEvent getDepthEvent() {
        return depthEvent;
    }

    public AggTradeEvent getTradeEvent() {
        return tradeEvent;
    }
}
