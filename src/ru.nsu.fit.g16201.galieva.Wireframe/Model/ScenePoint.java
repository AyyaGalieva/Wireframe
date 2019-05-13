package ru.nsu.fit.g16201.galieva.Wireframe.Model;

public class ScenePoint {
    private double x, y, z;
    private double w = 1.0;

    public ScenePoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ScenePoint(Matrix matrix) {
        this.x = matrix.getElement(0, 0);
        this.y = matrix.getElement(0, 1);
        this.z = matrix.getElement(0, 2);
        if (matrix.getRowCount() == 4) {
            this.w = matrix.getElement(0, 3);
        }
    }

    public ScenePoint sumPoint(ScenePoint p) {
        return new ScenePoint(x+p.x, y+p.y, z+p.z);
    }

    public ScenePoint subPoint(ScenePoint p) {
        return new ScenePoint(x-p.x, y-p.y, z-p.z);
    }

    public ScenePoint multScalar(double scalar) {
        return new ScenePoint(x*scalar, y*scalar, z*scalar);
    }

    public Matrix getMatrix() {
        return new Matrix(new double[][]{{x}, {y}, {z}, {w}});
    }

    public void copy(ScenePoint p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
        this.w = p.w;
    }

    public double getW() {
        return w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
