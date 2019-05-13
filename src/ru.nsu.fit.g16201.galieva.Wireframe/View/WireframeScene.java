package ru.nsu.fit.g16201.galieva.Wireframe.View;

import ru.nsu.fit.g16201.galieva.Wireframe.Model.*;
import ru.nsu.fit.g16201.galieva.Wireframe.View.SplineSettings.SettingsWindow;
import ru.nsu.fit.g16201.galieva.Wireframe.View.SplineSettings.SplineViewParameters;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class WireframeScene extends JPanel {
    private BufferedImage image;
    private static Color backgroundColor = Color.black;
    private int panelSizeX = 0, panelSizeY = 0;
    public ScenePoint camera = new ScenePoint(-10.0, 0.0, 0.0);

    private SettingsWindow settings;
    private SplineViewParameters splineViewParameters;

    private Matrix matrix;

    private Matrix perspectiveMatrix = new Matrix(new double[][]{
            {1.0, 0.0, 0.0, 0.0},
            {0.0, 1.0, 0.0, 0.0},
            {0.0, 0.0, 1.0, 0.0},
            {1.0/20, 0.0, 0.0, 0.0}
    });

    public Matrix toCameraCoordinates = new Matrix(new double[][]{
            {1.0, 0.0, 0.0, -camera.getX()},
            {0.0, 1.0, 0.0, -camera.getY()},
            {0.0, 0.0, -1.0, -camera.getZ()},
            {0.0, 0.0, 0.0, 1.0}
    });

    private Matrix rotationMatrix = Matrix.getE(4);

    private double scale = 100;
    private static final int frameOffset = 10;

    public WireframeScene(SettingsWindow settingsWindow) {
        this.settings = settingsWindow;
        this.splineViewParameters = settings.getSplineViewParameters();
        setMinimumSize(new Dimension(700, 500));
        backgroundColor = settings.getSceneColor();

        MouseAdapter mouseAdapter = new MouseAdapter() {
            private int endX = 0, endY = 0;

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                endX = e.getX();
                endY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                double angleX = -360.0*((double)(e.getX()-endX)/getWidth());
                double angleY = -360.0*((double)(e.getY()-endY)/getHeight());
                endX = e.getX();
                endY = e.getY();

                Matrix rotationMatrixX = getRotationMatrixZ(angleX);
                Matrix rotationMatrixY = getRotationMatrixY(angleY);
                rotationMatrix = rotationMatrixX.multMatrix(rotationMatrixY.multMatrix(rotationMatrix));
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                if (mouseWheelEvent.getWheelRotation() > 0){
                    if (settings.getZf()+0.5 <= 40.0001 && settings.getZn()+0.5 <= 40.0001) {
                        settings.setZnZf(settings.getZn()+0.5, settings.getZf()+0.5);
                    }
                }
                else {
                    double newZf = Math.max(0.0, settings.getZf()-0.5);
                    if (newZf >= -0.0001 && settings.getZn()+newZf-settings.getZf() >= -0.0001) {
                        settings.setZnZf(settings.getZn() - settings.getZf() + newZf, newZf);
                    }
                }
                repaint();
            }
        };
        addMouseMotionListener(mouseAdapter);
        addMouseListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    private void rotateX(double angle) {
        matrix = matrix.multMatrix(getRotationMatrixX(angle));
    }

    private Matrix getRotationMatrixX(double angle) {
        double angleRad = angle*Math.PI/180.0;
        return new Matrix(new double[][]{
                {1.0, 0.0, 0.0, 0.0},
                {0.0, Math.cos(angleRad), -Math.sin(angleRad), 0.0},
                {0.0, Math.sin(angleRad), Math.cos(angleRad), 0.0},
                {0.0, 0.0, 0.0, 1.0}
        });
    }

    private void rotateY(double angle) {
        matrix = matrix.multMatrix(getRotationMatrixY(angle));
    }

    private Matrix getRotationMatrixY(double angle) {
        double angleRad = angle*Math.PI/180.0;
        return new Matrix(new double[][]{
            {Math.cos(angleRad), 0.0, Math.sin(angleRad), 0.0},
            {0.0, 1.0, 0.0, 0.0},
            {-Math.sin(angleRad), 0.0, Math.cos(angleRad), 0.0},
            {0.0, 0.0, 0.0, 1.0}
        });
    }

    private void rotateZ(double angle) {
        matrix = matrix.multMatrix(getRotationMatrixZ(angle));
    }

    private Matrix getRotationMatrixZ(double angle) {
        double angleRad = angle*Math.PI/180.0;
        return new Matrix(new double[][]{
                {Math.cos(angleRad), -Math.sin(angleRad), 0.0, 0.0},
                {Math.sin(angleRad), Math.cos(angleRad), 0.0, 0.0},
                {0.0, 0.0, 1.0, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        });
    }

    private boolean clip(ScenePoint point1, ScenePoint point2) {
        ScenePoint p1 = new ScenePoint(point1.getX(), point1.getY(), point1.getZ());
        ScenePoint p2 = new ScenePoint(point2.getX(), point2.getY(), point2.getZ());

        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        double deltaZ = p2.getZ() - p1.getZ();

        if ((p1.getX()<0.0 && p2.getX()<0.0) || (p1.getX()>1.0 && p2.getX()>1.0))
            return false;
        else if ((p1.getX()<0.0 || p1.getX()>1.0 || p2.getX()<0.0 || p2.getX()>1.0) && (Math.abs(deltaX)>0.0001)) {
            if (p1.getX() >= p2.getX()) {
                ScenePoint tmp = p1;
                p1 = p2;
                p2 = tmp;
            }
            if (p1.getX() < 0.0)
                p1 = p1.sumPoint(p2.subPoint(p1).multScalar((-p1.getX())/deltaX));
            if (p2.getX() > 1.0)
                p2 = p2.sumPoint(p1.subPoint(p2).multScalar((p2.getX() - 1.0)/deltaX));
        }

        if ((p1.getY()<-1.0 && p2.getY()<-1.0) || (p1.getY()>1.0 && p2.getY()>1.0))
            return false;
        else if ((p1.getY()<-1.0 || p1.getY()>1.0 || p2.getY()<-1.0 || p2.getY()>1.0) && (Math.abs(deltaY)>0.0001)) {
            if (p1.getY() >= p2.getY()) {
                ScenePoint tmp = p1;
                p1 = p2;
                p2 = tmp;
            }
            if (p1.getY() < -1.0)
                p1 = p1.sumPoint(p2.subPoint(p1).multScalar((-1.0 - p1.getY())/deltaY));
            if (p2.getY() > 1.0)
                p2 = p2.sumPoint(p1.subPoint(p2).multScalar((p2.getY() - 1.0)/deltaY));
        }

        if ((p1.getZ()<-1.0 && p2.getZ()<-1.0) || (p1.getZ()>1.0 && p2.getZ()>1.0))
            return false;
        else if ((p1.getZ()<-1.0 || p1.getZ()>1.0 || p2.getZ()<-1.0 || p2.getZ()>1.0) && (Math.abs(deltaZ)>0.0001)) {
            if (p1.getZ() >= p2.getZ()) {
                ScenePoint tmp = p1;
                p1 = p2;
                p2 = tmp;
            }
            if (p1.getZ() < -1.0)
                p1 = p1.sumPoint(p2.subPoint(p1).multScalar((-1.0 - p1.getZ())/deltaZ));
            if (p2.getZ() > 1.0)
                p2 = p2.sumPoint(p1.subPoint(p2).multScalar((p2.getZ() - 1.0)/deltaZ));
        }

        point1.copy(p1);
        point2.copy(p2);
        return true;
    }

    private void drawLine(Graphics2D g2d, ScenePoint p1, ScenePoint p2){
        p1 = new ScenePoint(perspectiveMatrix.multMatrix(toCameraCoordinates).multMatrix(rotationMatrix).multMatrix(matrix).multMatrix(p1.getMatrix()));
        p1 = new ScenePoint(p1.getMatrix().multScalar(1.0/Math.abs(p1.getW())));

        p2 = new ScenePoint(perspectiveMatrix.multMatrix(toCameraCoordinates).multMatrix(rotationMatrix).multMatrix(matrix).multMatrix(p2.getMatrix()));
        p2 = new ScenePoint(p2.getMatrix().multScalar(1.0/Math.abs(p2.getW())));

        if (clip(p1, p2)) {
            double sw = settings.getSw();
            double sh = settings.getSh();
            g2d.drawLine((int)(p1.getY()*sw/2 + getWidth()/2), (int)(p1.getZ()*sh/2 + getHeight()/2),
                    (int)(p2.getY()*sw/2 + getWidth()/2), (int)(p2.getZ()*sh/2 + getHeight()/2));
        }
    }

    private void drawAxis(Graphics2D g2d) {
        g2d.setColor(Color.blue);
        drawLine(g2d, new ScenePoint(0.0, 0.0, 0.0), new ScenePoint(0.0, 0.0, 3.0));
        g2d.setColor(Color.green);
        drawLine(g2d, new ScenePoint(0.0, 0.0, 0.0), new ScenePoint(0.0, 3.0, 0.0));
        g2d.setColor(Color.red);
        drawLine(g2d, new ScenePoint(0.0, 0.0, 0.0), new ScenePoint(3.0, 0.0, 0.0));
    }

    private void drawFrame(Graphics2D g2d) {
        g2d.setColor(Color.gray);
        int frameWidth = (int)settings.getSw();
        int frameHeight = (int)(frameWidth*settings.getSh()/settings.getSw());
        g2d.drawLine(getWidth()/2-frameWidth/2-frameOffset, getHeight()/2-frameHeight/2-frameOffset, getWidth()/2+frameWidth/2+frameOffset, getHeight()/2-frameHeight/2-frameOffset);
        g2d.drawLine(getWidth()/2-frameWidth/2-frameOffset, getHeight()/2-frameHeight/2-frameOffset, getWidth()/2-frameWidth/2-frameOffset, getHeight()/2+frameHeight/2+frameOffset);
        g2d.drawLine(getWidth()/2+frameWidth/2+frameOffset, getHeight()/2+frameHeight/2+frameOffset, getWidth()/2-frameWidth/2-frameOffset, getHeight()/2+frameHeight/2+frameOffset);
        g2d.drawLine(getWidth()/2+frameWidth/2+frameOffset, getHeight()/2+frameHeight/2+frameOffset, getWidth()/2+frameWidth/2+frameOffset, getHeight()/2-frameHeight/2-frameOffset);
    }

    private void generateFigure(Graphics2D g2d, Spline spline) {
        List<Segment> figure = new ArrayList<>();
        int n = settings.getN();
        int m = settings.getM();
        int k = settings.getK();
        double a = settings.getA();
        double b = settings.getB();
        double c = settings.getC();
        double d = settings.getD();
        Point2D.Double[] points = new Point2D.Double[n*k+1];
        double[] sin = new double[m*k+1];
        double[] cos = new double[m*k+1];

        double x = a;
        double y = c;
        double stepX = Math.abs(a-b)/(points.length-1);
        double stepY = Math.abs(c-d)/(sin.length-1);
        for (int i = 0; i < points.length; ++i) {
            x = (x-b > 0.0001)?b:x;
            points[i] = spline.getNormSplinePoint(x);
            x += stepX;
        }
        for (int i = 0; i < sin.length; ++i) {
            sin[i] = Math.sin(y);
            cos[i] = Math.cos(y);
            y += stepY;
        }

        for (int i = 1; i < points.length; i += k) {
            for (int j = 0; j < sin.length; j += k) {
                for (int q = 0; q < k; ++q) {
                    ScenePoint p1 = new ScenePoint(points[i+q-1].y*cos[j], points[i+q-1].y*sin[j], points[i+q-1].x);
                    ScenePoint p2 = new ScenePoint(points[i+q].y*cos[j], points[i+q].y*sin[j], points[i+q].x);
                    figure.add(new Segment(p1, p2));
                }
            }
        }

        for (int j = 1; j < sin.length; j += k) {
            for (int i = 0; i < points.length; i += k) {
                for (int q = 0; q < k; ++q) {
                    ScenePoint p1 = new ScenePoint(points[i].y*cos[j+q-1], points[i].y*sin[j+q-1], points[i].x);
                    ScenePoint p2 = new ScenePoint(points[i].y*cos[j+q], points[i].y*sin[j+q], points[i].x);
                    figure.add(new Segment(p1, p2));
                }
            }
        }
        matrix = Matrix.getE(4);
        Matrix moveMatrix = new Matrix(new double[][]{
                {1.0, 0.0, 0.0, spline.x},
                {0.0, 1.0, 0.0, spline.y},
                {0.0, 0.0, 1.0, spline.z},
                {0.0, 0.0, 0.0, 1.0}
        });
        matrix = matrix.multMatrix(moveMatrix);

        drawAxis(g2d);
        if (Double.compare(spline.rx, 0.0) == 0 && Double.compare(spline.ry, 0.0) == 0 && Double.compare(spline.rz, 0.0) == 0)
            matrix = matrix.multMatrix(spline.getRotationMatrix());
        else {
            rotateX(spline.rx);
            rotateY(spline.ry);
            rotateZ(spline.rz);
            spline.setRotationMatrix(new Matrix(matrix));
        }
        g2d.setColor(spline.getColor());
        drawFigure(g2d, figure);
        matrix = Matrix.getE(4);
    }

    private void drawFigure(Graphics2D g2d, List<Segment> figure) {
        if (figure == null)
            return;
        for (Segment s : figure) {
            drawLine(g2d, s.getP1(), s.getP2());
        }
    }

    public void setRotationMatrix(Matrix rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }

    public Matrix getRotationMatrix() {
        return rotationMatrix;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public SettingsWindow getSettings() {
        return settings;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        panelSizeX = getWidth();
        panelSizeY = getHeight();
        backgroundColor = settings.getSceneColor();

        double zf = settings.getZf();
        double zn = settings.getZn();
        double sw = settings.getSw();
        double sh = settings.getSh();

        perspectiveMatrix = new Matrix(new double[][]{
                {zn/(zn-zf), 0.0, 0.0, -zn*zf/(zn-zf)},
                {0.0, zf*(2.0*scale)/sw, 0.0, 0.0},
                {0.0, 0.0, zf*(2.0*scale)/sh, 0.0},
                {1.0, 0.0, 0.0, 0.0}
        });

        image = new BufferedImage(panelSizeX, panelSizeY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D)image.getGraphics();
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, panelSizeX, panelSizeY);

        matrix = Matrix.getE(4);
        drawAxis(g2d);

        for (int i = 0; i < settings.getSplines().size(); ++i) {
            if (settings.getSplines().get(i).canDrawSpline())
                generateFigure(g2d, settings.getSplines().get(i));
        }
        matrix = Matrix.getE(4);

        drawFrame(g2d);

        setPreferredSize(new Dimension(panelSizeX, panelSizeY));
        g.drawImage(image, 0, 0, panelSizeX, panelSizeY, this);
    }
}
