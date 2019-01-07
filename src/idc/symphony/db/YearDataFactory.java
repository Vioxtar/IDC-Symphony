package idc.symphony.db;

import idc.symphony.data.YearData;
import idc.symphony.data.YearCollection;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *  Creates year-based statistical event data, filtered by supported faculties
 *  TODO: Convert YearDataFactory into a single-row SQL query using joined tables and variable-length IN
 */
public class YearDataFactory {
    private static final String SELECT_YEAR_ORDERED_EVENTS =
            "SELECT EventYear as \"Year\", EventFaculty as \"Faculty\", EventType as \"Type\"\n" +
            "FROM Events\n" +
            "ORDER BY \"Year\"";

    /**
     * Generates Year-based event statistics, only taking into account supported faculties.
     */
    public static YearCollection fromDB(Connection connection, Set<Integer> supportedFaculties) throws SQLException {
        int totalEvents = 0, totalFaculties = 0, totalTypes = 0;
        HashMap<Integer,YearData> yearStats = new HashMap<>();

        PreparedStatement eventsDistinctFacultyType = connection.prepareStatement(SELECT_YEAR_ORDERED_EVENTS);
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

            if (supportedFaculties == null || supportedFaculties.contains(eventFaculty)) {
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
}
