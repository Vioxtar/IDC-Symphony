package visual;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Window extends JPanel implements ActionListener {

    public Visualizer visParent;

    public static final int CANVAS_WIDTH = 1080;
    public static final int CANVAS_HEIGHT = 720;

    int targetMS = 1;

    Timer tm = new Timer(targetMS,this);

    public Window(Visualizer visParent) {
        this.visParent = visParent;
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    }

    long prevTime = System.nanoTime();

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.DARK_GRAY);

        long currTime = System.nanoTime();
        long dltaTime = currTime - prevTime; prevTime = currTime;
        double timeMul = (double) dltaTime / 1000000; // 1 ms = 10^6 ns

        Graphics2D g2d = (Graphics2D) g;
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        visParent.visualize(timeMul, g2d);

        tm.start(); // Start the timer to call future draws
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
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

