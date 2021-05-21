package logic.listeners;

import logic.schedulers.ScheduleEvent;
import model.OrderBookCache;
import org.quartz.Job;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

public interface OrderBookEventListener extends EventListener<OrderBookCache> {
}
