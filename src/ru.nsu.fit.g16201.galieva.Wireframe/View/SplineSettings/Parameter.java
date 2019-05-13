package ru.nsu.fit.g16201.galieva.Wireframe.View.SplineSettings;

public class Parameter {
    private double value, minValue, maxValue, step;

    public Parameter(double value, double minValue, double maxValue, double step) {
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
    }

    public double getValue() {
        return value;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getStep() {
        return step;
    }

}
