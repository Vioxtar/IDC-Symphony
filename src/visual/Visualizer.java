package visual;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class Visualizer extends JFrame {

    Window window;

    double frameY = 0;
    double frameX = 0;

    ArrayList<Trail> trails;

    public Visualizer() {
        this.window = new Window(this);
        JFrame frame = new JFrame("IDC Symphony");
        frame.setContentPane(this.window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();                      // "this" JFrame packs its components
        frame.setLocationRelativeTo(null); // center the application window
        frame.setVisible(true);            // show it


        trails = new ArrayList<Trail>();

        for (int i = 1; i <= 1; i++) {
            addTrail();
        }
    }

    public void visualize(double timeMul, Graphics g) {
        // Scroll up
        double scrollSpeed = 0.01;
        frameY += scrollSpeed * timeMul;

        // Update trail behavior
        for (Trail trail : trails) {
            trail.setTargetY(-frameY + 200);
            trail.update(timeMul);
        }

        // Simulate note plays
        if (ranRange(1, 50) == 1) {
            Trail ran = trails.get(ranRange(0, trails.size() - 1));
            ran.setImmExp(5);
            ran.setImmRadius(50);
        }

        // Simualte adding trails
        if (ranRange(1, 800) == 1) {
            Trail ran = trails.get(ranRange(0, trails.size() - 1));
            double xOrigin = window.getWidth() / 2;
            Color c = new Color(ranRange(0,255), ranRange(0,255), ranRange(0,255));
            Trail newTrail = new Trail(++trailID, c, c, xOrigin, 300, ran);
            newTrail.setTargetX(window.getWidth() / 2 + ranRange(-400, 400));
            newTrail.setTargetRadius(5);
            newTrail.setTargetExp(0.25);
            newTrail.setImmRadius(100);
            trails.add(newTrail);

        }

        // Draw all trails
        for (Trail trail : trails) {
            trail.draw(g, 0, frameY);
        }
    }

    int trailID = -1;
    public void addTrail() {
        double xOrigin = window.getWidth() / 2;
        Trail ran = null;
        if (!trails.isEmpty()) {
            xOrigin = trails.get(ranRange(0, trails.size() - 1)).targetX;
            if (ranRange(0, 3) == 1) {
                ran = trails.get(ranRange(0, trails.size() - 1));
            }
        }
        Trail newTrail = new Trail(++trailID, Color.WHITE, Color.LIGHT_GRAY, xOrigin, 300, ran);
        newTrail.setTargetX(window.getWidth() / 2 + ranRange(-400, 400));
        newTrail.setTargetRadius(5);
        newTrail.setTargetExp(0.25);
        newTrail.setImmRadius(100);
        trails.add(newTrail);
        // TODO: Support connecting trails by having the new tail spawn on the parent's head,
        // TODO: and every time the parent extends its trail, it pushes the child's tail with it
        // TODO: until it reaches its tail, and stays there?
    }

    public static int ranRange(int a, int b){
        if (a == b) {
            return a;
        }
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        double ranBase = Math.random();
        int diff = max - min + 1;
        int ran = (int)((ranBase * diff) + min);
        return ran;
    }

    public static double ranRange(double a, double b){
        if (a == b) {
            return a;
        }
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        double ranBase = Math.random();
        double diff = max - min;
        double ran = (ranBase * diff) + min;
        return ran;
    }

}
