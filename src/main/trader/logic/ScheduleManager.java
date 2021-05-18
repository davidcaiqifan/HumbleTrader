package logic;

import logic.listeners.ScheduledListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleManager {
    private Map<Integer, List<ScheduledListener>> scheduleListeners = new HashMap<>();
    private ScheduledExecutorService executor;

    public ScheduleManager() {
        executor = Executors.newScheduledThreadPool(4);
    }

    public void periodicCallback(int interval, ScheduledListener scheduledListener) {
        if (scheduleListeners.containsKey(interval)) {
            scheduleListeners.get(interval).add(scheduledListener);
        } else {
            scheduleListeners.put(interval, new ArrayList<>());
            scheduleListeners.get(interval).add(scheduledListener);
        }
        executor.scheduleAtFixedRate(() -> {
            for (ScheduledListener tl : scheduleListeners.get(interval))
                tl.handleScheduledEvent();
        }, 200, interval, TimeUnit.MILLISECONDS);
    }
}
