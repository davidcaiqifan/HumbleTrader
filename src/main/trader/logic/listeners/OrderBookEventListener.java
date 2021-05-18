package logic.listeners;

import org.quartz.Job;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

public interface OrderBookEventListener {
    void handleOrderBookEvent(Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache);
}
