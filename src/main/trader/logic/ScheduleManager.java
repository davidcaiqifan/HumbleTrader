package logic;

import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.event.AggTradeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleManager {
    private AggTradeEvent lastTradeEvent;
    private Map<Integer, List<ScheduledTradeEventListener>> scheduleListeners = new HashMap<>();
    private ScheduledExecutorService executor;

    public ScheduleManager() {
        executor = Executors.newScheduledThreadPool(4);
    }

    public void updateTradeEvent(AggTradeEvent tradeEvent) {
        this.lastTradeEvent = tradeEvent;
    }

    public void periodicCallback(int interval, ScheduledTradeEventListener scheduledTradeEventListener) {
        if (scheduleListeners.containsKey(interval)) {
            scheduleListeners.get(interval).add(scheduledTradeEventListener);
        } else {
            scheduleListeners.put(interval, new ArrayList<>());
            scheduleListeners.get(interval).add(scheduledTradeEventListener);
        }
        executor.scheduleAtFixedRate(() -> {
            for (ScheduledTradeEventListener tl : scheduleListeners.get(interval))
                tl.handleScheduledTradeEvent(this.lastTradeEvent);
        }, 0, interval, TimeUnit.MILLISECONDS);
    }
}
