import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import java.sql.*;


public class IDCSymphony {

    public static void main(String [] args) throws ClassNotFoundException {

        // This is how we'd be able to use the composer:

        // Composer composer = new Composer();
        // composer.loadPatternsFromDirectory(some directory with usable patterns in it);

        // Add a bunch of sequences:
        // composer.addSequence(String patternName, byte instrument, byte voice, float time, short reps)...
        // where patternName is retrieved from the directory of usable patterns
        // (time can be replaced with something more comfortable/descriptive than a float)

        // composer.play();

        


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
    }
}