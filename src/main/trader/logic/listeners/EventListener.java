package logic.listeners;

import logic.schedulers.ScheduleEvent;
import model.OrderBookCache;

public interface EventListener<T> {
    void handleEvent(T event);
    void handleScheduleEvent(ScheduleEvent scheduleEvent);
}
