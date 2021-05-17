package logic;

import com.binance.api.client.domain.event.AggTradeEvent;

public interface ScheduledTradeEventListener {
    void handleScheduledTradeEvent(AggTradeEvent aggTradeEvent);
}
