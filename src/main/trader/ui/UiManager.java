package ui;

import Analytics.MovingAverageCrossover;
import Analytics.PriceChecker;
import Analytics.RiskWatcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import logic.EventManager;
import logic.dataProcessors.MarketDataManager;
import logic.schedulers.ScheduleManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class UiManager extends Application {
    final int WINDOW_SIZE = 100;
    private ScheduledExecutorService scheduledExecutorServiceSMA1;
    private ScheduledExecutorService scheduledExecutorServiceSMA2;
    private ScheduledExecutorService scheduledExecutorServicePrice;
    private ScheduledExecutorService scheduledExecutorServiceRisk;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        EventManager eventManager = new EventManager();
        ExecutorService executor1 = Executors.newSingleThreadExecutor();
        executor1.submit(() -> {
            MarketDataManager marketDataManager = new MarketDataManager("BTCUSDT", eventManager);
            marketDataManager.startOrderBookStreaming();
        });
        ScheduleManager scheduleManager;
        try {
            scheduleManager = new ScheduleManager(eventManager);

//            });

            primaryStage.setTitle("Analytics Graph");
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
            lineChart.setTitle("Analytics Graph");
            lineChart.setAnimated(false); // disable animations
            lineChart.autosize();

            //defining a series to display data
            XYChart.Series<String, Number> seriesSMA1 = new XYChart.Series<>();
            seriesSMA1.setName("SMA1");

            // add series to chart
            lineChart.getData().add(seriesSMA1);

            //do the same for sma2
            XYChart.Series<String, Number> seriesSMA2 = new XYChart.Series<>();
            seriesSMA2.setName("SMA2");
            lineChart.getData().add(seriesSMA2);

            //price
            XYChart.Series<String, Number> seriesPrice = new XYChart.Series<>();
            seriesPrice.setName("Price");
            lineChart.getData().add(seriesPrice);

            //risk threshold
            XYChart.Series<String, Number> seriesRisk = new XYChart.Series<>();
            seriesRisk.setName("Threshold");
            lineChart.getData().add(seriesRisk);

            // setup scene
            Scene scene = new Scene(lineChart, 800, 600);
            primaryStage.setScene(scene);

            // show the stage
            primaryStage.show();

            // this is used to display time in HH:mm:ss format
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

            ExecutorService executor3 = Executors.newSingleThreadExecutor();
            executor3.submit(() -> {
                MovingAverageCrossover movingAverageCrossover
                        = new MovingAverageCrossover(1000, 2000, 10, 2000, scheduleManager);
                // setup a scheduled executor to periodically put data into the chart
                scheduledExecutorServiceSMA1 = Executors.newSingleThreadScheduledExecutor();

                // put dummy data onto graph per second
                scheduledExecutorServiceSMA1.scheduleAtFixedRate(() -> {
                    Double price1 = movingAverageCrossover.getFirstAverage();
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
                }, 12, 1, TimeUnit.SECONDS);


                // setup a scheduled executor to periodically put data into the chart
                scheduledExecutorServiceSMA2 = Executors.newSingleThreadScheduledExecutor();

                // put dummy data onto graph per second

                scheduledExecutorServiceSMA2.scheduleAtFixedRate(() -> {
                    Double price2 = movingAverageCrossover.getSecondAverage();
                    //System.out.println(price);
                    // Update the chart
                    Platform.runLater(() -> {
                        // get current time
                        Date now = new Date();
                        // put random number with current time
                        seriesSMA2.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), price2));

                        if (seriesSMA2.getData().size() > WINDOW_SIZE)
                            seriesSMA2.getData().remove(0);
                    });
                }, 24, 2, TimeUnit.SECONDS);
            });

            ExecutorService executor2 = Executors.newSingleThreadExecutor();
            executor2.submit(() -> {
                PriceChecker priceChecker
                        = new PriceChecker(1000, scheduleManager);
                scheduledExecutorServicePrice = Executors.newSingleThreadScheduledExecutor();

                // put dummy data onto graph per second
                scheduledExecutorServicePrice.scheduleAtFixedRate(() -> {
                    Double price = priceChecker.getPrice();
                    // Update the chart
                    Platform.runLater(() -> {
                        // get current time
                        Date now = new Date();
                        // put random number with current time
                        seriesPrice.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), price));

                        if (seriesPrice.getData().size() > WINDOW_SIZE)
                            seriesPrice.getData().remove(0);
                    });
                }, 5, 1, TimeUnit.SECONDS);
            });

            ExecutorService executor4 = Executors.newSingleThreadExecutor();
            executor4.submit(() -> {
                RiskWatcher riskWatcher
                        = new RiskWatcher(40350, scheduleManager);
                scheduledExecutorServiceRisk = Executors.newSingleThreadScheduledExecutor();

                // put dummy data onto graph per second
                scheduledExecutorServiceRisk.scheduleAtFixedRate(() -> {
                    Double threshold = riskWatcher.getThreshold();
                    //System.out.println(price);
                    // Update the chart
                    Platform.runLater(() -> {
                        // get current time
                        Date now = new Date();
                        // put random number with current time
                        seriesRisk.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), threshold));

                        if (seriesRisk.getData().size() > WINDOW_SIZE)
                            seriesRisk.getData().remove(0);
                    });
                }, 5, 1, TimeUnit.SECONDS);
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        scheduledExecutorServiceSMA1.shutdownNow();
        scheduledExecutorServiceSMA2.shutdownNow();
        scheduledExecutorServicePrice.shutdownNow();
    }
}
