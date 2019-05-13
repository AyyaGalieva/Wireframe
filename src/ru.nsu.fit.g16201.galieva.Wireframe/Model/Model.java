package ru.nsu.fit.g16201.galieva.Wireframe.Model;

import ru.nsu.fit.g16201.galieva.Wireframe.View.GUI;
import ru.nsu.fit.g16201.galieva.Wireframe.View.SplineSettings.SettingsWindow;
import ru.nsu.fit.g16201.galieva.Wireframe.View.WireframeScene;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class Model {
    private GUI view;
    Spline spline = new Spline();

    public Model(){}

    public void setView(GUI view) {
        this.view = view;
    }

    public Spline getSpline() {
        return spline;
    }

    public void loadWireframe(String path, WireframeScene wireframeScene) {
        if (path == null) {
            view.showFileIncorrect();
            return;
        }
        try (Scanner sc = new Scanner(new File(path))) {
            String[] params = getNextParams(sc);
            int n = Integer.parseInt(params[0]);
            int m = Integer.parseInt(params[1]);
            int k = Integer.parseInt(params[2]);
            double a = Double.parseDouble(params[3]);
            double b = Double.parseDouble(params[4]);
            double c = Double.parseDouble(params[5]);
            double d = Double.parseDouble(params[6]);

            wireframeScene.getSettings().setN(n);
            wireframeScene.getSettings().setM(m);
            wireframeScene.getSettings().setK(k);
            wireframeScene.getSettings().setABCD(a, b, c, d);

            params = getNextParams(sc);
            double zn = Double.parseDouble(params[0]);
            double zf = Double.parseDouble(params[1]);
            double sw = Double.parseDouble(params[2]);
            double sh = Double.parseDouble(params[3]);

            wireframeScene.getSettings().setZnZf(zn, zf);
            wireframeScene.getSettings().setSwSh(sw, sh);

            params = getNextParams(sc);
            double e11 = Double.parseDouble(params[0]);
            double e12 = Double.parseDouble(params[1]);
            double e13 = Double.parseDouble(params[2]);
            params = getNextParams(sc);
            double e21 = Double.parseDouble(params[0]);
            double e22 = Double.parseDouble(params[1]);
            double e23 = Double.parseDouble(params[2]);
            params = getNextParams(sc);
            double e31 = Double.parseDouble(params[0]);
            double e32 = Double.parseDouble(params[1]);
            double e33 = Double.parseDouble(params[2]);

            wireframeScene.setRotationMatrix(new Matrix(new double[][]{
                    {e11, e12, e13, 0.0},
                    {e21, e22, e23, 0.0},
                    {e31, e32, e33, 0.0},
                    {0.0, 0.0, 0.0, 1.0}
            }));

            params = getNextParams(sc);
            wireframeScene.getSettings().setSceneColor(new Color(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2])));

            params = getNextParams(sc);
            wireframeScene.getSettings().setSplines(new ArrayList<>());
            int figuresCount = Integer.parseInt(params[0]);
            for(int i = 0; i < figuresCount; ++i) {
                params = getNextParams(sc);
                Color splineColor = new Color(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));

                params = getNextParams(sc);
                double cx = Double.parseDouble(params[0]);
                double cy = Double.parseDouble(params[1]);
                double cz = Double.parseDouble(params[2]);

                params = getNextParams(sc);
                double r11 = Double.parseDouble(params[0]);
                double r12 = Double.parseDouble(params[1]);
                double r13 = Double.parseDouble(params[2]);
                params = getNextParams(sc);
                double r21 = Double.parseDouble(params[0]);
                double r22 = Double.parseDouble(params[1]);
                double r23 = Double.parseDouble(params[2]);
                params = getNextParams(sc);
                double r31 = Double.parseDouble(params[0]);
                double r32 = Double.parseDouble(params[1]);
                double r33 = Double.parseDouble(params[2]);

                params = getNextParams(sc);
                int pointsCount = Integer.parseInt(params[0]);

                Spline spline = new Spline();
                spline.setColor(splineColor);
                spline.x = cx;
                spline.y = cy;
                spline.z = cz;
                for(int j = 0; j < pointsCount; ++j) {
                    params = getNextParams(sc);
                    spline.addPoint(new Point2D.Double(Double.parseDouble(params[0]), Double.parseDouble(params[1])));
                }
                spline.setRotationMatrix(new Matrix(new double[][]{
                        {r11, r12, r13, 0.0},
                        {r21, r22, r23, 0.0},
                        {r31, r32, r33, 0.0},
                        {0.0, 0.0, 0.0, 1.0}
                }));

                wireframeScene.getSettings().getSplines().add(spline);
            }
        } catch (Exception e) {
            view.showFileIncorrect();
        }
    }

    private String[] getNextParams(Scanner sc) {
        String str = "";
        while (str.isEmpty()) {
            String[] strs = sc.nextLine().split("//");
            str = strs[0].trim();
        }
        return str.split(" ");
    }

    public void saveWireframe(String path, WireframeScene wireframeScene) {
        if (path == null) {
            view.showSaveFailed();
            return;
        }
        String separator = System.lineSeparator();
        try (BufferedWriter bw = Files.newBufferedWriter((new File(path)).toPath(), Charset.forName("UTF-8"))) {
            bw.write(String.valueOf(wireframeScene.getSettings().getN()) + " " + wireframeScene.getSettings().getM() + " " + wireframeScene.getSettings().getK() + " ");
            bw.write(String.valueOf(wireframeScene.getSettings().getA()) + " " + wireframeScene.getSettings().getB() + " ");
            bw.write(String.valueOf(wireframeScene.getSettings().getC()) + " " + wireframeScene.getSettings().getD() + separator);

            bw.write(String.valueOf(wireframeScene.getSettings().getZn()) + " " + wireframeScene.getSettings().getZf() + " ");
            bw.write(String.valueOf(wireframeScene.getSettings().getSw()) + " " + wireframeScene.getSettings().getSh() + separator);

            Matrix sceneRotationMatrix = wireframeScene.getRotationMatrix();
            bw.write(String.valueOf(sceneRotationMatrix.getElement(0,0)) + " " + sceneRotationMatrix.getElement(1,0) + " " + sceneRotationMatrix.getElement(2,0) + separator);
            bw.write(String.valueOf(sceneRotationMatrix.getElement(0,1)) + " " + sceneRotationMatrix.getElement(1,1) + " " + sceneRotationMatrix.getElement(2,1) + separator);
            bw.write(String.valueOf(sceneRotationMatrix.getElement(0,2)) + " " + sceneRotationMatrix.getElement(1,2) + " " + sceneRotationMatrix.getElement(2,2) + separator);

            Color backgroundColor = SettingsWindow.getSceneColor();
            bw.write(String.valueOf(backgroundColor.getRed()) + " " + backgroundColor.getGreen() + " " + backgroundColor.getBlue() + separator);

            bw.write(String.valueOf(wireframeScene.getSettings().getSplines().size()) + separator);
            for (Spline spline : wireframeScene.getSettings().getSplines()) {
                Color color = spline.getColor();
                bw.write(String.valueOf(color.getRed()) + " " + color.getGreen() + " " + color.getBlue() + separator);

                ScenePoint center = new ScenePoint(spline.x, spline.y, spline.z);
                bw.write(String.valueOf(center.getX()) + " " + center.getY() + " " + center.getZ() + separator);

                Matrix rotationMatrix = spline.getRotationMatrix();
                bw.write(String.valueOf(rotationMatrix.getElement(0,0)) + " " + rotationMatrix.getElement(1,0) + " " + rotationMatrix.getElement(2,0) + separator);
                bw.write(String.valueOf(rotationMatrix.getElement(0,1)) + " " + rotationMatrix.getElement(1,1) + " " + rotationMatrix.getElement(2,1) + separator);
                bw.write(String.valueOf(rotationMatrix.getElement(0,2)) + " " + rotationMatrix.getElement(1,2) + " " + rotationMatrix.getElement(2,2) + separator);

                bw.write(String.valueOf(spline.getPoints().size()) + separator);
                for (Point2D.Double point : spline.getPoints()) {
                    bw.write(String.valueOf(point.x + " " + point.y) + separator);
                }
            }
        } catch (Exception e) {
            view.showSaveFailed();
        }
    }
}
