package idc.symphony.music;

import idc.symphony.data.EventData;
import idc.symphony.data.EventType;
import idc.symphony.db.EventTypesFactory;
import idc.symphony.db.EventsQueries;
import idc.symphony.db.FacultyDataFactory;
import idc.symphony.music.band.Band;
import idc.symphony.data.FacultyData;
import idc.symphony.data.YearCollection;
import idc.symphony.db.YearDataFactory;
import idc.symphony.music.conducting.Command;
import idc.symphony.music.conducting.Prioritized;
import idc.symphony.music.conducting.Recurrence;
import org.jfugue.pattern.Pattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Database Bolero Conductor
 *
 * Iterates over Event database, deciding Bolero structure (sequence counts, lengths) and
 * roles for each faculty in every sequence according to event database.
 */
public class DBConductor {
    // DB Fields
    private Connection     dbConnection;
    private ConductorState state;

    private Map<Recurrence, SortedSet<Prioritized<Command>>> commandMap;

    public DBConductor(Connection dbConnection) {
        this.dbConnection = dbConnection;

        commandMap = new HashMap<>();

    }

    /**
     *
     * @param recurrence Recurrence scope of command execution
     */
    private void executeCommands(Recurrence recurrence) {
        if (commandMap.containsKey(recurrence)) {
            commandMap.get(recurrence).removeIf(
                    commandPrioritized -> !commandPrioritized.value.execute(state, recurrence));
        }
    }

    Connection connection() {
        return dbConnection;
    }

    /**
     * @param priority   Priority relative to other commands (lower means first)
     * @param recurrence When to call command (on each event...)
     */
    public Prioritized<Command> addCommand(int priority, Recurrence recurrence, Command command) {
        if (!commandMap.containsKey(recurrence)) {
            commandMap.put(recurrence, new TreeSet<>());
        }

        Prioritized<Command> prioritized = command.prioritize(priority);
        commandMap.get(recurrence).add(prioritized);

        return prioritized;
    }

    /**
     * Creates a new conductor state
     */
    private void resetState() throws SQLException {
        Band band = new Band();

        state = new ConductorState(
                this,
                band,
                prepareBoleroStructure(band),
                generateEventTypes(),
                generateFacultyData(band));
    }

    /**
     * Processes a fixed amount of events by advancing their content into state,
     * and executing relevant commands for each event.
     * @param numEvents Number of events to process
     * @param EventSet  Event iterator from DB
     */
    private void processEvents(int numEvents, ResultSet EventSet) throws SQLException {
        for (int i = 0; i < numEvents; i++) {
            EventSet.next();
            FacultyData eventFaculty = state.faculties.get(EventSet.getInt(EventsQueries.FACULTY));
            if (eventFaculty != null) {
                state.currentEvent = new EventData(
                        state.currentYear,
                        state.currentSequence,
                        state.eventTypes.get(EventSet.getInt(EventsQueries.TYPE)),
                        eventFaculty,
                        EventSet.getString(EventsQueries.DESCRIPTION)
                );

                state.addEvent(eventFaculty, state.currentEvent);
                executeCommands(Recurrence.Event);
            }
        }
    }


    /**
     * Process a fixed amount of sequences, updating state accordingly and executing relevant commands
     * for each sequence.
     * @param sequenceEventCounts Number of events in each sequence
     * @param EventSet            Event iterator from DB
     * @throws SQLException
     */
    private void processSequences(int[] sequenceEventCounts, ResultSet EventSet) throws SQLException {
        for (int yearSequence = 0;
             yearSequence < sequenceEventCounts.length;
             yearSequence++) {
            processEvents(sequenceEventCounts[yearSequence], EventSet);
            executeCommands(Recurrence.Sequence);
            state.sequenceContext().reset();
            state.currentSequence++;
        }

        if (sequenceEventCounts.length == 0 && state.getWholesPerEmpty() > 0) {
            executeCommands(Recurrence.EmptyYear);
            state.sequenceContext().reset();
            state.currentSequence++;
        }
    }

    /**
     * Transforms the events database into a Bolero-Structured composition according to the
     * compositional commands given to the conductor.
     */
    public Pattern conduct() throws SQLException {
        resetState();

        PreparedStatement eventStatement = dbConnection.prepareStatement(EventsQueries.SELECT_EVENTS);
        ResultSet EventSet = eventStatement.executeQuery();

        int yearMin = state.structure.getMinYear() - 1;
        int yearMax = state.structure.getMaxYear();

        for (state.currentYear = yearMin;
             state.currentYear <= yearMax;
             state.currentYear++) {
            processSequences(state.structure.eventsPerSequence(state.currentYear), EventSet);
            executeCommands(Recurrence.Year);
            state.yearContext().reset();
        }

        return state.composition.getFinalComposition(state.tempo);
    }

    public Map<Integer, FacultyData> getFacultyMap() {
        if (state != null) {
            return state.getFacultyMap();
        }

        return null;
    }

    /**
     * Calculates Bolero Structure (sequence amount, events per sequence, divided by years)
     */
    private EventsBoleroStructure prepareBoleroStructure(Band band) throws SQLException {
        try {
            YearCollection stats = YearDataFactory.fromDB(dbConnection, band.bandMembers().keySet());
            return new EventsBoleroStructure(stats);
        } catch (SQLException exception) {
            throw new SQLException("While creating EventsBoleroStructure", exception);
        }
    }

    /**
     * Imports faculties from database
     * Done via proxy function instead of directly using static class, for maintainability purposes.
     */
    private Map<Integer, FacultyData> generateFacultyData(Band band) throws SQLException {
        Map<Integer, FacultyData> facultyStatData;
        try {
            facultyStatData = FacultyDataFactory.fromDB(dbConnection);
        } catch(SQLException exception) {
            throw new SQLException("While generating Faculty data from DB", exception);
        }

        Iterator<Integer> facultyIDs = facultyStatData.keySet().iterator();
        Set<Integer> supportedIDs = band.bandMembers().keySet();

        while (facultyIDs.hasNext()) {
            if (!supportedIDs.contains(facultyIDs.next())) {
                facultyIDs.remove();
            }

        }

        return facultyStatData;
    }

    /**
     * Imports event types from database
     * Done via proxy function instead of directly using static class, for maintainability purposes.
     */
    private Map<Integer, EventType> generateEventTypes() throws SQLException {
        try {
            return EventTypesFactory.fromDB(dbConnection);
        } catch (SQLException exception) {
            throw new SQLException("While generating Event Type data from DB", exception);
        }
    }


}
