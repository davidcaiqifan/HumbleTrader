package ta4jtest;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import logic.dataProcessors.BinanceGateway;
import logic.dataProcessors.MarketDataManager;
import logic.listeners.TradeEventListener;
import logic.schedulers.ScheduleEvent;
import logic.schedulers.ScheduleManager;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.BaseTradingRecord;
import org.ta4j.core.Indicator;
import org.ta4j.core.Strategy;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.volume.ChaikinMoneyFlowIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CMFI implements TradeEventListener{
    private Map<Long, AggTrade> aggTradesCache;
    private BinanceGateway binanceGateway;
    private ScheduleManager scheduleManager;
    private int duration;
    private BarSeries barSeries;
    private TimeSeriesCollection dataset = new TimeSeriesCollection();
    private int count = 0;
    private Strategy strategy;
    private TradingRecord tradingRecord;
    private TimeSeries timeSeries;
    private Indicator<Num> indicator;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     *
     * @param duration Duration of bar in seconds.
     * @param scheduleManager
     */
    public CMFI(int duration, BinanceGateway binanceGateway, ScheduleManager scheduleManager) {
        //Setting parameters
        this.binanceGateway = binanceGateway;
        this.scheduleManager = scheduleManager;
        this.duration = duration;

        System.out.println("********************** Initialization **********************");
        // Getting the bar series
        initMovingBarSeries(10);

        //initiate indicator
        this.indicator = new ChaikinMoneyFlowIndicator(this.barSeries, 10);
        timeSeries = buildChartBarSeries(this.barSeries
                , this.indicator, "Chaikin Money Flow");
        dataset.addSeries(timeSeries);
        timeSeries.setMaximumItemCount(10);
        // Building the trading strategy
        this.strategy = buildStrategy(barSeries);

        // Initializing the trading history
        this.tradingRecord = new BaseTradingRecord();

        // Initialize the trade cache
        initializeAggTradesCache(this.binanceGateway.getRecentTradeSnapshot());
        // Adding the listener
        this.scheduleManager.getEventManager().addEventListener(this);

        // Configuring the scheduled callback
        try {
            //we want risk manager to always have the latest price updates, so interval is 100ms(same as websocket interval)
            scheduleManager.periodicCallback(this.duration * 1000, "chaikin");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("************************************************************");
    }
    @Override
    public void handleEvent(AggTradeEvent aggTradeEvent) {
        updateTradeCache(aggTradeEvent);
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

    @Override
    public void handleScheduleEvent(ScheduleEvent scheduleEvent) {
        String referenceTag = scheduleEvent.getReferenceTag();
        if (referenceTag == "chaikin") {
            ZonedDateTime endTime = ZonedDateTime.now();
            Bar newBar = buildBar(this.barSeries, endTime);
            barSeries.addBar(newBar);
            System.out.println("------------------------------------------------------\n" + "Bar " + count
                    + " added, close price = " + newBar.getClosePrice().doubleValue());

            int endIndex = barSeries.getEndIndex();
            int indicatorIndex = (int)Math.floor(endIndex/10);
            System.out.println(indicator.getValue(indicatorIndex).doubleValue());
            RegularTimePeriod rtp
                    = RegularTimePeriod.createInstance(Second.class, Date.from(newBar.getEndTime().toInstant()) , TimeZone.getTimeZone("SST"));
            timeSeries.add(rtp, indicator.getValue(indicatorIndex).doubleValue());
            //System.out.println(indicator.getValue(endIndex).doubleValue());
            if (strategy.shouldEnter(endIndex)) {
                // Our strategy should enter
                System.out.println("Strategy should ENTER on " + endIndex);
                //System.out.println(indicator.getValue((int)Math.floor(endIndex/10)));
                boolean entered = tradingRecord.enter(endIndex, newBar.getClosePrice(), DecimalNum.valueOf(10));
                if (entered) {
                    Trade entry = tradingRecord.getLastEntry();
                    System.out.println("Entered on " + entry.getIndex() + " (price=" + entry.getNetPrice().doubleValue()
                            + ", amount=" + entry.getAmount().doubleValue() + ")");
                }
            } else if (strategy.shouldExit(endIndex)) {
                // Our strategy should exit
                System.out.println("Strategy should EXIT on " + endIndex);
                boolean exited = tradingRecord.exit(endIndex, newBar.getClosePrice(), DecimalNum.valueOf(10));
                if (exited) {
                    Trade exit = tradingRecord.getLastExit();
                    System.out.println("Exited on " + exit.getIndex() + " (price=" + exit.getNetPrice().doubleValue()
                            + ", amount=" + exit.getAmount().doubleValue() + ")");
                }
            } else {
                System.out.println("Neutral");
            }

            //initiate new bar trade cache
            //timeSeries.add(new Day(Date.from(bar.getEndTime().toInstant())), bar.getClosePrice().doubleValue());
            initializeAggTradesCache(this.binanceGateway.getRecentTradeSnapshot());
            this.count++;
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

    private static TimeSeries buildChartBarSeries(BarSeries barSeries, Indicator<Num> indicator,
                                                                      String name) {
        TimeSeries chartTimeSeries = new TimeSeries(name);
        for (int i = 0; i < barSeries.getBarCount(); i++) {
            Bar bar = barSeries.getBar(i);
            chartTimeSeries.add(new Day(Date.from(bar.getEndTime().toInstant())), indicator.getValue(i).doubleValue());
        }
        return chartTimeSeries;
    }

    public TimeSeriesCollection getDataset() {
        return dataset;
    }

    /**
     * Builds a moving bar series (i.e. keeping only the maxBarCount last bars)
     *
     * @param maxBarCount the number of bars to keep in the bar series (at maximum)
     * @return a moving bar series
     */
    private void initMovingBarSeries(int maxBarCount) {
        this.barSeries
                = new BaseBarSeriesBuilder().withName("CMFISeries").withNumTypeOf(DoubleNum.class).build();
        System.out.println("Initial bar count: " + barSeries.getBarCount());
        // Limitating the number of bars to maxBarCount
        barSeries.setMaximumBarCount(maxBarCount);
        // LAST_BAR_CLOSE_PRICE = barSeries.getBar(barSeries.getEndIndex()).getClosePrice();
        //wait till buffer full?
        //System.out.println(" (limited to " + maxBarCount + "), close price = " + LAST_BAR_CLOSE_PRICE);
    }

    /**
     * @param series a bar series
     * @return a Chaikin based strategy
     */
    private Strategy buildStrategy(BarSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }

        // Signals
        // Buy when Chaikin over 0.05
        // Sell Chaikin below 0.05
        Strategy buySellSignals = new BaseStrategy(new OverIndicatorRule(this.indicator, 0.05),
                new UnderIndicatorRule(this.indicator, -0.05));
        return buySellSignals;
    }


}
