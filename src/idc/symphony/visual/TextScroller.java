package idc.symphony.visual;

import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.LinkedList;
import java.util.ListIterator;

public class TextScroller {

    LinkedList<QueuedTextLine> lines; // The visible text lines

    int textSize;
    public void setTextSize(int size) {
        textSize = size;
    }

    int maxLines = 1;
    public void setMaxLines(int lines) {
        maxLines = lines;
    }

    int wrappingWidth = 1000;
    public void setWrappingWidth(int width) { wrappingWidth = width; }

    /**
     * Returns the maxs of this trail - defined by the maxs of the head and tail points.
     * @return
     */
    public double[] getMaxs() {
        if (lines.size() <= 0) {
            return null;
        }
        return new double[] {
            lines.getFirst().body.x + wrappingWidth,
            lines.getLast().body.y
        };
    }

    /**
     * Returns the mins of this trail - defined by the mins of the head and tail points.
     * @return
     */
    public double[] getMins() {
        if (lines.size() <= 0) {
            return null;
        }
        return new double[] {
            lines.getFirst().body.x,
            lines.getFirst().body.y
        };
    }


    double targetX; double targetY;
    public void setX(double x) {
        targetX = x;
    }
    public void setY(double y) {
        targetY = y;
    }

    public TextScroller() {
        lines = new LinkedList<>();
    }

    public void addText(String txt, Color col) {
        QueuedTextLine newLine = new QueuedTextLine(txt);
        newLine.col = col;
        if (lines.size() > 0) {
            QueuedTextLine first = lines.getFirst();
            newLine.body.x = first.body.x;
            newLine.body.y = first.body.y;
        } else {
            newLine.body.x = targetX;
            newLine.body.y = targetY;
        }

        lines.addFirst(newLine);
        newLine.fade = 0; newLine.fadingOut = false;
        int size = lines.size();
        if (size > maxLines) {
            ListIterator it = lines.listIterator(size);
            while (size > maxLines && it.hasPrevious()) {
                QueuedTextLine line = (QueuedTextLine)it.previous();
                line.fadingOut = true;
                size--;
            }
        }
    }

    public void clearText() {
        ListIterator it = lines.listIterator(0);
        while (it.hasNext()) {
            QueuedTextLine line = (QueuedTextLine)it.next();
            line.fadingOut = true;
        }
    }

    public void update() {
        double mul = 0.01;

        ListIterator it = lines.listIterator();
        double spacing = 100;
        double ySum = 0;
        while (it.hasNext()) {
            QueuedTextLine line = (QueuedTextLine)it.next();
            line.body.aimToX(targetX, mul);
            line.body.aimToY(targetY + ySum, mul);
            line.body.applyVel(mul); line.body.applySlide(0.2, mul);
            ySum += line.lastHeight + spacing;

            if (!line.fadingOut) {
                line.fade = (1 + line.fade * 99) / 100;
            } else {
                line.fade = line.fade * 0.97;
                if (line.fade <= 0.001) {
                    it.remove();
                }
            }
        }
    }

    public void draw(Pane g) {

        ListIterator it = lines.listIterator(lines.size());
        while (it.hasPrevious()) {
            QueuedTextLine line = (QueuedTextLine)it.previous();

            // Title
            Text titleTxt = new Text(line.text);
            titleTxt.setFont(new Font(textSize));

            titleTxt.setWrappingWidth(wrappingWidth);

            titleTxt.setX(line.body.x); titleTxt.setY(line.body.y);
            titleTxt.setTextOrigin(VPos.TOP); titleTxt.setTextAlignment(TextAlignment.LEFT);
            titleTxt.setFill(line.col);
            titleTxt.setOpacity(line.fade);

            line.lastHeight = titleTxt.getLayoutBounds().getHeight();

            g.getChildren().add(titleTxt);
        }

    }

    private class QueuedTextLine {

        TPoint body;
        String text;
        double fade;
        boolean fadingOut;
        double lastHeight = 10;
        Color col;

        private QueuedTextLine(String text) {
            this.body = new TPoint();
            this.text = text;
        }
    }

}
