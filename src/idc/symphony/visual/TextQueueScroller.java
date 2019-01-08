package idc.symphony.visual;

import java.util.LinkedList;

public class TextQueueScroller {

    int maxLines = 5;
    TPoint topCenter;
    LinkedList<QueuedTextLine> lines;

    public void addText(String txt) {
        QueuedTextLine newLine = new QueuedTextLine(txt);
        lines.addFirst(newLine);
        if (lines.size() > maxLines) {
            lines.removeLast();
        }
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
