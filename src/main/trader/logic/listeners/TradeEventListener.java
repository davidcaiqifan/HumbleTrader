package logic.listeners;

import com.binance.api.client.domain.market.AggTrade;

import java.util.Map;

public interface TradeEventListener extends EventListener<Map<Long, AggTrade>> {
}
