package model;

import com.binance.api.client.domain.event.DepthEvent;

public class Event {
    private DepthEvent depthEvent;
    public Event(DepthEvent event) {
    }
    public DepthEvent getDepthEvent() {
        return depthEvent;
    }

}
