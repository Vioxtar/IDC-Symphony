import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.TrackTable;
import org.jfugue.player.Player;

import java.sql.*;


public class IDCSymphony {

    public static void main(String [] args) throws ClassNotFoundException {

        // This is how we'd be able to use the composer:

        // composition.Composer composer = new composition.Composer();
        // composer.loadPatternsFromDirectory(some directory with usable patterns in it);

        // Add a bunch of sequences:
        // composer.addSequence(String patternName, byte instrument, byte voice, float time, short reps)...
        // where patternName is retrieved from the directory of usable patterns
        // (time can be replaced with something more comfortable/descriptive than a float)

        // composer.play();

        // I don't know why I composed this

        // Player player = new Player();
        // Pattern fin = new Pattern("KEY:Gminor");
        // Pattern p1 = new Pattern("V0 #YES1 I[CHOIR_AAHS] C4 D4 E4 F4 E4 D4 | C4 D4 E4 F4 E4 D4").addToEachNoteToken("a60")
        //         .add(new Pattern("V2 @#YES1 I[VIOLIN] C3 R R D3 R R | C3 R G3 D3 R R").addToEachNoteToken("a40"));
        // fin.add(p1, 2);
        // Pattern p2 = new Pattern("V0 #YES1 I[CHOIR_AAHS] C4 D4 E4 F4 E4 D4 | #YES2 C4 D4 E4 F4 E4 D4").addToEachNoteToken("a50")
        //         .add(new Pattern("V1 @#YES1 I[PIZZICATO_STRINGS] E C D B4i Ci D E | @#YES2 R R F Ei Ci A4 C").addToEachNoteToken("a72"))
        //         .add(new Pattern("V2 @#YES1 I[VIOLIN] C3 R R D3 R R | @#YES2 C3 R G3 D3 R R").addToEachNoteToken("a40"));
        // fin.add(p2, 2);
        // player.play(fin);
        

        /*
        // Connect to database and perform a test read
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:data/IDC Events.db");
            Statement statement = connection.createStatement();

            ResultSet bla = statement.executeQuery("select * from events;");
            ResultSetMetaData blaMD = bla.getMetaData();
            while (bla.next()) {
                for (int col = 1; col <= blaMD.getColumnCount(); col++) {
//                    System.out.println(bla.getObject(col));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
    }
}