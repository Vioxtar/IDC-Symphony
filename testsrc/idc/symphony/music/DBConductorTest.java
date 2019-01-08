package idc.symphony.music;

import idc.symphony.music.conducting.DBConductor;
import idc.symphony.music.conducting.commands.Recurrence;
import idc.symphony.music.conducting.logging.EventLogger;
import idc.symphony.music.conducting.logging.SequenceLogger;
import idc.symphony.music.conducting.logging.YearLogger;
import idc.symphony.music.conducting.commands.*;
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
        DBConductor conductor = new DBConductor();
        System.out.println(String.format("Connection time: %f", (System.currentTimeMillis() - before) / 1000.0));
        before = System.currentTimeMillis();
        conductor.addCommand(1000, Recurrence.Year, new YearLogger(logger));

        conductor.addCommand(0, Recurrence.Sequence, new DuetMelody());
        conductor.addCommand(1, Recurrence.Sequence, new DefaultMelody());
        conductor.addCommand(2, Recurrence.Sequence, new DefaultRhythm());
        conductor.addCommand(3, Recurrence.Sequence, new DefaultCarpet());
        conductor.addCommand(4, Recurrence.Sequence, new DefaultCarpet());
        conductor.addCommand(5, Recurrence.Sequence, new DefaultCarpet());
        conductor.addCommand(9, Recurrence.Sequence, new LyricFacultyRoles());
        conductor.addCommand(1000, Recurrence.Sequence, new SequenceLogger(logger));

        conductor.addCommand(0, Recurrence.EmptyYear, new EmptyRhythm());
        conductor.addCommand(9, Recurrence.EmptyYear, new LyricFacultyRoles());
        conductor.addCommand(1000, Recurrence.EmptyYear, new SequenceLogger(logger));

        conductor.addCommand(0, Recurrence.Event, new LyricEvents());
        conductor.addCommand(1000, Recurrence.Event, new EventLogger(logger));
        System.out.println(String.format("Conductor config time: %f", (System.currentTimeMillis() - before) / 1000.0));
        before = System.currentTimeMillis();
        Pattern song = conductor.conduct(dbConnection);
        System.out.println(String.format("Conducting time: %f", (System.currentTimeMillis() - before) / 1000.0));

        before = System.currentTimeMillis();
        VisualEventFactory eventManager = new VisualEventFactory(
            VisualEventFactory.defaultConverters(conductor.getFacultyMap()));
        VisualEventsBuilder eventsBuilder = new VisualEventsBuilder(eventManager);
        System.out.println(String.format("Events init time: %f", (System.currentTimeMillis() - before) / 1000.0));

        before = System.currentTimeMillis();
        Deque<VisualEvent> scheduledEvents = (Deque<VisualEvent>)eventsBuilder.build(song);
        System.out.println(String.format("Events build time: %f", (System.currentTimeMillis() - before) / 1000.0));
        System.out.println(scheduledEvents.size());


        for (VisualEvent event : scheduledEvents) {
            if (event instanceof NotePlayed) {
                NotePlayed played = (NotePlayed) event;

                System.out.println(String.format("@%f for %f, %s",
                        played.time, played.duration, played.faculty.name));
            }
        }


    }
}