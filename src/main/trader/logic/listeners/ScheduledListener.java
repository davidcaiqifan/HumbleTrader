package logic.listeners;

import com.binance.api.client.domain.event.AggTradeEvent;

public interface ScheduledListener {
    void handleScheduledEvent();
}
