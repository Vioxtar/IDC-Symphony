package idc.symphony.music.conducting;

import idc.symphony.music.conducting.DBConductor;
import idc.symphony.music.conducting.commands.Recurrence;
import idc.symphony.music.conducting.logging.EventLogger;
import idc.symphony.music.conducting.logging.SequenceLogger;
import idc.symphony.music.conducting.logging.YearLogger;
import idc.symphony.music.conducting.commands.*;
import idc.symphony.ui.cache.CachedResponse;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller in charge of the conducting process
 *
 */
public class ConductorController {
    private static Logger logger = Logger.getLogger("idc.symphony");
    private static SQLiteConfig SQL_CONF = new SQLiteConfig();
    static {
        SQL_CONF.setReadOnly(true);
    }

    /**
     * Generates song from database.
     * (Main conducting algorithm - core of program)
     */
    private DBConductor createsNewMIDI = new DBConductor()
    {
        {
            addCommand(0, Recurrence.Sequence, new DuetMelody());
            addCommand(1, Recurrence.Sequence, new DefaultMelody());
            addCommand(2, Recurrence.Sequence, new DefaultRhythm());
            addCommand(3, Recurrence.Sequence, new DefaultCarpet());
            addCommand(4, Recurrence.Sequence, new DefaultCarpet());
            addCommand(5, Recurrence.Sequence, new DefaultCarpet());
            addCommand(9, Recurrence.Sequence, new LyricFacultyRoles());
            addCommand(0, Recurrence.EmptyYear, new EmptyRhythm());
            addCommand(9, Recurrence.EmptyYear, new LyricFacultyRoles());
            addCommand(0, Recurrence.Event, new LyricEvents());
        }
    };

    /**
     * Only creates Metadata messages for visualizer processing
     */
    private DBConductor preparesForExistingMIDI = new DBConductor()
    {
        {
            addCommand(0, Recurrence.Sequence, new DuetMelody(false));
            addCommand(1, Recurrence.Sequence, new DefaultMelody(false));
            addCommand(2, Recurrence.Sequence, new DefaultRhythm(false));
            addCommand(3, Recurrence.Sequence, new DefaultCarpet(false));
            addCommand(4, Recurrence.Sequence, new DefaultCarpet(false));
            addCommand(5, Recurrence.Sequence, new DefaultCarpet(false));
            addCommand(9, Recurrence.Sequence, new LyricFacultyRoles());
            addCommand(0, Recurrence.EmptyYear, new EmptyRhythm(false));
            addCommand(9, Recurrence.EmptyYear, new LyricFacultyRoles());
            addCommand(0, Recurrence.Event, new LyricEvents());
        }
    };

    private BooleanProperty conductingProperty =
            new SimpleBooleanProperty(this, "MIDI conversion in progress", false);

    private CachedResponse<Pattern> conductedPattern = new CachedResponse<>();

    /**
     * Property for returning
     * @return
     */
    public BooleanProperty conductingProperty() {
        return conductingProperty;
    }

    /**
     * Cached pattern result for tasks
     * Some use-cases may result in unnecessary regeneration of the exact same pattern.
     *
     * This cache may be used to prevent such unnecessary processes.
     *
     * @return
     */
    public CachedResponse<Pattern> patternCache() {
        return conductedPattern;
    }

    /**
     * Generates a task that creates a musical MIDI pattern
     * Musificies database, can be sent directly into visualization.
     *
     * @param file     Database file to generate pattern from
     * @param callback Called when finished with resulting pattern
     */
    public void getFullMIDIAsync(File file, Consumer<Pattern> callback) {
        if (!conductingProperty.get()) {
            Task<Pattern> task = new Task<Pattern>() {
                @Override
                protected Pattern call() throws Exception {
                    try {
                        return createsNewMIDI.conduct(SQL_CONF.createConnection(fileToSQLURI(file)));
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.getMessage(), ex);
                        throw ex;
                    } finally {
                        Platform.runLater(() -> conductingProperty.set(false));
                    }
                }
            };

            conductingProperty.set(true);
            launchTask(task, callback);
        }

    }

    /**
     * Generates a task that creates an info MIDI pattern that does not contain musical data.
     * Purely for visualization, can be attached to pattern extracted from existing MIDI.
     *
     * @param dbFile   Database file to generate pattern from
     * @param midiFile Existing MIDI to draw musical information from
     * @param callback Called when finished with resulting pattern
     */
    public void getInfoMIDIAsync(File dbFile, File midiFile, Consumer<Pattern> callback) {
        if (!conductingProperty.get()) {
            Task<Pattern> task = new Task<Pattern>() {
                @Override
                protected Pattern call() throws Exception {
                    try {
                        Pattern existingSong = MidiFileManager.loadPatternFromMidi(midiFile);
                        return existingSong.add(
                                preparesForExistingMIDI.conduct(SQL_CONF.createConnection(fileToSQLURI(dbFile))));
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.getMessage(), ex);
                        throw ex;
                    } finally {
                        Platform.runLater(() -> conductingProperty.set(false));
                    }
                }
            };


            conductingProperty.set(true);
            launchTask(task, callback);
        }
    }

    /**
     * Saves given pattern into a MIDI file.
     * Callback is called when done to determine success or failure.
     *
     * @param file     Target MIDI File
     * @param pattern  Source pattern
     * @param callback Called when finished
     */
    public void saveMIDI(File file, Pattern pattern, Consumer<Boolean> callback) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override public Boolean call() throws Exception {
                try {
                    MidiFileManager.savePatternToMidi(pattern, file);
                    return true;
                } catch (Exception ex) {
                    Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
                    throw ex;
                }
            }
        };

        launchTask(task, callback);
    }

    /**
     * JDBC Local SQLite URI translator
     * @param file Local SQLite file
     * @return JDBC URI
     */
    private String fileToSQLURI(File file) {
        return String.format("jdbc:sqlite:%s", file.getPath());
    }

    /**
     * Generic task launcher
     * Creates a daemon background thread to execute given task.
     *
     * @param task     Task to launch
     * @param callback Task return value receiving callback
     * @param <T>      Task return type
     */
    private <T> void launchTask(Task<T> task, Consumer<T> callback) {
        task.setOnSucceeded((state) -> callback.accept(task.getValue()));
        task.setOnFailed((state) -> callback.accept(null));
        task.setOnCancelled((state) -> callback.accept(null));

        Thread thread = new Thread();
        thread.setDaemon(true);
        thread.start();
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

        addLoggingCommands(createsNewMIDI, logger);
        addLoggingCommands(preparesForExistingMIDI, logger);
        attachedLogger = logger;
    }

    /**
     * Adds progress info logging commands into conductor strategies
     * @param conductor Conductor strategy
     * @param logger    Logger the logging commands will use for logging
     */
    private void addLoggingCommands(DBConductor conductor, Logger logger) {
        conductor.addCommand(Integer.MAX_VALUE, Recurrence.Year, new YearLogger(logger));
        conductor.addCommand(Integer.MAX_VALUE, Recurrence.Sequence, new SequenceLogger(logger));
        conductor.addCommand(Integer.MAX_VALUE, Recurrence.EmptyYear, new SequenceLogger(logger));
        conductor.addCommand(Integer.MAX_VALUE, Recurrence.Event, new EventLogger(logger));
    }

    private Logger attachedLogger = null;
}
