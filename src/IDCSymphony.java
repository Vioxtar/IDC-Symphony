import idc.symphony.music.conducting.DBConductor;
import idc.symphony.music.conducting.commands.Recurrence;
import idc.symphony.music.conducting.logging.EventLogger;
import idc.symphony.music.conducting.logging.SequenceLogger;
import idc.symphony.music.conducting.logging.YearLogger;
import idc.symphony.music.conducting.commands.*;
import idc.symphony.music.transformers.visualization.VisualEventsBuilder;
import idc.symphony.music.transformers.visualization.VisualEventFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import idc.symphony.visual.Visualizer;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.sqlite.SQLiteConfig;

import java.sql.SQLException;
import java.util.logging.Logger;

public class IDCSymphony extends Application {
    public static void main(String [] args) {

        launch(args);

    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("IDC Symphony");

        SQLiteConfig sqlConf = new SQLiteConfig();
        sqlConf.setReadOnly(true);

        Logger logger = Logger.getAnonymousLogger();

        DBConductor conductor = new DBConductor();

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

        Pattern song = null;
        try {
            song = conductor.conduct( sqlConf.createConnection("jdbc:sqlite:data/IDC Events.db"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        VisualEventFactory eventManager = new VisualEventFactory(VisualEventFactory.defaultConverters(conductor.getFacultyMap()));
        VisualEventsBuilder eventsBuilder = new VisualEventsBuilder(eventManager);


        //TODO Pleh



        // Start the visualizer
//        Visualizer vis = new Visualizer(eventsBuilder.build(song));
//
//        Player player = new Player();
//        player.delayPlay(0, song);
//
//        vis.start(primaryStage);


    }
}