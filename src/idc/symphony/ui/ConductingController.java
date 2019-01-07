package idc.symphony.ui;

import idc.symphony.music.DBConductor;
import idc.symphony.music.conducting.Command;
import idc.symphony.music.conducting.Recurrence;
import idc.symphony.music.conducting.logging.EventLogger;
import idc.symphony.music.conducting.logging.SequenceLogger;
import idc.symphony.music.conducting.logging.YearLogger;
import idc.symphony.music.conducting.rules.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import org.jfugue.pattern.Pattern;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller in charge of the conducting process
 */
public class ConductingController {
    private static SQLiteConfig SQL_CONF = new SQLiteConfig();
    static {
        SQL_CONF.setReadOnly(true);
    }

    private DBConductor createsNewMIDI = new DBConductor();
    {
        createsNewMIDI.addCommand(0, Recurrence.Sequence, new DuetMelody());
        createsNewMIDI.addCommand(1, Recurrence.Sequence, new DefaultMelody());
        createsNewMIDI.addCommand(2, Recurrence.Sequence, new DefaultRhythm());
        createsNewMIDI.addCommand(3, Recurrence.Sequence, new DefaultCarpet());
        createsNewMIDI.addCommand(4, Recurrence.Sequence, new DefaultCarpet());
        createsNewMIDI.addCommand(5, Recurrence.Sequence, new DefaultCarpet());
        createsNewMIDI.addCommand(9, Recurrence.Sequence, new LyricFacultyRoles());
        createsNewMIDI.addCommand(0, Recurrence.EmptyYear, new EmptyRhythm());
        createsNewMIDI.addCommand(9, Recurrence.EmptyYear, new LyricFacultyRoles());
        createsNewMIDI.addCommand(0, Recurrence.Event, new LyricEvents());
    }

    private DBConductor preparesForExistingMIDI = new DBConductor();
    {
        preparesForExistingMIDI.addCommand(0, Recurrence.Sequence, new DuetMelody(false));
        preparesForExistingMIDI.addCommand(1, Recurrence.Sequence, new DefaultMelody(false));
        preparesForExistingMIDI.addCommand(2, Recurrence.Sequence, new DefaultRhythm(false));
        preparesForExistingMIDI.addCommand(3, Recurrence.Sequence, new DefaultCarpet(false));
        preparesForExistingMIDI.addCommand(4, Recurrence.Sequence, new DefaultCarpet(false));
        preparesForExistingMIDI.addCommand(5, Recurrence.Sequence, new DefaultCarpet(false));
        preparesForExistingMIDI.addCommand(9, Recurrence.Sequence, new LyricFacultyRoles());
        preparesForExistingMIDI.addCommand(0, Recurrence.EmptyYear, new EmptyRhythm(false));
        preparesForExistingMIDI.addCommand(9, Recurrence.EmptyYear, new LyricFacultyRoles());
        preparesForExistingMIDI.addCommand(0, Recurrence.Event, new LyricEvents());
    }

    private BooleanProperty conductingProperty =
            new SimpleBooleanProperty(this, "MIDI conversion in progress", false);

    public BooleanProperty conductingProperty() {
        return conductingProperty;
    }

    public void getFullMIDIAsync(File file, Consumer<Pattern> callback) {
        Task<Pattern> task = new Task<Pattern>() {
            @Override
            protected Pattern call() throws Exception {
                try {
                    return createsNewMIDI.conduct(SQL_CONF.createConnection(fileToSQLURI(file)));
                } catch (Exception ex) {
                    Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
                    throw ex;
                } finally {
                    Platform.runLater(() -> conductingProperty.set(false));
                }
            }
        };

        setTaskCallback(task, callback);

        conductingProperty.set(true);
        Thread conducting = new Thread(task);
        conducting.setDaemon(true);
        conducting.start();
    }

    public void getInfoMIDIAsync(File file, Consumer<Pattern> callback) {
        Task<Pattern> task = new Task<Pattern>() {
            @Override
            protected Pattern call() throws Exception {
                try {
                    return preparesForExistingMIDI.conduct(SQL_CONF.createConnection(fileToSQLURI(file)));
                } catch (Exception ex) {
                    Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
                    throw ex;
                } finally {
                    Platform.runLater(() -> conductingProperty.set(false));
                }
            }
        };

        setTaskCallback(task, callback);

        conductingProperty.set(true);
        Thread conducting = new Thread(task);
        conducting.setDaemon(true);
        conducting.start();
    }

    private String fileToSQLURI(File file) {
        return String.format("jdbc:sqlite:%s", file.getPath());
    }

    private void setTaskCallback(Task<Pattern> task, Consumer<Pattern> callback) {
        task.setOnSucceeded((state) -> callback.accept(task.getValue()));
        task.setOnFailed((state) -> callback.accept(null));
        task.setOnCancelled((state) -> callback.accept(null));
    }


    /**
     * Attaches given logger to conducting process, to provide state information about conducting progress
     * to the user.
     * @param logger Application logger
     */
    public void attachLogger(Logger logger) {
        if (attachedLogger != null) {
            throw new IllegalStateException("Logger already attached!");
        }

        createsNewMIDI.addCommand(Integer.MAX_VALUE, Recurrence.Year, new YearLogger(logger));
        createsNewMIDI.addCommand(Integer.MAX_VALUE, Recurrence.Sequence, new SequenceLogger(logger));
        createsNewMIDI.addCommand(Integer.MAX_VALUE, Recurrence.EmptyYear, new SequenceLogger(logger));
        createsNewMIDI.addCommand(Integer.MAX_VALUE, Recurrence.Event, new EventLogger(logger));

        preparesForExistingMIDI.addCommand(Integer.MAX_VALUE, Recurrence.Year, new YearLogger(logger));
        preparesForExistingMIDI.addCommand(Integer.MAX_VALUE, Recurrence.Sequence, new SequenceLogger(logger));
        preparesForExistingMIDI.addCommand(Integer.MAX_VALUE, Recurrence.EmptyYear, new SequenceLogger(logger));
        preparesForExistingMIDI.addCommand(Integer.MAX_VALUE, Recurrence.Event, new EventLogger(logger));

        attachedLogger = logger;
    }

    private Logger attachedLogger = null;
}
