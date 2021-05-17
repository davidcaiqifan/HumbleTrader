package logic.listeners;

import com.binance.api.client.domain.event.AggTradeEvent;

public interface ScheduledTradeEventListener {
    void handleScheduledTradeEvent(AggTradeEvent aggTradeEvent);
}
