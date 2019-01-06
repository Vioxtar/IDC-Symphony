package idc.symphony.ui.logging;

import idc.symphony.ui.logging.LogFormatter;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Logging Handler for writing statuses to user log in UI
 */
public class LogHandler extends Handler{
    private static final Map<Level, String> COLORS = new HashMap<>();
    private static final String DEFAULT_COLOR = "#000000";
    static {
        COLORS.put(Level.WARNING, "#AA9911");
        COLORS.put(Level.SEVERE, "#AA2222");
        COLORS.put(Level.FINE, "#666");
    }

    private int maxLogs;
    private ListView<Label> logListView;
    private List<Label> items;

    public LogHandler(ListView<Label> logListView, int maxLogs) {
        this.logListView = logListView;
        this.items = logListView.getItems();
        this.maxLogs = maxLogs;

        setLevel(Level.FINE);
        setFormatter(new LogFormatter());
    }


    @Override
    public void publish(LogRecord record) {
        if (logListView != null) {
           Platform.runLater(() -> {
               if(items.size() > maxLogs) {
                   items.remove(0);
               }

               Label recordLabel = new Label();
               recordLabel.setTextFill(Paint.valueOf(COLORS.getOrDefault(record.getLevel(), DEFAULT_COLOR)));
               recordLabel.setFont(Font.font("monospaced"));
               recordLabel.setPrefWidth(logListView.getPrefWidth());
               recordLabel.setWrapText(true);
               recordLabel.setText(getFormatter().format(record));

               items.add(recordLabel);
           });
        }
    }

    @Override
    public void flush() {
        // Log handler does not contain stream
    }

    @Override
    public void close() throws SecurityException {
        // Log handler does not contain stream
    }
}
