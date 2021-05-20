package logic.calc;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SimpleMovingAverage {
    private DescriptiveStatistics descriptiveStatistics;
    private int window;
    public SimpleMovingAverage(int window) {
        this.window = window;
        this.descriptiveStatistics = new DescriptiveStatistics(window);
    }

    /**
     * Adds new sample to descriptiveStatistics.
     */
    public void updateSamples(double sample) {
        this.descriptiveStatistics.addValue(sample);
    }

    /**
     * Returns -1 if data is still being buffered, otherwise return average.
     */
    public double getSimpleMovingAverage() {
        if(this.descriptiveStatistics.getN() < this.window) {
            return -1;
        } else {
            return this.descriptiveStatistics.getMean();
        }
    }
}
