package logic.eventproducers;

import model.Event;

import java.util.Map;
import java.util.Queue;

public abstract class EventProducer implements Producer{
    protected Map eventMap;
    protected String eventType;
    public EventProducer(String eventType, Map eventMap) {
        this.eventType = eventType;
        this.eventMap = eventMap;
    }
}
