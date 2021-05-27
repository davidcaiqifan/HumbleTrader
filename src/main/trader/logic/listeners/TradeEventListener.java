package logic.listeners;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import model.AggsTradeCache;

import java.util.Map;

public interface TradeEventListener extends EventListener<AggTradeEvent> {
}
