package idc.symphony.music;

import idc.symphony.stats.YearStat;
import idc.symphony.stats.YearStats;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
     * Create Bolero Structure with year sequence repetitions
     * based on how interesting that year was compared to the average year.
     * @param stats
     * @throws SQLException
     */
    public EventsBoleroStructure(YearStats stats) throws SQLException {
        eventsPerYearSequence.clear();

        minYear = Integer.MAX_VALUE;
        maxYear = Integer.MIN_VALUE;

        stats.statsMap().forEach((year, yearStat) -> {
            if (year < minYear) {
                minYear = year;
            }

            if (year > maxYear) {
                maxYear = year;
            }

            int numOfSeqs = 1 + (int)(yearRelativeDensity(yearStat, stats) / 3);
            int[] sequenceEvents = new int[numOfSeqs];


            int eventsPerSeq = yearStat.events() / numOfSeqs;
            int remainder = yearStat.events() % numOfSeqs;

            Arrays.setAll(
                    sequenceEvents,
                    (index) -> eventsPerSeq + (index < remainder ? 1 : 0)
            );

            eventsPerYearSequence.put(year, sequenceEvents);
        });
    }

    private float yearRelativeDensity(YearStat yearStat, YearStats stats) {
        return (yearStat.events() * yearStat.faculties() * yearStat.types()) /
                (stats.averageEvents() * stats.averageFaculties() * stats.averageTypes());
    }
}
