package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import logic.DataGatewayManager;
import logic.EventManager;
import logic.stats.OrderBookManager;
import logic.stats.ScheduledPriceUpdate;
import logic.stats.TradeManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class UiManager extends Application {
    final int WINDOW_SIZE = 100;
    private ScheduledExecutorService scheduledExecutorServiceBid;
    private ScheduledExecutorService scheduledExecutorServiceAsk;
    private TradeManager tradeGenerator;
    private ScheduledPriceUpdate priceGenerator;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("JavaFX Realtime Chart Demo");
        EventManager eventManager = new EventManager(new DataGatewayManager());
        this.priceGenerator = new ScheduledPriceUpdate();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                eventManager.getScheduleManager().periodicCallback(100, priceGenerator);
            }
        });
        t1.start();
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
        lineChart.setTitle("Realtime Price Chart");
        lineChart.setAnimated(false); // disable animations
        lineChart.autosize();

        //defining a series to display data
        XYChart.Series<String, Number> seriesBid = new XYChart.Series<>();
        seriesBid.setName("Trade price");

        // add series to chart
        lineChart.getData().add(seriesBid);

        //do the same for ask
//        XYChart.Series<String, Number> seriesAsk = new XYChart.Series<>();
//        seriesBid.setName("Data Series Ask");
//        lineChart.getData().add(seriesAsk);

        // setup scene
        Scene scene = new Scene(lineChart, 800, 600);
        primaryStage.setScene(scene);

        // show the stage
        primaryStage.show();

        // this is used to display time in HH:mm:ss format
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorServiceBid = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorServiceBid.scheduleAtFixedRate(() -> {
            Double price = priceGenerator.getPrice();
            System.out.println(price);
            // Update the chart
            Platform.runLater(() -> {
                // get current time
                Date now = new Date();
                // put random number with current time
                seriesBid.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), price));

                if (seriesBid.getData().size() > WINDOW_SIZE)
                    seriesBid.getData().remove(0);
            });
        }, 0, 1, TimeUnit.SECONDS);

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        scheduledExecutorServiceBid.shutdownNow();
    }
}
