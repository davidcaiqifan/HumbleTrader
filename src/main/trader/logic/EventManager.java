package logic;

import com.binance.api.client.domain.event.AggTradeEvent;
import logic.listeners.OrderBookEventListener;
import logic.listeners.TradeEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private MarketDataManager marketDataManager;
    private ScheduleManager scheduleManager;
    private List<OrderBookEventListener> orderBookEventListeners = new ArrayList<>();
    private List<TradeEventListener> tradeEventlisteners = new ArrayList<>();

    public EventManager(MarketDataManager marketDataManager) {
        this.marketDataManager = marketDataManager;
        this.scheduleManager = new ScheduleManager();
    }


    /**
     * Add trade event listeners.
     */
    public void addTradeEventListener(TradeEventListener toAdd) {
        tradeEventlisteners.add(toAdd);
    }

    public void publishTradeEvent(ScheduleManager scheduleManager, AggTradeEvent tradeEvent) {
        // Notify everybody that may be interested.
//        for (TradeEventListener tl : tradeEventlisteners)
//            tl.handleTradeEvent();
        scheduleManager.updateTradeEvent(tradeEvent);
    }

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }

//    public static void main(String[] args) {
//        new EventManager(new MarketDataManager());
//    }

}
