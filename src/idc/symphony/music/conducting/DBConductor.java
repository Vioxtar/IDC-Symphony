package idc.symphony.music.conducting;

import idc.symphony.data.EventData;
import idc.symphony.data.EventType;
import idc.symphony.db.EventTypesFactory;
import idc.symphony.db.EventsQueries;
import idc.symphony.db.FacultyDataFactory;
import idc.symphony.music.band.Band;
import idc.symphony.data.FacultyData;
import idc.symphony.data.YearCollection;
import idc.symphony.db.YearDataFactory;
import idc.symphony.music.conducting.commands.Command;
import idc.symphony.music.conducting.commands.Recurrence;
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
    /**
     * Map of conducting commands with different execution commands
     */
    private Map<Recurrence, SortedSet<Prioritized<Command>>> commandMap;
    private ConductorState state;

    public DBConductor() {
        commandMap = new HashMap<>();
    }

    /**
     * @param recurrence Recurrence scope of command execution
     */
    private void executeCommands(Recurrence recurrence) {
        if (commandMap.containsKey(recurrence)) {
            commandMap.get(recurrence).removeIf(
                    commandPrioritized -> !commandPrioritized.value.execute(state, recurrence));
        }
    }

    /**
     * @param priority   Priority relative to other commands (lower means first)
     * @param recurrence When to call command (on each event...)
     */
    public void addCommand(int priority, Recurrence recurrence, Command command) {
        if (!commandMap.containsKey(recurrence)) {
            commandMap.put(recurrence, new TreeSet<>());
        }

        Prioritized<Command> prioritized = command.prioritize(priority);
        commandMap.get(recurrence).add(prioritized);
    }

    /**
     * Creates a new conductor state
     */
    private void resetState(Connection connection) throws SQLException {
        Band band = new Band();

        state = new ConductorState(
                this,
                band,
                prepareBoleroStructure(connection, band),
                generateEventTypes(connection),
                generateFacultyData(connection, band));
        state.connection = connection;
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
     */
    private void processSequences(int[] sequenceEventCounts, ResultSet EventSet) throws SQLException {
        for (int sequenceEventCount : sequenceEventCounts) {
            processEvents(sequenceEventCount, EventSet);
            executeCommands(Recurrence.Sequence);
            state.sequenceContext().reset();
            state.currentSequence++;
            state.emptyStreak = false;
        }

        if (sequenceEventCounts.length == 0 && !state.emptyStreak && state.getWholesPerEmpty() > 0) {
            state.emptyStreak = true;
            executeCommands(Recurrence.EmptyYear);
            state.sequenceContext().reset();
            state.currentSequence++;
        }
    }

    /**
     * Transforms the events database into a Bolero-Structured composition according to the
     * compositional commands given to the conductor.
     */
    public Pattern conduct(Connection connection) throws SQLException {
        resetState(connection);

        PreparedStatement eventStatement = state.connection.prepareStatement(EventsQueries.SELECT_EVENTS);
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

    /**
     * @return Map of faculties used by conductor to generate music in last use of conduct()
     */
    public Map<Integer, FacultyData> getFacultyMap() {
        if (state != null) {
            return state.getFacultyMap();
        }

        return null;
    }

    /**
     * Calculates Bolero YearStructure (sequence amount, events per sequence, divided by years)
     */
    private EventsBoleroStructure prepareBoleroStructure(Connection connection, Band band) throws SQLException {
        try {
            YearCollection stats = YearDataFactory.fromDB(connection, band.bandMembers().keySet());
            return new EventsBoleroStructure(stats);
        } catch (SQLException exception) {
            throw new SQLException("While creating EventsBoleroStructure", exception);
        }
    }

    /**
     * Imports faculties from database and filters away faculties not supported by band implementation
     * Done via proxy function instead of directly using static class, for maintainability purposes.
     */
    private Map<Integer, FacultyData> generateFacultyData(Connection connection, Band band) throws SQLException {
        Map<Integer, FacultyData> facultyStatData;
        try {
            facultyStatData = FacultyDataFactory.fromDB(connection);
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
    private Map<Integer, EventType> generateEventTypes(Connection connection) throws SQLException {
        try {
            return EventTypesFactory.fromDB(connection);
        } catch (SQLException exception) {
            throw new SQLException("While generating Event Type data from DB", exception);
        }
    }


}
