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

    public static void main(String [] args) throws ClassNotFoundException {
        composerTest();
    }

    public static void composerTest() {
        File dir = new File("usableplaceholderpatterns");

        Composer composer = new Composer();

        String[] goodNames = composer.loadPatternsFromFile(dir);

        for (int i = 0; i < goodNames.length; i++) {
            composer.addSequence(goodNames[i], 60, 0, 0.5f, 0f + 5 * i);
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

    public void tomersDoomSong() {

        // I don't know why I composed this

         Player player = new Player();
         Pattern fin = new Pattern("KEY:Gminor");
         Pattern p1 = new Pattern("V0 #YES1 I[CHOIR_AAHS] C4 D4 E4 F4 E4 D4 | C4 D4 E4 F4 E4 D4").addToEachNoteToken("a60")
                 .add(new Pattern("V2 @#YES1 I[VIOLIN] C3 R R D3 R R | C3 R G3 D3 R R").addToEachNoteToken("a40"));
         fin.add(p1, 2);
         Pattern p2 = new Pattern("V0 #YES1 I[CHOIR_AAHS] C4 D4 E4 F4 E4 D4 | #YES2 C4 D4 E4 F4 E4 D4").addToEachNoteToken("a50")
                 .add(new Pattern("V1 @#YES1 I[PIZZICATO_STRINGS] E C D B4i Ci D E | @#YES2 R R F Ei Ci A4 C").addToEachNoteToken("a72"))
                 .add(new Pattern("V2 @#YES1 I[VIOLIN] C3 R R D3 R R | @#YES2 C3 R G3 D3 R R").addToEachNoteToken("a40"));
         fin.add(p2, 2);
         player.play(fin);
    }
}