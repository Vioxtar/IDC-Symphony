package Stats;

import java.util.HashMap;
import java.util.Map;

/**
 *  Per-Year + Total statistics;
 *  Num of Events in Year
 *  Num of Distinct Faculties in Year
 *  Num of Distinct Event Types in Year
 */
public class YearStats {
    public static final YearStat EMPTY_YEAR = new YearStat(0,0, 0);

    private YearStat total;
    private HashMap<Integer, YearStat> yearStatMap;

    public YearStats(
            HashMap<Integer,YearStat> yearStats,
            YearStat total) {
        this.yearStatMap = yearStats;
        this.total = total;
    }

    // Totals
    public int totalEvents() {
        return total.events();
    }
    public int totalFaculties() {
        return total.faculties();
    }
    public int totalTypes() {
        return total.types();
    }

    // Averages
    public float averageEvents() { return (float)total.events() / yearStatMap.size(); }
    public float averageFaculties() { return (float)total.faculties() / yearStatMap.size();}
    public float averageTypes() { return (float)total.types() / yearStatMap.size();}

    // Per year map - getter + indexed getter for simplified access
    public Map<Integer,YearStat> statsMap() { return yearStatMap; }
    public YearStat yearStat(int year) {
        return yearStatMap.getOrDefault(year, EMPTY_YEAR);
    }
}
