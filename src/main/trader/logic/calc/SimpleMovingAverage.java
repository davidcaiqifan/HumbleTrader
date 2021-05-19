package logic.calc;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SimpleMovingAverage {
    private DescriptiveStatistics descriptiveStatistics;
    public SimpleMovingAverage(int window) {
        this.descriptiveStatistics = new DescriptiveStatistics(window);
    }

    public void updateSamples(double sample) {
        this.descriptiveStatistics.addValue(sample);
    }

    public double getSimpleMovingAverage() {
        return this.descriptiveStatistics.getMean();
    }
}
