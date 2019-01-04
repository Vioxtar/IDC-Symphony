package idc.symphony.data;

import java.util.HashMap;
import java.util.Map;

/**
 *  Per-Year + Total Years Data;
 *  Num of Events in Year
 *  Num of Distinct Faculties in Year
 *  Num of Distinct Event Types in Year
 */
public class YearCollection {
    public static final YearData EMPTY_YEAR = new YearData(0,0, 0);

    private YearData total;
    private HashMap<Integer, YearData> yearStatMap;

    public YearCollection(
            HashMap<Integer,YearData> yearStats,
            YearData total) {
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
    public Map<Integer,YearData> statsMap() { return yearStatMap; }
    public YearData yearStat(int year) {
        return yearStatMap.getOrDefault(year, EMPTY_YEAR);
    }
}
