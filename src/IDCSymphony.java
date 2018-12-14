import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import java.sql.*;


public class IDCSymphony {

    public static void main(String [] args) throws ClassNotFoundException {

        System.out.println(p1 == p2);

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