package logic;

import java.util.HashMap;
import java.util.Map;

public class EventManagerFactory {
    private static final Map<String, EventManager> eventManagerInstances = new HashMap<>();
    public <T> EventManager<T> getEventManager(Class<T> eventType) {
        String event = eventType.getName();
        //ensures only one instance of a type of EventManager is created
        if(eventManagerInstances.containsKey(event)) {
            return eventManagerInstances.get(event);
        } else {
            EventManager<T> eventManager = new EventManager<T>();
            eventManagerInstances.put(event, eventManager);
            return eventManager;
        }
    }
}
