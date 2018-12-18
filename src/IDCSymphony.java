import composition.Composer;
import composition.DBCompositionBuilder;
import composition.variables.ConstantVariable;
import composition.variables.Variable;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.TrackTable;
import org.jfugue.player.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;


public class IDCSymphony {
    PatternLibrary patternLibrary = new PatternLibrary();

    public static void main(String [] args) throws ClassNotFoundException {
        IDCSymphony symphony = new IDCSymphony();
        symphony.composerTest();
    }

    public void composerTest() {
        File dir = new File("usableplaceholderpatterns");

        Composer composer = new Composer();

        String[] goodNames = patternLibrary.loadPatternsFromFile(dir);

        for (int i = 0; i < goodNames.length; i++) {
            composer.addSequence(patternLibrary.getPattern(goodNames[i]), 60, 0, 0.5f, 0f + 5 * i);
        }

        composer.compose()
                .print()
                .play();
    }

    public void connectionTest() {

        // Connect to database and perform a test read
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:data/IDC Events.db");
            PreparedStatement statement = connection.prepareStatement("select * from events;");

            DBCompositionBuilder compBuilder = new DBCompositionBuilder(connection);
            compBuilder.getContext().setVariable("Hello", "World");
            compBuilder.pushQuery(statement);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}