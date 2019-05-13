package ru.nsu.fit.g16201.galieva.Wireframe.View.SplineSettings;

import java.awt.*;

public class SplineViewParameters {
    private Color splineColor = Color.black;
    private double leftBorder, rightBorder;

    public Color getSplineColor() {
        return splineColor;
    }

    public void setSplineColor(Color splineColor) {
        this.splineColor = splineColor;
    }

    public double getLeftBorder() {
        return leftBorder;
    }

    public void setLeftBorder(double leftBorder) {
        this.leftBorder = leftBorder;
    }

    public double getRightBorder() {
        return rightBorder;
    }

    public void setRightBorder(double rightBorder) {
        this.rightBorder = rightBorder;
    }
}
