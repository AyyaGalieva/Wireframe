package ru.nsu.fit.g16201.galieva.Wireframe.Model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Spline {
    private static final Matrix matrix = new Matrix(new double[][]{
        {-1.0/6, 1.0/2, -1.0/2, 1.0/6},
        {1.0/2, -1.0, 1.0/2, 0.0},
        {-1.0/2, 0.0, 1.0/2, 0.0},
        {1.0/6, 2.0/3, 1.0/6, 0.0}});
    private Matrix rotationMatrix = Matrix.getE(4);
    private List<Point2D.Double> points;
    private double splineLength;
    private List<Double> segmentLengths;
    private Color color = new Color(0, 255, 0);

    public double x = 0.0;
    public double y = 0.0;
    public double z = 0.0;
    public double rx = 0.0;
    public double ry = 0.0;
    public double rz = 0.0;

    public Spline() {
        points = new ArrayList<>();
        splineLength = 0.0;
        segmentLengths = new ArrayList<>();
    }

    public void addPoint(Point2D.Double p) {
        points.add(p);
        calcLength();
    }

    public void removePoint(int pointPos) {
        points.remove(pointPos);
        calcLength();
    }

    public void movePoint(int pointPos, Point2D.Double newP) {
        points.set(pointPos, newP);
        calcLength();
    }

    private void calcLength() {
        splineLength = 0.0;
        segmentLengths = new ArrayList<>();
        for (int pointPos = 1; pointPos < points.size()-2; ++pointPos) {
            Point2D.Double prevPoint = null;
            double length = 0.0;
            for (double t = 0; t <= 1.0; t+=0.01) {
                Point2D.Double curPoint = getSplinePoint(pointPos, t);
                if (prevPoint!=null) {
                    length += Math.sqrt((curPoint.x-prevPoint.x)*(curPoint.x-prevPoint.x)+(curPoint.y-prevPoint.y)*(curPoint.y-prevPoint.y));
                }
                prevPoint = curPoint;
            }
            segmentLengths.add(length);
            splineLength += length;
        }
    }

    public Point2D.Double getNormSplinePoint(double t) {
        double length = splineLength*t;
        double prevSegment = 0.0;
        for (int i = 0; i < segmentLengths.size(); ++i) {
            double curSegment = prevSegment + segmentLengths.get(i);
            if (length <= curSegment)
                return getSplinePoint(i+1, (length-prevSegment)/(curSegment-prevSegment));
            prevSegment = curSegment;
        }
        return getSplinePoint(points.size()-3, 1.0);
    }

    private Point2D.Double getSplinePoint(int pointPos, double t) {
        Matrix T = new Matrix(new double[][]{{t*t*t, t*t, t, 1}});
        Matrix Gx = new Matrix(new double[][]{
                {points.get(pointPos-1).x},
                {points.get(pointPos).x},
                {points.get(pointPos+1).x},
                {points.get(pointPos+2).x}});
        Matrix Gy = new Matrix(new double[][]{
                {points.get(pointPos-1).y},
                {points.get(pointPos).y},
                {points.get(pointPos+1).y},
                {points.get(pointPos+2).y}});
        return new Point2D.Double(T.multMatrix(matrix).multMatrix(Gx).getElement(0,0), T.multMatrix(matrix).multMatrix(Gy).getElement(0,0));
    }

    public boolean canDrawSpline() {
        return points.size() >= 4;
    }

    public List<Point2D.Double> getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Matrix getRotationMatrix() {
        return rotationMatrix;
    }

    public void setRotationMatrix(Matrix rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }
}
