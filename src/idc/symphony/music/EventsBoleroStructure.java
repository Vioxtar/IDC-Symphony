package idc.symphony.music;

import idc.symphony.data.YearData;
import idc.symphony.data.YearCollection;

import java.sql.SQLException;
import java.util.*;

/**
 * Generates Bolero structure according to statistical data of events
 */
public class EventsBoleroStructure {
    public static final int[] EMPTY_SEQUENCES = new int[0];
    private Map<Integer, int[]> eventsPerYearSequence = new HashMap<>();
    private int minYear;
    private int maxYear;

    public int totalNumSequences() {
        int[] numSequences = {0};
        eventsPerYearSequence.values().forEach(a -> numSequences[0] += a.length);
        return numSequences[0];
    }

    public int[] eventsPerSequence(int year) {
        return eventsPerYearSequence.getOrDefault(year, EMPTY_SEQUENCES);
    }

    public int getMinYear() {
        return minYear;
    }

    public int getMaxYear() {
        return maxYear;
    }

    /**
     * Create Bolero YearStructure with year sequence repetitions
     * based on how interesting that year was compared to the average year.
     * @param stats
     * @throws SQLException
     */
    public EventsBoleroStructure(YearCollection stats) throws SQLException {
        eventsPerYearSequence.clear();

        minYear = Integer.MAX_VALUE;
        maxYear = Integer.MIN_VALUE;

        stats.statsMap().forEach((year, yearData) -> {
            if (year < minYear) {
                minYear = year;
            }

            if (year > maxYear) {
                maxYear = year;
            }

            int numOfSeqs = 1 + (int)(yearRelativeDensity(yearData, stats) / 3);
            int[] sequenceEvents = new int[numOfSeqs];

            int eventsPerSeq = yearData.events() / numOfSeqs;
            int remainder = yearData.events() % numOfSeqs;

            Arrays.setAll(
                    sequenceEvents,
                    (index) -> eventsPerSeq + (index < remainder ? 1 : 0)
            );

            eventsPerYearSequence.put(year, sequenceEvents);
        });
    }

    private float yearRelativeDensity(YearData yearData, YearCollection stats) {
        return (yearData.events() * yearData.faculties() * yearData.types()) /
                (stats.averageEvents() * stats.averageFaculties() * stats.averageTypes());
    }
}
