import logic.schedulers.ScheduleManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;
import platform.AnalyticsBuilder;
import ta4jtest.CMFI;
import ta4jtest.CandleStickTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        AnalyticsBuilder analyticsBuilder = new AnalyticsBuilder("BTCUSDT").withOrderBook().withAggsTrade();
        analyticsBuilder.initialize();
        // setup a scheduled executor thread pool to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        //BasicConfigurator.configure();
        ScheduleManager scheduleManager = analyticsBuilder.getOrderBookScheduleManager();
        ScheduleManager tradeScheduleManager = analyticsBuilder.getTradeScheduleManager();
        CMFI CMFI
                = new CMFI(1, analyticsBuilder.getBinanceGateway(), tradeScheduleManager);
        CandleStickTest candleStickTest
                = new CandleStickTest(5
                , analyticsBuilder.getBinanceGateway(), tradeScheduleManager);
        /*
         * Getting bar series
         */
        /*
         * Building chart dataset
         */
        //TimeSeriesCollection dataset = CMFI.getDataset();
        OHLCDataset dataset1 = candleStickTest.getOhlcCollection();
        //dataset.addSeries(buildChartBarSeries(series, CMFI.getIndicator(), "BTCUSDT"));

        /*
         * Creating the chart
         */
//        JFreeChart chart = ChartFactory.createTimeSeriesChart("BTCUSDT", // title
//                "Time of day", // x-axis label
//                "Price Per Unit", // y-axis label
//                dataset, // data
//                true, // create legend?
//                true, // generate tooltips?
//                false // generate URLs?
//        );
        JFreeChart chart = ChartFactory
                .createCandlestickChart("FX Trader Prototype", "Time"
                        , "Value", dataset1, true);
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

        /*
         * Displaying the chart
         */
        displayChart(chart);

        /*
         * Creating the chart
         */
        JFreeChart chart1 = ChartFactory.createTimeSeriesChart("Bitcoin Chaikin Indicator", // title
                "Time", // x-axis label
                "Indicator Value", // y-axis label
                CMFI.getDataset(), // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );
        XYPlot plot1 = (XYPlot) chart.getPlot();
        DateAxis axis1 = (DateAxis) plot1.getDomainAxis();
        axis1.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

        /*
         * Displaying the chart
         */
        displayChart(chart1);
    }



    /**
     * Displays a chart in a frame.
     *
     * @param chart the chart to be displayed
     */
    private static void displayChart(JFreeChart chart) {
        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        // Application frame
        ApplicationFrame frame = new ApplicationFrame("Candlestick Chart");
        frame.setContentPane(panel);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }

//    public OhlcChart update(Timeseries<Double> ts)
//    {
//        Stroke myStroke = new BasicStroke((float) 1.0);
//        XYLineAndShapeRenderer timeSeriesRenderer = new XYLineAndShapeRenderer();
//        timeSeriesRenderer.setBaseShapesVisible(false);
//        timeSeriesRenderer.setSeriesPaint(0, Color.blue);
//        timeSeriesRenderer.setSeriesStroke(0, myStroke);
//
//        UiTimeseries series = new UiTimeseries(ts);
//        dataTrend.addSeries(series);
//        plot.setDataset(plot.getDatasetCount()+1, dataTrend);
//        plot.setRenderer(plot.getDatasetCount()+1, timeSeriesRenderer);
//
//        return this;
//    }
}
