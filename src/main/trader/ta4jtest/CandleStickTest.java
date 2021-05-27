package ta4jtest;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import logic.dataProcessors.BinanceGateway;
import logic.listeners.TradeEventListener;
import logic.schedulers.ScheduleEvent;
import logic.schedulers.ScheduleManager;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DoubleNum;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CandleStickTest implements TradeEventListener {
    private Map<Long, AggTrade> aggTradesCache;
    private BinanceGateway binanceGateway;
    private ScheduleManager scheduleManager;
    private int duration;
    private BarSeries barSeries;
    private Bar bar;
    private OHLCSeries ohlcSeries = new OHLCSeries("Candlesticks");

    private OHLCSeriesCollection ohlcCollection = new OHLCSeriesCollection();


    /**
     *
     * @param duration Duration of bar in seconds.
     * @param scheduleManager
     */
    public CandleStickTest(int duration, BinanceGateway binanceGateway, ScheduleManager scheduleManager) {
        this.binanceGateway = binanceGateway;
        this.scheduleManager = scheduleManager;
        this.duration = duration;
        this.barSeries = new BaseBarSeriesBuilder().withName("candleStickSeries").withNumTypeOf(DoubleNum.class).build();
        initializeAggTradesCache(this.binanceGateway.getRecentTradeSnapshot());
        this.scheduleManager.getEventManager().addEventListener(this);
        try {
            //we want risk manager to always have the latest price updates, so interval is 100ms(same as websocket interval)
            scheduleManager.periodicCallback(this.duration * 1000, "chaikin");
        } catch (Exception e) {
            System.out.println(e);
        }
        this.ohlcCollection.addSeries(this.ohlcSeries);
    }
    @Override
    public void handleEvent(AggTradeEvent aggTradeEvent) {
        updateTradeCache(aggTradeEvent);
    }



    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        String referenceTag = scheduleEvent.getReferenceTag();
        if (referenceTag == "chaikin") {
            ZonedDateTime endTime = ZonedDateTime.now();
            this.bar = buildBar(this.barSeries,endTime);
            barSeries.addBar(bar);
            RegularTimePeriod rtp
                    = RegularTimePeriod.createInstance(Second.class, Date.from(bar.getEndTime().toInstant()) , TimeZone.getTimeZone("SST"));
            this.ohlcSeries.add(rtp,bar.getOpenPrice().doubleValue(),
                    bar.getHighPrice().doubleValue(),
                    bar.getLowPrice().doubleValue(), bar.getClosePrice().doubleValue());
            initializeAggTradesCache(this.binanceGateway.getRecentTradeSnapshot());
        }
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

    private void updateTradeCache(AggTradeEvent aggTradeEvent) {
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

    @SuppressWarnings("deprecation")
    private Bar buildBar(BarSeries series, ZonedDateTime beginTime) {
        Duration barDuration = Duration.ofSeconds(this.duration);
        ZonedDateTime barEndTime = beginTime;
        barEndTime = barEndTime.plus(barDuration);
        Bar bar = new BaseBar(barDuration, barEndTime, series.function());
        for (Map.Entry<Long, AggTrade> tradeEntry : this.aggTradesCache.entrySet()) {
            double tradeVolume = Double.parseDouble(tradeEntry.getValue().getQuantity());
            double tradePrice = Double.parseDouble(tradeEntry.getValue().getPrice());
            bar.addTrade(tradeVolume,tradePrice,series.function());
        }
        return bar;
    }
    public BarSeries getBarSeries() {
        return barSeries;
    }

    public OHLCSeriesCollection getOhlcCollection() {
        return ohlcCollection;
    }
}
