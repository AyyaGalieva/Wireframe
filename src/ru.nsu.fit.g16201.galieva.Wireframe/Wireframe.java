package ru.nsu.fit.g16201.galieva.Wireframe;

import ru.nsu.fit.g16201.galieva.Wireframe.Model.Model;
import ru.nsu.fit.g16201.galieva.Wireframe.View.GUI;

public class Wireframe {
    public static void main(String[] args) {
        Model m = new Model();
        GUI view = new GUI(m);
        m.setView(view);
        view.setVisible(true);
    }
}
