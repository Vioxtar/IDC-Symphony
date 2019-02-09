package idc.symphony.music.conducting;

import idc.symphony.data.EventData;
import idc.symphony.data.EventType;
import idc.symphony.music.band.Band;
import idc.symphony.data.FacultyData;
import idc.symphony.music.band.BandRole;
import org.jfugue.theory.Key;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Mutable Conductor State
 * Used for creating final composition via procedurally changing event statistics
 */
public class ConductorState {
    static final int DEFAULT_TEMPO = 110;
    static final int DEFAULT_WHOLES_PER_SEQUENCE = 4;
    static final int DEFAULT_WHOLES_PER_EMPTY = 1;
    static final Key DEFAULT_SONG_KEY = new Key("G");

    /**
     * Conductor-decided length of normal sequence and empty year sequence.
     */
    int wholesPerSequence = DEFAULT_WHOLES_PER_SEQUENCE;
    int wholesPerEmptyYear = DEFAULT_WHOLES_PER_EMPTY;
    Key songKey = DEFAULT_SONG_KEY;

    /**
     * Underlying Conductor
     */
    DBConductor conductor;
    Connection connection;

    /**
     * Band facade for per-faculty music generation strategies
     */
    Band band;

    /**
     * Composition
     */
    int tempo = DEFAULT_TEMPO;
    Composition composition;


    /**
     * Iterative Composition YearStructure for DBConductor
     */
    EventsBoleroStructure structure;


    /**
     * Database data fields
     */
    Map<Integer, EventType> eventTypes;
    Map<Integer, FacultyData> faculties;
    FacultyContext sequenceContext;
    FacultyContext yearContext;

    /**
     * Last event processed / event currently being processed
     */
    EventData currentEvent = null;

    /**
     *  Sequence being processed
     */
    boolean emptyStreak = false;
    int currentSequence = 0;
    int currentYear = 0;


    /**
     * Mutable Conductor State
     * @param conductor the conductor this state is a state of
     */
    ConductorState(DBConductor conductor,
                   Band band,
                   EventsBoleroStructure structure,
                   Map<Integer, EventType> eventTypes,
                   Map<Integer, FacultyData> facultyData) {

        this.conductor = conductor;
        this.band = band;
        this.structure = structure;
        this.eventTypes = eventTypes;
        this.faculties = facultyData;
        this.composition = new Composition(calcTrackLengths());
        yearContext = new FacultyContext();
        sequenceContext = new FacultyContext(yearContext);
    }

    private int[] calcTrackLengths() {
        ArrayList<Integer> trackLengths =
                new ArrayList<>(structure.getMaxYear() - structure.getMinYear());

        boolean emptyStreak = false;

        for (int year = structure.getMinYear() - 1; year <= structure.getMaxYear() + 1; year++) {
            int[] eventsPerSeq = structure.eventsPerSequence(year);

            if (eventsPerSeq.length > 0) {
                emptyStreak = false;
                for (int i = 0; i < eventsPerSeq.length; i++) {
                    trackLengths.add(wholesPerSequence);

                }
            } else if (!emptyStreak) {
                if (year >= structure.getMinYear() && year <= structure.getMaxYear()) {
                    emptyStreak = true;
                }

                if (wholesPerEmptyYear > 0) {
                    trackLengths.add(wholesPerEmptyYear);
                }
            }
        }

        int[] trackLengthsArr = new int[trackLengths.size()];
        for (int i = 0; i < trackLengthsArr.length; i++) {
            trackLengthsArr[i] = trackLengths.get(i);
        }

        return trackLengthsArr;
    }

    public int getCurrentSequence() {
        return currentSequence;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public EventData getCurrentEvent() { return currentEvent; }

    public int getWholesPerSequence() {
        return wholesPerSequence;
    }

    public int getWholesPerEmpty() {
        return wholesPerEmptyYear;
    }

    public Key getSongKey() { return songKey; }

    public Band getBand() {
        return band;
    }

    public Map<Integer, FacultyData> getFacultyMap() {
        return this.faculties;
    }

    public void addEvent(FacultyData faculty, EventData event) {
        faculty.events.add(event);
        sequenceContext.addEvent(faculty.ID, event);
    }


    public FacultyContext sequenceContext() {
        return sequenceContext;
    }
    public FacultyContext yearContext() { return yearContext;}

    public EventsBoleroStructure getStructure() { return structure; }

    public Composition getComposition() {
        return this.composition;
    }

    public DBConductor getConductor() {
        return conductor;
    }

    public ResultSet queryDB(String query) throws SQLException {
        return connection.prepareStatement(query).executeQuery();
    }

    /**
     * Temporary faculty data scope
     * Resets between scope changes (new year, new sequence, etc)
     */
    public class FacultyContext {

        int eventsInContext = 0;
        FacultyContext containedContext;
        Map<Integer, List<EventData>> facultyEvents;
        Map<Integer, BandRole> facultyRoles;
        Map<BandRole, Set<Integer>> existingRoles;

        private FacultyContext() {
            this(null);
        }

        private FacultyContext(FacultyContext containedContext) {
            this.facultyEvents = new HashMap<>();
            this.facultyRoles = new HashMap<>();
            this.existingRoles = new HashMap<>();
            this.containedContext = containedContext;
        }

        public boolean facultyHasRole(int facultyID) {
            return facultyRoles.containsKey(facultyID);
        }

        public BandRole facultyRole(int facultyID) {
            return facultyRoles.get(facultyID);
        }

        public boolean roleExists(BandRole role) {
            return existingRoles.containsKey(role);
        }

        public List<EventData> getEvents(int facultyID) {

            return facultyEvents.getOrDefault(facultyID, Collections.emptyList());
        }

        public boolean addEvent(int facultyID, EventData event) {
            if (containedContext != null) {
                containedContext.addEvent(facultyID, event);
            }

            List<EventData> events = facultyEvents.computeIfAbsent(facultyID, ArrayList::new);
            eventsInContext++;
            return events.add(event);
        }

        public int getEventCount(int facultyID) {

            return facultyEvents.getOrDefault(facultyID, Collections.emptyList()).size();
        }

        public int getEventCount() {
            return eventsInContext;
        }

        public void setHasRole(int facultyID, BandRole role) {
            if (containedContext != null) {
                containedContext.setHasRole(facultyID, role);
            }

            facultyRoles.put(facultyID, role);

            if (role != null) {
                Set<Integer> roleFaculties = existingRoles.computeIfAbsent(role, k -> new HashSet<>());
                roleFaculties.add(facultyID);
            }
        }

        void reset() {
            facultyEvents.clear();
            facultyRoles.clear();
            existingRoles.clear();
            eventsInContext = 0;
        }
    }
}
