package idc.symphony.db;

/**
 * Events SQL Query constants
 */
public class EventsQueries {
    public static final String SELECT_EVENTS =
            "SELECT EventYear as \"Year\", EventType as \"Type\", " +
                    "EventFaculty as Faculty, EventShortDescription as Description\n" +
            "FROM Events\n" +
            "ORDER BY EventYear";

    public static final String YEAR = "Year";
    public static final String TYPE = "Type";
    public static final String FACULTY = "Faculty";
    public static final String DESCRIPTION = "Description";

    private EventsQueries(){}
}
