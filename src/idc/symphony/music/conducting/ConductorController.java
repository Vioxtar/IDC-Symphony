package idc.symphony.music.conducting;

import idc.symphony.db.DBController;
import idc.symphony.music.conducting.commands.Recurrence;
import idc.symphony.music.conducting.commands.shared.ContextExtension;
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

import java.io.File;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller in charge of the conducting process
 *
 */
public class ConductorController extends DBController {
    private static Logger logger = Logger.getLogger("idc.symphony");

    /**
     * Generates song from database.
     * (Main conducting algorithm - core of program)
     */
    private DBConductor createsNewMIDI = new DBConductor()
    {
        {
            ContextExtension context = new ContextExtension();

            addCommand(-3, Recurrence.Sequence, new ContextInit(context));
            addCommand(-2, Recurrence.Sequence, new PrevalenceCalculator(context, 0.6f));
            addCommand(0, Recurrence.Sequence, new DuetMelody());
            addCommand(1, Recurrence.Sequence, new DefaultMelody());
            addCommand(2, Recurrence.Sequence, new PrevalenceBass(context));
            addCommand(3, Recurrence.Sequence, new PrevalenceDuet(context));
            addCommand(4, Recurrence.Sequence, new PrevalenceMelody(context));
            addCommand(5, Recurrence.Sequence, new IntensityRhythm(context));
            addCommand(6, Recurrence.Sequence, new PrevalenceCarpet(context));
            addCommand(7, Recurrence.Sequence, new PrevalenceCarpet(context));
            addCommand(8, Recurrence.Sequence, new PrevalenceCarpet(context));
            addCommand(9, Recurrence.Sequence, new LyricFacultyRoles());
            addCommand(0, Recurrence.EmptyYear, new EmptyRhythm(context));
            addCommand(9, Recurrence.EmptyYear, new LyricFacultyRoles());
            addCommand(10, Recurrence.EmptyYear, new EmptyRemover(4));
            addCommand(0, Recurrence.Event, new LyricEvents());
            addCommand(15, Recurrence.Sequence, new EndingFader(context));
            addCommand(10, Recurrence.End, new LyricSongTermination());
        }
    };

    /**
     * Only creates Metadata messages for visualizer processing
     */
    private DBConductor preparesForExistingMIDI = new DBConductor()
    {
        {
            ContextExtension context = new ContextExtension();

            addCommand(-3, Recurrence.Sequence, new ContextInit(context));
            addCommand(-1, Recurrence.Sequence, new PrevalenceCalculator(context, 0.8f));
            addCommand(0, Recurrence.Sequence, new PrevalenceDuet(context, false));
            addCommand(1, Recurrence.Sequence, new PrevalenceMelody(context, false));
            addCommand(2, Recurrence.Sequence, new IntensityRhythm(context,false));
            addCommand(3, Recurrence.Sequence, new PrevalenceCarpet(context, false));
            addCommand(4, Recurrence.Sequence, new PrevalenceCarpet(context, false));
            addCommand(5, Recurrence.Sequence, new PrevalenceCarpet(context, false));
            addCommand(9, Recurrence.Sequence, new LyricFacultyRoles());
            addCommand(0, Recurrence.EmptyYear, new EmptyRhythm(context, false));
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
                {
                    updateTitle("getFullMIDIAsync");
                }

                @Override
                protected Pattern call() throws Exception {
                    try {
                        return createsNewMIDI.conduct(getConnection(file));
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
                    {
                        updateTitle("getInfoMIDIAsync");
                    }

                    try {
                        Pattern existingSong = MidiFileManager.loadPatternFromMidi(midiFile);
                        return existingSong.add(
                                preparesForExistingMIDI.conduct(getConnection(dbFile)));
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
