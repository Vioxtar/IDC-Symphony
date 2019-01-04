package idc.symphony.music;

import idc.symphony.data.FacultyData;
import idc.symphony.music.conducting.Command;
import idc.symphony.music.conducting.Recurrence;
import idc.symphony.music.conducting.logging.EventLogger;
import idc.symphony.music.conducting.logging.SequenceLogger;
import idc.symphony.music.conducting.logging.YearLogger;
import idc.symphony.music.conducting.rules.*;
import idc.symphony.music.transformers.visualization.VisualEventsBuilder;
import idc.symphony.music.transformers.visualization.factory.VisualEventManager;
import org.jfugue.pattern.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
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
                "%5$s%6$s%n");
        Logger logger = Logger.getAnonymousLogger();

        DBConductor conductor = new DBConductor(dbConnection);

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

        Pattern song = conductor.conduct();

        VisualEventManager eventManager = new VisualEventManager(
            VisualEventManager.defaultConverters(conductor.getFacultyMap()));
        VisualEventsBuilder eventsBuilder = new VisualEventsBuilder(eventManager);

        System.out.println(eventsBuilder.build(song).size());
    }
}