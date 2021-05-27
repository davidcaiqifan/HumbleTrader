package logic;

import logic.listeners.EventListener;
import logic.schedulers.ScheduleEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventManager<U> {
    private List<EventListener<U>> eventListeners = new ArrayList<>();
    private ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Add event listeners.
     */
    public void addEventListener(EventListener<U> eventListener) {
        eventListeners.add(eventListener);
    }

    public void publishEvent(U event) {
        // Notify everybody that may be interested.
        for (EventListener<U> eventListener : this.eventListeners) {
            executor.submit(new Runnable() {
                public void run() {
                    eventListener.handleEvent(event);
                }
            });
        }
    }

    public void publishScheduleEvent(ScheduleEvent scheduleEvent) {
        for (EventListener<U> eventListener: this.eventListeners) {
            executor.submit(new Runnable() {
                public void run() {
                    eventListener.handleScheduleEvent(scheduleEvent);
                }
            });
        }
    }
}
