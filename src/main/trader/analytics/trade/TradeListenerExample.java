package analytics.trade;

import com.binance.api.client.domain.market.AggTrade;
import logic.calc.Math;
import logic.listeners.TradeEventListener;
import logic.schedulers.ScheduleEvent;
import logic.schedulers.ScheduleManager;
import model.OrderBookCache;

import java.util.Map;

public class TradeListenerExample implements TradeEventListener {
    Map<Long, AggTrade> localTradeCache;
    private ScheduleManager scheduleManager;
    private int interval;

    /**
     * Simple Trade event listener that prints cumulative trade volume
     */
    public TradeListenerExample(int interval, ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
        this.interval = interval;
        this.scheduleManager.getEventManager().addEventListener(this);
        try {
            //we want risk manager to always have the latest price updates, so interval is 100ms(same as websocket interval)
            scheduleManager.periodicCallback(interval, "recenttrade");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void handleEvent(Map<Long, AggTrade> tradeCacheEvent) {
        this.localTradeCache = tradeCacheEvent;
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        String referenceTag = scheduleEvent.getReferenceTag();
        if (referenceTag == "recenttrade") {
            System.out.println("Latest Trade : " + this.localTradeCache.size());
        }
    }
}
