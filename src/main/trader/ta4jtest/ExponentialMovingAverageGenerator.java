package ta4jtest;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import logic.EventManager;
import logic.calc.Math;
import logic.calc.SimpleMovingAverage;
import logic.dataProcessors.BinanceGateway;
import logic.listeners.EventListener;
import logic.listeners.OrderBookEventListener;
import logic.listeners.TradeEventListener;
import logic.schedulers.ScheduleEvent;
import logic.schedulers.ScheduleManager;
import model.AggsTradeCache;
import model.OrderBookCache;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DoubleNum;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExponentialMovingAverageGenerator implements TradeEventListener {
    private Map<Long, AggTrade> aggTradesCache;
    private BinanceGateway binanceGateway;
    private int duration;
    private Bar bar;

    /**
     *
     * @param period1
     * @param duration Duration of bar in seconds.
     * @param scheduleManager
     */
    public ExponentialMovingAverageGenerator(int period1, int duration, BinanceGateway binanceGateway, ScheduleManager scheduleManager) {
        this.binanceGateway = binanceGateway;
        this.duration = duration;
        BarSeries series = new BaseBarSeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum.class).build();
        ZonedDateTime endTime = ZonedDateTime.now();
        series.addBar(endTime, 105.42, 112.99, 104.01, 111.42, 1337);
    }
    @Override
    public void handleEvent(AggTradeEvent aggTradeEvent) {
        Long aggregatedTradeId = aggTradeEvent.getAggregatedTradeId();
        AggTrade updateAggTrade = aggTradesCache.get(aggregatedTradeId);
        if (updateAggTrade == null) {
            // new agg trade
            updateAggTrade = new AggTrade();
        }
        updateAggTrade.setAggregatedTradeId(aggregatedTradeId);
        updateAggTrade.setPrice(aggTradeEvent.getPrice());
        updateAggTrade.setQuantity(aggTradeEvent.getQuantity());
        updateAggTrade.setFirstBreakdownTradeId(aggTradeEvent.getFirstBreakdownTradeId());
        updateAggTrade.setLastBreakdownTradeId(aggTradeEvent.getLastBreakdownTradeId());
        updateAggTrade.setBuyerMaker(aggTradeEvent.isBuyerMaker());

        // Store the updated agg trade in the cache
        aggTradesCache.put(aggregatedTradeId, updateAggTrade);
    }

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        //this.bar = buildBar();
        initializeAggTradesCache(this.binanceGateway.getRecentTradeSnapshot());
    }

    /**
     * Initializes the aggTrades cache by using the REST API.
     */
    private void initializeAggTradesCache(List<AggTrade> aggTrades) {
        this.aggTradesCache = new HashMap<>();
        for (AggTrade aggTrade : aggTrades) {
            aggTradesCache.put(aggTrade.getAggregatedTradeId(), aggTrade);
        }
    }

    @SuppressWarnings("deprecation")
    private Bar buildBar(BarSeries series, ZonedDateTime beginTime, int duration) {
        Duration barDuration = Duration.ofSeconds(duration);
        ZonedDateTime barEndTime = beginTime;
        barEndTime = barEndTime.plus(barDuration);
        Bar bar = new BaseBar(barDuration, barEndTime, series.function());
        for (Map.Entry<Long, AggTrade> tradeEntry : this.aggTradesCache.entrySet()) {
            double tradePrice = Double.parseDouble(tradeEntry.getValue().getPrice());
            double tradeVolume = Double.parseDouble(tradeEntry.getValue().getQuantity());
            bar.addTrade(tradePrice,tradeVolume,series.function());
        }
        return bar;
    }

}
