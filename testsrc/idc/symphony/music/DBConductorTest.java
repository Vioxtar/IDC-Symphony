package idc.symphony.music;

import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.DBConductor;
import idc.symphony.music.conducting.commands.Recurrence;
import idc.symphony.music.conducting.logging.EventLogger;
import idc.symphony.music.conducting.logging.SequenceLogger;
import idc.symphony.music.conducting.logging.YearLogger;
import idc.symphony.music.conducting.commands.*;
import idc.symphony.music.transformers.ComputeSongDuration;
import idc.symphony.visual.parsing.VisualEventsBuilder;
import idc.symphony.visual.parsing.VisualEventFactory;
import idc.symphony.visual.scheduling.NotePlayed;
import idc.symphony.visual.scheduling.VisualEvent;
import org.jfugue.pattern.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Deque;
import java.util.logging.Logger;

class DBConductorTest {
    static Connection dbConnection;
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
            addCommand(0, Recurrence.End, new DebugSectionLengths());
        }
    };

    @BeforeAll
    static void connectDB() throws SQLException {
        SQLiteConfig sqlConf = new SQLiteConfig();
        sqlConf.setReadOnly(true);

        dbConnection = sqlConf.createConnection("jdbc:sqlite:data/IDC Events.db");
    }

    @Test
    void testConducting() throws SQLException {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%5$s%6$s");
        Logger logger = Logger.getAnonymousLogger();

        long before = System.currentTimeMillis();
        System.out.println(String.format("Connection time: %f", (System.currentTimeMillis() - before) / 1000.0));
        before = System.currentTimeMillis();

        System.out.println(String.format("Conductor config time: %f", (System.currentTimeMillis() - before) / 1000.0));
        before = System.currentTimeMillis();
        Pattern song = createsNewMIDI.conduct(dbConnection);
        System.out.println(String.format("Conducting time: %f", (System.currentTimeMillis() - before) / 1000.0));
    }

    @Test
    void testEvents() throws SQLException {
        Pattern song = createsNewMIDI.conduct(dbConnection);
        long before = System.currentTimeMillis();
        VisualEventFactory eventManager = new VisualEventFactory(
                VisualEventFactory.defaultConverters(createsNewMIDI.getFacultyMap()));
        VisualEventsBuilder eventsBuilder = new VisualEventsBuilder(eventManager);
        System.out.println(String.format("Events init time: %f", (System.currentTimeMillis() - before) / 1000.0));

        before = System.currentTimeMillis();
        Deque<VisualEvent> scheduledEvents = (Deque<VisualEvent>)eventsBuilder.build(song);
        System.out.println(String.format("Events build time: %f", (System.currentTimeMillis() - before) / 1000.0));
        System.out.println(scheduledEvents.size());
    }

    private static class DebugSectionLengths implements Command {
        @Override
        public boolean execute(ConductorState state, Recurrence recurrence) {
            for (int section = 0; section < state.getComposition().getNumSections(); section++) {
                for (int track = 0; track < state.getComposition().getNumTracks(); track++) {
                    double duration = ComputeSongDuration.getDuration(state.getComposition().getPattern(track, section));

                    if (duration > 0) {
                        System.out.println(String.format("Section: %d, Track: %d, Duration: %f", section, track, duration));
                    }
                }
            }

            return false;
        }
    }
}