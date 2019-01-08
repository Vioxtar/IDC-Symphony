package idc.symphony.visual;

import idc.symphony.data.FacultyData;
import idc.symphony.db.DBController;
import idc.symphony.db.FacultyDataFactory;
import idc.symphony.music.transformers.BPMExtractor;
import idc.symphony.visual.parsing.VisualEventFactory;
import idc.symphony.visual.parsing.VisualEventsBuilder;
import idc.symphony.visual.scheduling.VisualEvent;
import javafx.concurrent.Task;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.player.SequencerManager;

import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.sql.Connection;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls the visualization process of a pattern
 */
public class VisualizerController extends DBController {
    private static Logger logger = Logger.getLogger("idc.symphony");
    private Player patternPlayer;

    /**
     * Builds a stream of visual events from a given pattern using database information
     *
     * @param dbFile   Database to get faculty data from
     * @param song     Song to visualize
     * @param callback Visualizer events
     */
    public void buildAsync(File dbFile, Pattern song, Consumer<Queue<VisualEvent>> callback) {
        Task<Queue<VisualEvent>> task = new Task<Queue<VisualEvent>>() {
            {
                updateTitle("buildAsync");
            }

            @Override
            protected Queue<VisualEvent> call() throws Exception {
                try {
                    Connection connection = getConnection(dbFile);
                    Map<Integer, FacultyData> facultyMap = FacultyDataFactory.fromDB(connection);

                    VisualEventFactory factory =
                            new VisualEventFactory(VisualEventFactory.defaultConverters(facultyMap));
                    VisualEventsBuilder builder = new VisualEventsBuilder(factory);
                    return builder.build(song);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                    throw ex;
                }
            }
        };
        launchTask(task, callback);
    }

    /**
     * Play song asynchronously
     * @param pattern Song to play
     */
    public void playPattern(Pattern pattern) {
        explicitlySetBPM(pattern);
        stopPlaying();
        patternPlayer = new Player();
        patternPlayer.delayPlay(0, pattern);
    }

    /**
     * Sets MIDI Sequencer's tempo explicitly
     * Sequencer has a strange bug where replaying the same pattern causes it to initialize with an incorrect tempo.
     * @param pattern Pattern to draw tempo from
     */
    private void explicitlySetBPM(Pattern pattern) {
        try {
            int bpm = BPMExtractor.extract(pattern);

            if (bpm > 0) {
                SequencerManager.getInstance().getSequencer().setTempoInBPM(BPMExtractor.extract(pattern));
            }
        } catch (MidiUnavailableException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Stop playing song
     */
    public void stopPlaying() {
        if (patternPlayer != null) {
            patternPlayer.getManagedPlayer().reset();
        }
    }
}

