package idc.symphony.db;

import idc.symphony.stats.YearStat;
import idc.symphony.stats.YearStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class YearStatsFactory {
    private static final String SELECT_EVENTS_PER_YEAR =
            "SELECT\n" +
            "   DISTINCT e1.EventYear" +
            "       as \"Year\",\n" +
            "   (SELECT COUNT(*) FROM Events AS e2 WHERE e2.EventYear = e1.EventYear)" +
            "       as NumEvents,\n" +
            "   (SELECT COUNT(*) FROM" +
            "       (SELECT DISTINCT EventFaculty FROM Events AS e2 WHERE e2.EventYear = e1.EventYear))" +
            "       as NumFaculties,\n" +
            "   (SELECT COUNT(*) FROM" +
            "       (SELECT DISTINCT EventType FROM Events AS e2 WHERE e2.EventYear = e1.EventYear))" +
            "       as NumTypes\n" +
            "FROM Events AS e1\n" +
            "GROUP BY EventYear\n";

    public static YearStats fromDB(Connection connection) throws SQLException {
        int totalEvents = 0, totalFaculties = 0, totalTypes = 0, yearCount = 0;
        HashMap<Integer, YearStat> yearStats = new HashMap<Integer,YearStat>();

        PreparedStatement eventsPerYearStatement = connection.prepareStatement(SELECT_EVENTS_PER_YEAR);
        ResultSet eventsPerYears = eventsPerYearStatement.executeQuery();

        while (eventsPerYears.next()) {
            YearStat currYearStat = new YearStat(
                    eventsPerYears.getInt("NumEvents"),
                    eventsPerYears.getInt("NumFaculties"),
                    eventsPerYears.getInt("NumTypes")
            );

            totalEvents += currYearStat.events();
            totalFaculties += currYearStat.faculties();
            totalTypes += currYearStat.types();
            yearCount++;

            yearStats.put(
                    eventsPerYears.getInt("Year"),
                    currYearStat
            );
        }

        YearStat total = new YearStat(totalEvents, totalFaculties, totalTypes);

        return new YearStats(
                yearStats,
                total
        );
    }
}
