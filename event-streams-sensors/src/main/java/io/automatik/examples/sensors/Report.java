package io.automatik.examples.sensors;

public class Report {

    private double average;

    private double min;

    private double max;

    private boolean leakDetected;

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public boolean isLeakDetected() {
        return leakDetected;
    }

    public void setLeakDetected(boolean leakDetected) {
        this.leakDetected = leakDetected;
    }

    @Override
    public String toString() {
        return "Report [average=" + average + ", min=" + min + ", max=" + max + ", leakDetected=" + leakDetected + "]";
    }

}
