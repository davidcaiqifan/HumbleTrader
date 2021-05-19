package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import logic.EventManager;
import logic.ScheduleManager;
import logic.dataProcessors.MarketDataManager;
import logic.listeners.ScheduledPriceManager;
import logic.dataProcessors.TradeManager;
import logic.listeners.SimpleMovingAverage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UiManager extends Application {
    final int WINDOW_SIZE = 100;
    private ScheduledExecutorService scheduledExecutorServiceSMA1;
    private ScheduledExecutorService scheduledExecutorServiceSMA2;
    private TradeManager tradeGenerator;
    private ScheduledPriceManager priceGenerator;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Weighted Average Prices");
        ScheduleManager sched;
        EventManager eventManager = new EventManager(new MarketDataManager("DOGEUSDT"));
        SimpleMovingAverage sma1 = new SimpleMovingAverage(1000);
        SimpleMovingAverage sma2 = new SimpleMovingAverage(10000);
        try {
            sched = new ScheduleManager(eventManager);
            sched.periodicCallback(100, sma1);
            sched.periodicCallback(100, sma2);
        } catch (Exception e) {
            System.out.println(e);
        }
        //EventManager eventManager = new EventManager(new MarketDataManager());
        this.priceGenerator = new ScheduledPriceManager();
//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                eventManager.getScheduleManager().periodicCallback(100, priceGenerator);
//            }
//        });
        //t1.start();
        //priceGenerator = new OrderBookManager(eventManager);

        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis(); // we are gonna plot against time
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Price");
        yAxis.setAnimated(false); // axis animations are removed
        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);

        //creating the line chart with two axis created above
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("SMA1");
        lineChart.setAnimated(false); // disable animations
        lineChart.autosize();

        //defining a series to display data
        XYChart.Series<String, Number> seriesSMA1 = new XYChart.Series<>();
        seriesSMA1.setName("SMA2 ");

        // add series to chart
        lineChart.getData().add(seriesSMA1);

        //do the same for sma2
        XYChart.Series<String, Number> seriesSMA2 = new XYChart.Series<>();
        seriesSMA2.setName("SMA2");
        lineChart.getData().add(seriesSMA2);

        // setup scene
        Scene scene = new Scene(lineChart, 800, 600);
        primaryStage.setScene(scene);

        // show the stage
        primaryStage.show();

        // this is used to display time in HH:mm:ss format
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorServiceSMA1 = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorServiceSMA1.scheduleAtFixedRate(() -> {
            Double price1 = sma1.getSma();
            //System.out.println(price);
            // Update the chart
            Platform.runLater(() -> {
                // get current time
                Date now = new Date();
                // put random number with current time
                seriesSMA1.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), price1));

                if (seriesSMA1.getData().size() > WINDOW_SIZE)
                    seriesSMA1.getData().remove(0);
            });
        }, 0, 1, TimeUnit.SECONDS);


        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorServiceSMA2 = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorServiceSMA2.scheduleAtFixedRate(() -> {
            Double price1 = sma2.getSma();
            //System.out.println(price);
            // Update the chart
            Platform.runLater(() -> {
                // get current time
                Date now = new Date();
                // put random number with current time
                seriesSMA2.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), price1));

                if (seriesSMA2.getData().size() > WINDOW_SIZE)
                    seriesSMA2.getData().remove(0);
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        scheduledExecutorServiceSMA1.shutdownNow();
        scheduledExecutorServiceSMA2.shutdownNow();
    }
}
