package ru.nsu.fit.g16201.galieva.Wireframe.Model;

public class Matrix {
    private double[][] elements;
    private int rowCount, colCount;

    public Matrix(double[][] elements) {
        this.elements = elements;
        this.rowCount = elements.length;
        this.colCount = elements.length > 0 ? elements[0].length : 0;
    }

    public Matrix(int colCount, int rowCount) {
        this.elements = new double[rowCount][colCount];
        this.rowCount = rowCount;
        this.colCount = colCount;
    }

    public Matrix(Matrix other) {
        this.rowCount = other.rowCount;
        this.colCount = other.colCount;
        this.elements = new double[rowCount][colCount];
        for (int x = 0; x < colCount; ++x) {
            for (int y = 0; y < rowCount; ++y) {
                setElement(x, y, other.getElement(x, y));
            }
        }
    }

    public static Matrix getE(int size) {
        Matrix res = new Matrix(size, size);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (i == j)
                    res.setElement(i, j, 1.0);
                else res.setElement(i, j, 0.0);
            }
        }
        return res;
    }

    public void setElement(int col, int row, double value) {
        if (row >= 0 && row < rowCount && col >= 0 && col < colCount)
            elements[row][col] = value;
    }
    public Double getElement(int col, int row) {
        if (row >= 0 && row < rowCount && col >= 0 && col < colCount)
            return elements[row][col];
        return null;
    }

    public Matrix multMatrix(Matrix other) {
        if (colCount == other.rowCount) {
            Matrix res = new Matrix(other.colCount, rowCount);
            for (int y = 0; y < rowCount; ++y) {
                for (int x = 0; x < other.colCount; ++x) {
                    double s = 0.0;
                    for (int z = 0; z < colCount; ++z) {
                        s += getElement(z, y)*other.getElement(x, z);
                    }
                    res.setElement(x, y, s);
                }
            }
            return res;
        }
        return null;
    }

    public Matrix multScalar(double scalar) {
        Matrix res = new Matrix(this);
        for (int x = 0; x < colCount; ++x) {
            for (int y = 0; y < rowCount; ++y) {
                res.setElement(x, y, scalar*res.getElement(x, y));
            }
        }
        return res;
    }

    public int getRowCount() {
        return rowCount;
    }
}
