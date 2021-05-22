package logic.listeners;

import logic.schedulers.ScheduleEvent;
import logic.schedulers.ScheduleManager;
import model.OrderBookCache;
import org.quartz.Job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public abstract class OrderBookEventListener implements EventListener<OrderBookCache> {
    private ScheduleManager scheduleManager;
    private List<String> scheduledCallbackTags = new ArrayList<>();
    public OrderBookEventListener(ScheduleManager scheduleManager, int... intervals) {
        this.scheduleManager = scheduleManager;
        this.scheduleManager.getEventManager().addEventListener(this);
        try {
            int i = 0;
            //for loop for periodicCallback calls for each interval value provided
            for (int interval : intervals)
            {
                String scheduledCallbackTag = this.getClass().getName() + i;
                this.scheduledCallbackTags.add(scheduledCallbackTag);
                scheduleManager.periodicCallback(interval, scheduledCallbackTag);
                i++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public List<String> getScheduledCallbackTags() {
        return scheduledCallbackTags;
    }
}
