package idc.symphony.db;

import idc.symphony.data.YearData;
import idc.symphony.data.YearCollection;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class YearDataFactory {
    private static final String SELECT_EVENTS_PER_YEAR =
            "SELECT\n" +
            "   DISTINCT e1.EventYear" +
            "       as \"Year\",\n" +
            "   (SELECT COUNT(*) FROM Events AS e2 WHERE e2.EventYear = e1.EventYear\n" +
                    "AND e2.EventFaculty IN ?) " +
            "       as NumEvents,\n" +
            "   (SELECT COUNT(*) FROM" +
            "       (SELECT DISTINCT EventFaculty FROM Events AS e2\n" +
                    "WHERE e2.EventYear = e1.EventYear" +
                    "       AND e2.EventFaculty IN ?))" +
            "       as NumFaculties,w\n" +
            "   (SELECT COUNT(*) FROM" +
            "       (SELECT DISTINCT EventType FROM Events AS e2 WHERE e2.EventYear = e1.EventYear" +
                    "   AND e2.EventFaculty IN ?)) " +
            "       AS NumTypes\n" +
            "FROM Events AS e1\n" +
            "GROUP BY EventYear\n" +
            "ORDER BY EventYear";

    private static final String SELECT_EVENTS_DISTINCT_FACULTY_TYPE =
            "SELECT EventYear as \"Year\", EventFaculty as \"Faculty\", EventType as \"Type\"\n" +
            "FROM Events\n" +
            "ORDER BY \"Year\"";

    /**
     * Generates Year-based event statistics, only taking into account accepted faculties.
     */
    public static YearCollection fromDB(Connection connection, Set<Integer> acceptedFaculties) throws SQLException {
        if (acceptedFaculties == null) {
            return fromDBAll(connection);
        }

        int totalEvents = 0, totalFaculties = 0, totalTypes = 0;
        HashMap<Integer,YearData> yearStats = new HashMap<>();

        PreparedStatement eventsDistinctFacultyType = connection.prepareStatement(SELECT_EVENTS_DISTINCT_FACULTY_TYPE);
        ResultSet events = eventsDistinctFacultyType.executeQuery();

        int currentYear = -1;
        int currentEvents = 0;
        Set<Integer> currentFaculties = new HashSet<>();
        Set<Integer> currentTypes = new HashSet<>();

        while (events.next()) {
            int eventYear = events.getInt("Year");
            int eventFaculty = events.getInt("Faculty");
            int eventType = events.getInt("Type");

            if (eventYear != currentYear) {
                if (currentEvents > 0) {
                    totalEvents += currentEvents;
                    totalFaculties += currentFaculties.size();
                    totalTypes += currentTypes.size();

                    yearStats.put(currentYear,
                            new YearData(currentEvents, currentFaculties.size(), currentTypes.size()));
                }

                currentEvents = 0;
                currentFaculties.clear();
                currentTypes.clear();
                currentYear = eventYear;
            }

            if (acceptedFaculties.contains(eventFaculty)) {
                currentEvents++;
                currentFaculties.add(eventFaculty);
                currentTypes.add(eventType);
            }
        }

        if (currentYear != -1) {
            totalEvents += currentEvents;
            totalFaculties += currentFaculties.size();
            totalTypes += currentTypes.size();

            yearStats.put(currentYear,
                    new YearData(currentEvents, currentFaculties.size(), currentTypes.size()));
        }

        YearData total = new YearData(totalEvents, totalFaculties, totalTypes);
        return new YearCollection(yearStats, total);
    }

    public static YearCollection fromDBAll(Connection connection) throws SQLException {
        int totalEvents = 0, totalFaculties = 0, totalTypes = 0;
        HashMap<Integer, YearData> yearStats = new HashMap<>();

        PreparedStatement eventsPerYearStatement = connection.prepareStatement(SELECT_EVENTS_PER_YEAR);
        ResultSet eventsPerYears = eventsPerYearStatement.executeQuery();

        while (eventsPerYears.next()) {
            YearData currYearData = new YearData(
                    eventsPerYears.getInt("NumEvents"),
                    eventsPerYears.getInt("NumFaculties"),
                    eventsPerYears.getInt("NumTypes")
            );

            totalEvents += currYearData.events();
            totalFaculties += currYearData.faculties();
            totalTypes += currYearData.types();

            yearStats.put(
                    eventsPerYears.getInt("Year"),
                    currYearData
            );
        }

        YearData total = new YearData(totalEvents, totalFaculties, totalTypes);
        return new YearCollection(yearStats, total);
    }
}