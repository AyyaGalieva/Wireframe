package ru.nsu.fit.g16201.galieva.Wireframe.View;

import ru.nsu.fit.g16201.galieva.Wireframe.Model.Matrix;
import ru.nsu.fit.g16201.galieva.Wireframe.Model.Model;
import ru.nsu.fit.g16201.galieva.Wireframe.View.SplineSettings.SettingsWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class GUI extends JFrame {
    private MenuBar menuBar;
    private JToolBar toolBar;
    private JLabel statusBar;

    private Model model;

    private WireframeScene wireframeScene;

    private Map<String, AbstractButton> buttonMap = new TreeMap<>();
    private Map<String, Menu> menuMap = new TreeMap<>();
    private Map<String, MenuItem> menuItemMap = new TreeMap<>();

    public GUI(Model model) {
        this.model = model;
        setTitle("Wireframe");
        setSize(1200, 800);
        setMinimumSize(new Dimension(600, 400));
        setLocationByPlatform(true);

        menuBar = new MenuBar();
        toolBar = new JToolBar();
        this.setMenuBar(menuBar);

        statusBar = new JLabel();
        statusBar.setPreferredSize(new Dimension(150, 15));
        statusBar.setBackground(Color.white);

        SettingsWindow settingsWindow = new SettingsWindow(model.getSpline());
        wireframeScene = new WireframeScene(settingsWindow);
        settingsWindow.setScene(wireframeScene);
        add(wireframeScene, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });

        addButton("Load", "File", "Load wireframe", true, false,"/resources/load.png", () -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/test/");
            fileChooser.setDialogTitle("Load wireframe");
            int f = fileChooser.showOpenDialog(GUI.this);
            if (f == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                model.loadWireframe(file.getAbsolutePath(), wireframeScene);
                wireframeScene.update(wireframeScene.getGraphics());
                wireframeScene.getSettings().switchToSpline(wireframeScene.getSettings().getSplines().get(wireframeScene.getSettings().getCur()));
            }
        });

        addButton("Save", "File", "Save wireframe", true, false,"/resources/save.png", () -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/test/");
            fileChooser.setDialogTitle("Save wireframe");
            int f = fileChooser.showOpenDialog(GUI.this);
            if (f == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                model.saveWireframe(file.getAbsolutePath(), wireframeScene);
            }
        });

        this.toolBar.addSeparator();

        addButton("Settings", "View", "Show settings", true, false,"/resources/settings.jpg", () -> {
            settingsWindow.setLocationRelativeTo(this);
            settingsWindow.setVisible(true);
        });

        addButton("Init", "View", "Set camera position to default", true, false,"/resources/clear.png", () -> {
            wireframeScene.setRotationMatrix(Matrix.getE(4));
            wireframeScene.setScale(100);
            wireframeScene.repaint();
        });

        this.toolBar.addSeparator();

        addButton("Info", "Help", "Show author's info", true, false, "/resources/info.jpg", () ->
                JOptionPane.showMessageDialog(null, "Wireframe v.1.0\n" + "Author:\t Ayya Galieva, gr. 16201",
                        "Author info", JOptionPane.INFORMATION_MESSAGE));
        add(toolBar, BorderLayout.NORTH);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void addButton(String name, String menuName, String toolTipText, boolean shutdown, boolean toggleValue, String imagePath, Runnable action) {
        AbstractButton button;
        MenuItem item;

        Image toolImage = null;
        try {
            toolImage = ImageIO.read(getClass().getResource(imagePath));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if (shutdown) {
            if (toolImage != null) {
                button = new JButton();
                button.setIcon(new ImageIcon(toolImage));
            }
            else {
                button = new JButton(name);
            }
            item = new MenuItem(name);
            item.addActionListener(e -> {
                if (item.isEnabled()) {
                    action.run();
                }
            });
        }
        else {
            if (toolImage != null) {
                button = new JToggleButton();
                button.setIcon(new ImageIcon(toolImage));
                button.setSelected(toggleValue);
            }
            else {
                button = new JToggleButton(name);
            }
            CheckboxMenuItem checkboxMenuItem = new CheckboxMenuItem(name);
            checkboxMenuItem.addItemListener(e -> {
                if (checkboxMenuItem.isEnabled())
                    action.run();
            });
            item = checkboxMenuItem;
        }

        button.setToolTipText(toolTipText);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean pressedOrEntered = false;
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled() && pressedOrEntered)
                    action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                pressedOrEntered = true;
                statusBar.setText(toolTipText);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusBar.setText("");
                pressedOrEntered = false;
            }
        };

        button.addMouseListener(mouseAdapter);
        toolBar.add(button);

        if (!menuMap.containsKey(menuName)) {
            Menu menu = new Menu(menuName);
            menuMap.put(menuName, menu);
            menuBar.add(menu);
        }
        menuMap.get(menuName).add(item);
        menuItemMap.put(name, item);
        buttonMap.put(name, button);
    }

    public void showFileIncorrect() {
        JOptionPane.showMessageDialog(this, "File is incorrect", "error", JOptionPane.WARNING_MESSAGE);
    }

    public void showSaveFailed() {
        JOptionPane.showMessageDialog(this, "Couldn't save file", "error", JOptionPane.WARNING_MESSAGE);
    }
}
