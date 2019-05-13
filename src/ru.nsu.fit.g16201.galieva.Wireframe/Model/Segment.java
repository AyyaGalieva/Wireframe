package ru.nsu.fit.g16201.galieva.Wireframe.Model;

public class Segment {
    private ScenePoint p1, p2;

    public Segment(ScenePoint p1, ScenePoint p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public ScenePoint getP1() {
        return p1;
    }

    public ScenePoint getP2() {
        return p2;
    }
}
