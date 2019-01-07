package idc.symphony.ui.logging;

import idc.symphony.ui.logging.LogFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Logging Handler for writing statuses to user log in UI
 *
 * Note:
 * Using Platform.runLater() will potentially create hundreds of callbacks in one second
 * Instead, Logging Handler uses JavaFX TimeLine in order to poll for LogRecords at a fixed rate.
 */
public class LogHandler extends Handler{
    private static final Map<Level, String> COLORS = new HashMap<>();
    private static final String DEFAULT_COLOR = "#000000";
    static {
        COLORS.put(Level.WARNING, "#AA9911");
        COLORS.put(Level.SEVERE, "#AA2222");
        COLORS.put(Level.FINE, "#666");
        COLORS.put(Level.FINER, "#999");
    }

    // Cyclic buffer
    private int maxLogs;

    private ListView<Label> logListView;
    private List<Label> items;

    private DoubleBinding cellWidth;

    private BlockingDeque<LogRecord> records = new LinkedBlockingDeque<>(10000);
    private List<LogRecord> logBatchCache = new ArrayList<LogRecord>(100);

    public LogHandler(ListView<Label> logListView, int maxLogs) {
        this.logListView = logListView;
        this.items = logListView.getItems();
        this.maxLogs = maxLogs;

        cellWidth = Bindings.createDoubleBinding(
                () -> logListView.getWidth() - logListView.getPadding().getLeft() - logListView.getPadding().getRight() - 1,
                logListView.widthProperty(), logListView.paddingProperty());

        setFormatter(new LogFormatter());


        Timeline logPoller = new Timeline(30.0,
                new KeyFrame(Duration.seconds(0.2), event -> {
                    records.drainTo(logBatchCache, 100);

                    logBatchCache.forEach(this::publishRecord);
                    logBatchCache.clear();
                }));
        logPoller.setCycleCount(Timeline.INDEFINITE);
        logPoller.play();
    }


    @Override
    public synchronized void publish(LogRecord record) {
        records.offer(record);
    }

    private void publishRecord(LogRecord record){
        if (logListView != null) {
            Label recordLabel = getNextLabel();
            recordLabel.setTextFill(Paint.valueOf(COLORS.getOrDefault(record.getLevel(), DEFAULT_COLOR)));
            recordLabel.setText(getFormatter().format(record));
            recordLabel.maxWidthProperty().bind(cellWidth);
            recordLabel.setWrapText(true);

            items.add(recordLabel);
            logListView.scrollTo(items.size() - 1);
        }
    }

    /**
     * Once reached max logs, use the log records we're removing to stop making new objects
     */
    private Label getNextLabel() {
        Label nextLabel;

        if (items.size() == maxLogs) {
            nextLabel = items.get(0);
            items.remove(0);
        } else {
            nextLabel = new Label();
            nextLabel.setFont(Font.font("monospaced"));
            nextLabel.setWrapText(true);
        }

        return nextLabel;
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
