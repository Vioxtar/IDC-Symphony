import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EventsBoleroStructure {
    // TODO: Turn into one select using window function
    // For some reason, SQLite JDBC does not support window functions even though SQLite does
    //
    private static final String SELECT_EVENTS_PER_YEAR =
            "SELECT COUNT(*) as NumEvents " +
            "FROM Events " +
            "WHERE EventYear = ?";


    private Map<Integer, int[]> eventsPerSequence = new HashMap<>();

    public int[] getEventsPerSequence(int year) {
        return eventsPerSequence.getOrDefault(year, new int[0]);
    }

    public int getTotalNumSequences() {
        int[] numSequences = {0};
        eventsPerSequence.values().forEach(a -> numSequences[0] += a.length);
        return numSequences[0];
    }

    public void buildBolero(Connection dbConnection, float sequenceLength, float minSecPerEvent) throws SQLException {
        eventsPerSequence.clear();
        PreparedStatement eventsPerYearStatement = dbConnection.prepareStatement(SELECT_EVENTS_PER_YEAR);
        for (int year = 1994; year < 2019; year++) {
            eventsPerYearStatement.setInt(1, year);
            ResultSet eventsPerYear = eventsPerYearStatement.executeQuery();

            if (eventsPerYear.next()) {
                int numEvents = eventsPerYear.getInt("NumEvents");
                if (numEvents == 0) continue;

                int[] eventsPerSequence = new int[(int)Math.ceil(numEvents * minSecPerEvent / sequenceLength)];

                // Events Per Seq = Num Events / Num of Sequences
                int eventsPerSeq = numEvents / eventsPerSequence.length;
                int remainder = numEvents % eventsPerSequence.length;

                for (int i = 0; i < eventsPerSequence.length; i++) {
                    eventsPerSequence[i] = eventsPerSeq;

                    if (remainder > 0) {
                        eventsPerSequence[i]++;
                        remainder--;
                    }
                }

                this.eventsPerSequence.put(year, eventsPerSequence);
            }
        }
    }

}
