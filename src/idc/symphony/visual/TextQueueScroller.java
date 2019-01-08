package idc.symphony.visual;

import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.ListIterator;

public class TextQueueScroller {

    QueuedTextLine head; // Dummy
    LinkedList<QueuedTextLine> lines; // The visible text lines
    QueuedTextLine tail; // Dummy

    int maxLines = 1;
    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    double targetX; double targetY;
    public void setX(double x) {
        targetX = x;
    }
    public void setY(double y) {
        targetY = y;
    }

    public void addText(String txt) {
        QueuedTextLine newLine = new QueuedTextLine(txt);
        lines.addFirst(newLine);
        if (lines.size() > maxLines) {
            lines.removeLast();
        }
    }

    public void scrollDown() {
        lines.addFirst(head);
        tail = lines.getLast();
    }

    public void update() {
        double mul = 0.01;
        head.body.aimToX(targetX, mul);
        head.body.aimToY(targetY, mul);
        head.body.applyVel(mul); head.body.applySlide(0.2, mul);

        ListIterator it = lines.listIterator();
        double seg = 50;
        while (it.hasNext()) {
            QueuedTextLine line = (QueuedTextLine)it.next();
            line.body.aimToX(targetX, mul);
            line.body.aimToY(targetY + seg, mul);
            line.body.applyVel(mul); line.body.applySlide(0.2, mul);
        }
    }

    public void draw(Pane g) {

    }





    public TextQueueScroller(){}

    private class QueuedTextLine {

        TPoint body;
        String text;

        private QueuedTextLine(String text) {
            this.body = new TPoint();
            this.text = text;
        }
    }

}
