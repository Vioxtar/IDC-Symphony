package idc.symphony.music;

import idc.symphony.db.EventsQueries;
import idc.symphony.db.FacultyStatsFactory;
import idc.symphony.music.band.Band;
import idc.symphony.stats.FacultyStat;
import idc.symphony.stats.YearStats;
import idc.symphony.db.YearStatsFactory;
import org.jfugue.pattern.Pattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Iterates over Event database, deciding Bolero structure (sequence counts, lengths) and
 * roles for each faculty in every sequence according to event database.
 */
public class DBConductor {
    public static final int DEFAULT_TEMPO = 110;
    public static final float DEFAULT_WHOLES_PER_SEQUENCE = 4;
    public static final float DEFAULT_WHOLES = 1;

    // DB Fields
    private Connection dbConnection;

    // Composition Data
    Band band;

    private int tempo = DEFAULT_TEMPO;
    private float wholesPerSequence = DEFAULT_WHOLES_PER_SEQUENCE;
    private float wholesPerEmptyYear = DEFAULT_WHOLES;



    public DBConductor(Connection dbConnection) {

        this.dbConnection = dbConnection;
        this.band = new Band();
    }

    public Pattern conduct() throws SQLException {
        ConductorState state = new ConductorState();
        state.structure = prepareBoleroStructure();
        state.facultyStats = generateFacultyStats();
        state.composition = new Composition(calcTrackLengths(state.structure), tempo);

        PreparedStatement eventStatement = dbConnection.prepareStatement(EventsQueries.SELECT_EVENTS);
        ResultSet EventSet = eventStatement.executeQuery();
        while (EventSet.next()) {
            int eventYear = EventSet.getInt(EventsQueries.YEAR);
            if (startOfNewSequence(state, eventYear)) {
                finishCurrentSequence(state, eventYear);
            }

            int eventFaculty = EventSet.getInt(EventsQueries.FACULTY);
            if (state.facultyStats.containsKey(eventFaculty)) {
                FacultyStat stats = state.facultyStats.get(eventFaculty);
                stats.sequenceOccurences++;
                stats.totalOccurences++;
                stats.yearOccurences++;
            }


            if (EventSet.isLast()) {
                finishCurrentSequence(state, eventYear);
            }
        }

        return state.composition.getFinalComposition();
    }

    private void finishCurrentSequence(ConductorState state, int eventYear) {
        if (state.eventsPerSequence != null) {



        }

        state.yearSequence++;
        state.facultyStats.forEach((id, stat) -> stat.sequenceOccurences = 0);

        if (state.year != eventYear) {
            state.year = eventYear;
            state.facultyStats.forEach((id, stat) -> stat.yearOccurences = 0);
            state.yearSequence = 0;
            state.eventsPerSequence = state.structure.eventsPerSequence(eventYear);
        }
    }

    private BandRoles decideOnRoles(ConductorState state) {
        Map<Integer, FacultyStat> statsMapCopy = new HashMap<>(state.facultyStats);
        PriorityQueue<FacultyStat> topSequence = new PriorityQueue<>(FacultyStat.SEQ_COMPARATOR);
        PriorityQueue<FacultyStat> topTotal = new PriorityQueue<>(FacultyStat.TOTAL_COMPARATOR);
        BandRoles roles = new BandRoles();

        topSequence.addAll(statsMapCopy.values());

        if (!topSequence.isEmpty()) {
            FacultyStat melody = topSequence.poll();
            if (melody.sequenceOccurences != 0) {
                roles.melodyID = melody.facultyID;
                statsMapCopy.remove(melody.facultyID);
            }
        }

        if (!topSequence.isEmpty()) {
            FacultyStat secondary = topSequence.poll();
            if (secondary.sequenceOccurences != 0) {
                roles.secondaryID = secondary.facultyID;
                statsMapCopy.remove(secondary.facultyID);
            }
        }

        topTotal.addAll(statsMapCopy.values());
        if (!topTotal.isEmpty()) {
            FacultyStat carpet1 = topTotal.poll();
            if (carpet1.totalOccurences != 0) {
                roles.carpet3ID = carpet1.facultyID;
            }
        }

        if (!topTotal.isEmpty()) {
            FacultyStat carpet2 = topTotal.poll();
            if (carpet2.totalOccurences != 0) {
                roles.carpet3ID = carpet2.facultyID;
            }
        }

        if (!topTotal.isEmpty()) {
            FacultyStat carpet3 = topTotal.poll();
            if (carpet3.totalOccurences != 0) {
                roles.carpet3ID = carpet3.facultyID;
            }
        }

        return roles;
    }


    private float[] calcTrackLengths(EventsBoleroStructure structure) {
        ArrayList<Float> trackLengths =
                new ArrayList<>(structure.getMaxYear() - structure.getMinYear());

        for (int year = structure.getMinYear(); year < structure.getMaxYear(); year++) {
            int[] eventsPerSeq = structure.eventsPerSequence(year);

            if (eventsPerSeq.length > 0) {
                for (int i = 0; i < eventsPerSeq.length; i++) {
                    trackLengths.add(wholesPerSequence);
                }
            } else {
                if (wholesPerEmptyYear > 0) {
                    trackLengths.add(wholesPerEmptyYear);
                }
            }
        }

        float[] trackLengthsArr = new float[trackLengths.size()];
        for (int i = 0; i < trackLengthsArr.length; i++) {
            trackLengthsArr[i] = trackLengths.get(i);
        }

        return trackLengthsArr;
    }

    private boolean startOfNewSequence(ConductorState state, int eventYear) {
        if (state.eventsPerSequence == null) {
            return true;
        }

        if (state.eventsInSequence >= state.eventsPerSequence[state.yearSequence]) {
            return true;
        }

        if (state.year != eventYear) {
            return true;
        }

        return false;
    }

    private EventsBoleroStructure prepareBoleroStructure() throws SQLException {
        try {
            YearStats stats = YearStatsFactory.fromDB(dbConnection, band.bandMembers().keySet());
            return new EventsBoleroStructure(stats);
        } catch (SQLException exception) {
            throw new SQLException("While creating EventsBoleroStructure", exception);
        }
    }

    private Map<Integer, FacultyStat> generateFacultyStats() throws SQLException {
        Map<Integer, FacultyStat> facultyStatMap;
        try {
            facultyStatMap = FacultyStatsFactory.fromDB(dbConnection);
        } catch(SQLException exception) {
            throw new SQLException("While generating FacultyStats from DB", exception);
        }

        Iterator<Integer> facultyIDs = facultyStatMap.keySet().iterator();
        Set<Integer> supportedIDs = band.bandMembers().keySet();

        while (facultyIDs.hasNext()) {
            if (!supportedIDs.contains(facultyIDs.next())) {
                facultyIDs.remove();
            }

        }

        return facultyStatMap;
    }

    private class BandRoles {
        public int melodyID = -1;
        public int secondaryID =- 1;
        public int carpet1ID = -1;
        public int carpet2ID = -1;
        public int carpet3ID = -1;
        public int rhythmID = -1;
    }

    private class ConductorState {
        Composition composition;
        EventsBoleroStructure structure;
        Map<Integer, FacultyStat> facultyStats;

        int year = -1;
        int yearSequence = 0;
        int totalSequence = 0;
        int eventsInSequence = 0;
        int[] eventsPerSequence = null;
    }
}
