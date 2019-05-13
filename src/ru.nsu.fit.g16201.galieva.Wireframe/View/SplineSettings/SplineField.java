package ru.nsu.fit.g16201.galieva.Wireframe.View.SplineSettings;

import ru.nsu.fit.g16201.galieva.Wireframe.Model.Spline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SplineField extends JPanel {
    private BufferedImage image;
    private Spline spline;
    private SplineViewParameters splineViewParameters;
    private int panelSizeX = 0, panelSizeY = 0;
    private static int pointRadius = 5;
    private int selectedNode;
    private boolean isNodeSelected = false;
    private double scale;

    public SplineField() {
        setFocusable(true);
        requestFocusInWindow();

        selectedNode = -1;
        scale = 50;

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Point2D.Double p = definePoint(e);
                int pointPos = getPointPos(spline.getPoints(), p);
                if (pointPos != -1) {
                    isNodeSelected = true;
                    selectedNode = pointPos;
                }
                else {
                    isNodeSelected = false;
                    selectedNode = -1;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (selectedNode == -1) {
                    Point2D.Double p = definePoint(e);
                    spline.addPoint(p);
                    selectedNode = getPointPos(spline.getPoints(), p);
                    isNodeSelected = true;
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (isNodeSelected) {
                    spline.movePoint(selectedNode, definePoint(e));
                    repaint();
                }
            }

            private Point2D.Double definePoint(MouseEvent e) {
                return new Point2D.Double((e.getX()-(panelSizeX/2.0))/scale, (e.getY()-(panelSizeY/2.0))/scale);
            }

            private int getPointPos(List<Point2D.Double> points, Point2D.Double p) {
                for (int i = 0; i < points.size(); ++i) {
                    if (Math.abs(p.x-points.get(i).x) < (double)pointRadius/scale && Math.abs(p.y-points.get(i).y) < (double)pointRadius/scale)
                        return i;
                }
                return -1;
            }
        };

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_DELETE && isNodeSelected) {
                    isNodeSelected = false;
                    spline.removePoint(selectedNode);
                    selectedNode = spline.getPoints().size()-1;
                    isNodeSelected = true;
                    repaint();
                }
            }
        };

        addMouseMotionListener(mouseAdapter);
        addMouseListener(mouseAdapter);
        addKeyListener(keyAdapter);
    }

    private void recalcScale() {
        double maxX = 0.0, maxY = 0.0;
        for (Point2D.Double p : spline.getPoints()) {
            maxX = Math.max(maxX, Math.abs(p.x));
            maxY = Math.max(maxY, Math.abs(p.y));
        }
        maxX += 1.0;
        maxY += 1.0;
        scale = 50;
        if (maxY > 0.0001)
            scale = Math.min(scale, (image.getHeight()/2)/maxY);
        if (maxX > 0.0001)
            scale = Math.min(scale, (image.getWidth()/2)/maxX);
    }

    private void drawSpline(Graphics2D g2d) {
        List<Point2D.Double> points = spline.getPoints();

        for (int i = 0; i < points.size(); ++i) {
            Point2D.Double point = points.get(i);
            if (i == selectedNode)
                g2d.setColor(Color.red);
            else g2d.setColor(Color.white);

            g2d.drawOval((int)(point.x*scale + panelSizeX/2 - pointRadius), (int)(point.y*scale + panelSizeY/2 - pointRadius), pointRadius*2, pointRadius*2);
            if (i == selectedNode)
                g2d.drawOval((int)(point.x*scale + panelSizeX/2 - pointRadius*2), (int)(point.y*scale + panelSizeY/2 - pointRadius*2), pointRadius*4, pointRadius*4);
        }

        g2d.setColor(Color.darkGray);
        for (int i = 0; i < points.size() - 1; ++i){
            Point2D.Double p1 = points.get(i);
            Point2D.Double p2 = points.get(i+1);
            g2d.drawLine((int)(p1.x*scale + panelSizeX/2), (int)(p1.y*scale + panelSizeY/2),
                    (int)(p2.x*scale + panelSizeX/2), (int)(p2.y*scale + panelSizeY/2));
        }

        if (!spline.canDrawSpline())
            return;

        ArrayList<Point2D.Double> splinePoints = new ArrayList<>();
        for (double t = 0.0; t <= 1.0001; t += 0.001)
            splinePoints.add(spline.getNormSplinePoint(t));

        Color splineColor = splineViewParameters.getSplineColor();
        for (int i = 0; i < splinePoints.size()-1; ++i) {
            double pos = ((double)i/(splinePoints.size()-1));
            if (pos >= splineViewParameters.getLeftBorder() && pos <= splineViewParameters.getRightBorder()){
                g2d.setColor(splineColor);
            }
            else {
                g2d.setColor(new Color(splineColor.getRed()/2, splineColor.getGreen()/2, splineColor.getBlue()/2));
            }
            g2d.drawLine(
                    (int)((splinePoints.get(i).x*scale) + panelSizeX/2),
                    (int)((splinePoints.get(i).y*scale) + panelSizeY/2),
                    (int)((splinePoints.get(i+1).x*scale) + panelSizeX/2),
                    (int)((splinePoints.get(i+1).y*scale) + panelSizeY/2));
        }
    }

    public void setSpline(Spline spline) {
        this.spline = spline;
    }

    public void setSplineViewParameters(SplineViewParameters splineViewParameters) {
        this.splineViewParameters = splineViewParameters;
        repaint();
    }

    @Override
    protected  void paintComponent(Graphics g) {
        if (splineViewParameters == null) {
            return;
        }

        super.paintComponent(g);

        panelSizeX = getWidth();
        panelSizeY = getHeight();

        setPreferredSize(new Dimension(panelSizeX, panelSizeY));

        image = new BufferedImage(panelSizeX, panelSizeY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D)image.getGraphics();

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.setColor(Color.gray);
        g2d.drawLine(0, panelSizeY/2, panelSizeX, panelSizeY/2);
        g2d.drawLine(panelSizeX/2, 0, panelSizeX/2, panelSizeY);

        if (spline != null) {
            recalcScale();
            drawSpline(g2d);
        }

        g.drawImage(image, 0, 0, panelSizeX, panelSizeY, this);
    }
}
