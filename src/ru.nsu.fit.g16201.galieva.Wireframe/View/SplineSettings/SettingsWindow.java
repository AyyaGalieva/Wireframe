package ru.nsu.fit.g16201.galieva.Wireframe.View.SplineSettings;

import ru.nsu.fit.g16201.galieva.Wireframe.Model.Spline;
import ru.nsu.fit.g16201.galieva.Wireframe.View.WireframeScene;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class SettingsWindow extends JFrame {
    private static final Map<String, Parameter> parameters;
    private SplineViewParameters splineSettings;
    private double a = 0.0;
    private double b = 1.0;
    private double c = 0.0;
    private double d = 6.28;
    private double zn = 20.0;
    private double zf = 5.0;
    private double sw = 600.0;
    private double sh = 600.0;
    private int cur = 0;
    private SplineField splineField;
    private Spline spline;
    private ArrayList<Spline> splines;
    private GridBagConstraints gbc;
    private static Color sceneColor = Color.black;
    private WireframeScene scene;

    private Map<String, JSpinner> spinners = new TreeMap<>();

    private static final String[] paramNames = new String[]{"sceneR", "sceneG", "sceneB", "spline№", "n", "m", "k", "R", "G", "B", "a", "b", "c", "d", "Cx", "Cy", "Cz", "Rx", "Ry", "Rz", "zn", "zf", "sw", "sh"};
    static{
        parameters = new TreeMap<>();

        parameters.put("n", new Parameter(5,5, 100, 1));
        parameters.put("m", new Parameter(5,5, 100, 1));
        parameters.put("k", new Parameter(5,5, 100, 1));
        parameters.put("spline№", new Parameter(0,0, 0, 1));

        parameters.put("R", new Parameter(0,0, 255, 1));
        parameters.put("G", new Parameter(255,0, 255, 1));
        parameters.put("B", new Parameter(0,0, 255, 1));

        parameters.put("sceneR", new Parameter(sceneColor.getRed(), 0, 255, 1));
        parameters.put("sceneG", new Parameter(sceneColor.getGreen(), 0, 255, 1));
        parameters.put("sceneB", new Parameter(sceneColor.getBlue(), 0, 255, 1));

        parameters.put("a", new Parameter(0,0, 1, 0.01));
        parameters.put("b", new Parameter(1,0, 1, 0.01));
        parameters.put("c", new Parameter(0,0, 6.283, 0.01));
        parameters.put("d", new Parameter(6.283,0, 6.283, 0.01));

        parameters.put("Rx", new Parameter(0,0, 360, 1));
        parameters.put("Ry", new Parameter(0,0, 360, 1));
        parameters.put("Rz", new Parameter(0,0, 360, 1));

        parameters.put("zn", new Parameter(20.0,5.5, 40.0, 0.5));
        parameters.put("zf", new Parameter(5.0,1.0, 19.5, 0.5));
        parameters.put("sw", new Parameter(600.0,0.0, 10000.0, 10.0));
        parameters.put("sh", new Parameter(600.0,0.0, 10000.0, 10.0));

        parameters.put("Cx", new Parameter(0, -10, 10, 0.1));
        parameters.put("Cy", new Parameter(0, -10, 10, 0.1));
        parameters.put("Cz", new Parameter(0, -10, 10, 0.1));
    }

    public SettingsWindow(Spline s) {
        setTitle("Settings");
        setSize(800, 600);
        setMinimumSize(new Dimension(300, 200));

        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        this.spline = s;
        splineField = new SplineField();
        splineField.setSpline(s);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        add(splineField, gbc);

        cur = 0;
        splines = new ArrayList<>();
        splines.add(s);

        addParameters();
    }

    private void addParameters() {
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridLayout(7, 10));
        ChangeListener cl = e -> updateSettings();
        for (String p : paramNames) {
            JLabel label = new JLabel(" " + p + ":");
            parametersPanel.add(label);
            Parameter parameter = parameters.get(p);
            SpinnerNumberModel snm = new SpinnerNumberModel(parameter.getValue(), parameter.getMinValue(), parameter.getMaxValue(), parameter.getStep());
            JSpinner spinner = new JSpinner(snm);
            spinner.addChangeListener(cl);
            spinners.put(p, spinner);
            parametersPanel.add(spinner);
        }

        AbstractButton addButton = new JButton("New");
        MouseAdapter mouseAdapterAdd = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                splines.add(new Spline());
                cur = splines.size() - 1;
                switchToSpline(splines.get(cur));
                spinners.get("spline№").setModel(new SpinnerNumberModel((double)cur, 0, splines.size()-1, 1));
                updateSettings();
            }
        };

        addButton.addMouseListener(mouseAdapterAdd);
        parametersPanel.add(addButton);

        AbstractButton deleteButton = new JButton("Delete spline");
        MouseAdapter mouseAdapterDelete = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (splines.size() - 1 > 0) {
                    splines.remove(cur);
                    cur = splines.size() - 1;
                    switchToSpline(splines.get(cur));
                    spinners.get("spline№").setModel(new SpinnerNumberModel((double)cur, 0, splines.size()-1, 1));
                    updateSettings();
                }
            }
        };
        deleteButton.addMouseListener(mouseAdapterDelete);
        parametersPanel.add(deleteButton);

        AbstractButton applyButton = new JButton("Apply");
        MouseAdapter mouseAdapterApply = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                scene.repaint();
            }
        };

        applyButton.addMouseListener(mouseAdapterApply);
        parametersPanel.add(applyButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        add(parametersPanel, gbc);
        updateSettings();
    }

    public void switchToSpline(Spline spline) {
        splineField.setSpline(spline);
        spinners.get("spline№").setModel(new SpinnerNumberModel((double)cur, 0, splines.size()-1, 1));
        spinners.get("R").setModel(new SpinnerNumberModel((double)splines.get(cur).getColor().getRed(), 0, 255, 1));
        spinners.get("G").setModel(new SpinnerNumberModel((double)splines.get(cur).getColor().getGreen(), 0, 255, 1));
        spinners.get("B").setModel(new SpinnerNumberModel((double)splines.get(cur).getColor().getBlue(), 0, 255, 1));
        spinners.get("Cx").setModel(new SpinnerNumberModel(splines.get(cur).x, -10, 10, 0.1));
        spinners.get("Cy").setModel(new SpinnerNumberModel(splines.get(cur).y, -10, 10, 0.1));
        spinners.get("Cz").setModel(new SpinnerNumberModel(splines.get(cur).z, -10, 10, 0.1));
        spinners.get("Rx").setModel(new SpinnerNumberModel(splines.get(cur).rx, 0, 360, 1));
        spinners.get("Ry").setModel(new SpinnerNumberModel(splines.get(cur).ry, 0, 360, 1));
        spinners.get("Rz").setModel(new SpinnerNumberModel(splines.get(cur).rz, 0, 360, 1));
    }

    public void updateSettings() {
        SplineViewParameters splineViewParameters = new SplineViewParameters();
        int prev = cur;
        cur = (int)(double)(Double)spinners.get("spline№").getValue();
        if (prev != cur)
            switchToSpline(splines.get(cur));
        Color splineColor = new Color((int)(double)(Double)spinners.get("R").getValue(), (int)(double)(Double)spinners.get("G").getValue(), (int)(double)(Double)spinners.get("B").getValue());
        splineViewParameters.setSplineColor(splineColor);
        splines.get(cur).setColor(splineColor);

        sw = (double)(Double)spinners.get("sw").getValue();
        sh = (double)(Double)spinners.get("sh").getValue();
        double znValue = (double)(Double)spinners.get("zn").getValue();
        double zfValue = (double)(Double)spinners.get("zf").getValue();
        spinners.get("zf").setModel(new SpinnerNumberModel(
                Math.min(znValue, zfValue), 0.0, Math.min(40.0, znValue-0.5), 0.5));
        spinners.get("zn").setModel(new SpinnerNumberModel(
                Math.max(znValue, zfValue), Math.max(0.0, zfValue+0.5), 40.0, 0.5));
        zn = Math.max(znValue, zfValue);
        zf = Math.min(znValue, zfValue);

        setSceneColor(new Color((int)(double)(Double)spinners.get("sceneR").getValue(),
                (int)(double)(Double)spinners.get("sceneG").getValue(),
                (int)(double)(Double)spinners.get("sceneB").getValue()));

        splines.get(cur).x = (double)(Double)spinners.get("Cx").getValue();
        splines.get(cur).y = (double)(Double)spinners.get("Cy").getValue();
        splines.get(cur).z = (double)(Double)spinners.get("Cz").getValue();
        splines.get(cur).rx = (double)(Double)spinners.get("Rx").getValue();
        splines.get(cur).ry = (double)(Double)spinners.get("Ry").getValue();
        splines.get(cur).rz = (double)(Double)spinners.get("Rz").getValue();

        double aValue = (double)(Double)spinners.get("a").getValue();
        double bValue = (double)(Double)spinners.get("b").getValue();
        double cValue = (double)(Double)spinners.get("c").getValue();
        double dValue = (double)(Double)spinners.get("d").getValue();
        spinners.get("a").setModel(new SpinnerNumberModel(
                Math.min(aValue, bValue), 0.0, Math.min(1.0, bValue), 0.01));
        spinners.get("b").setModel(new SpinnerNumberModel(
                Math.max(aValue, bValue), Math.max(0.0, aValue), 1.0, 0.01));
        spinners.get("c").setModel(new SpinnerNumberModel(
                Math.min(cValue, dValue), 0., Math.min(6.283, bValue), 0.01));
        spinners.get("d").setModel(new SpinnerNumberModel(
                Math.max(cValue, dValue), Math.max(0., aValue), 6.283, 0.01));
        a =  Math.min(aValue, bValue);
        b =  Math.max(aValue, bValue);
        c = Math.min(cValue, dValue);
        d = Math.max(cValue, dValue);

        splineViewParameters.setLeftBorder(aValue);
        splineViewParameters.setRightBorder(bValue);
        splineSettings = splineViewParameters;
        splineField.setSplineViewParameters(splineViewParameters);
    }

    public void setSceneColor(Color c){
        sceneColor = c;
        spinners.get("sceneR").setValue((double)(int)(Integer)c.getRed());
        spinners.get("sceneG").setValue((double)(int)(Integer)c.getGreen());
        spinners.get("sceneB").setValue((double)(int)(Integer)c.getBlue());
    }

    public static Color getSceneColor() {
        return sceneColor;
    }

    public int getN() {
        return (int)(double)(Double)spinners.get("n").getValue();
    }

    public int getM(){
        return (int)(double)(Double)spinners.get("m").getValue();
    }

    public int getK(){
        return (int)(double)(Double)spinners.get("k").getValue();
    }

    public void setN(int n){
        spinners.get("n").setModel(new SpinnerNumberModel((double)n, 5, 100, 1));
    }

    public void setM(int m){
        spinners.get("m").setModel(new SpinnerNumberModel((double)m, 5, 100, 1));
    }

    public void setK(int k){
        spinners.get("k").setModel(new SpinnerNumberModel((double)k, 5, 100, 1));
    }

    public void setABCD(double a, double b, double c, double d){
        spinners.get("a").setModel(new SpinnerNumberModel(Math.min(a, b), 0.0, Math.min(1.0, b), 0.01));
        spinners.get("b").setModel(new SpinnerNumberModel(Math.max(a, b), Math.max(0.0, a), 1.0, 0.01));
        spinners.get("c").setModel(new SpinnerNumberModel( Math.min(c, d), 0.0, Math.min(6.283, b), 0.01));
        spinners.get("d").setModel(new SpinnerNumberModel(Math.max(c, d), Math.max(0.0, a), 6.283, 0.01));
        this.a =  Math.min(a, b);
        this.b =  Math.max(a, b);
        this.c = Math.min(c, d);
        this.d = Math.max(c, d);
    }

    public double getZn() {
        return zn;
    }

    public double getZf() {
        return zf;
    }

    public void setZnZf(double i, double j) {
        double newZf = Math.min(i, j);
        double newZn = Math.max(i, j);
        if ((newZf<1.0)||(newZf>19.5)||(newZn<5.5)||(newZn>40.0))
            return;
        spinners.get("zf").setModel(new SpinnerNumberModel(newZf, 1.0, 19.5, 0.5));
        spinners.get("zn").setModel(new SpinnerNumberModel(newZn, 5.5, 40.0, 0.5));
        zn = Math.max(i, j);
        zf = Math.min(i, j);
    }

    public double getSw() {
        return sw;
    }

    public double getSh() {
        return sh;
    }

    public void setSwSh(double sw, double sh){
        this.sw = sw;
        this.sh = sh;
        spinners.get("sw").setValue(sw);
        spinners.get("sh").setValue(sh);
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public int getCur() {
        return cur;
    }

    public ArrayList<Spline> getSplines() {
        return splines;
    }

    public void setSplines(ArrayList<Spline> splines) {
        this.splines = splines;
    }

    public SplineViewParameters getSplineViewParameters(){
        return splineSettings;
    }

    public void setScene(WireframeScene scene) {
        this.scene = scene;
    }
}
