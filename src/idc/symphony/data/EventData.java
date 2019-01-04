package idc.symphony.data;

/**
 * Pure Data Class
 */
public class EventData {
    public final int year;
    public final int sequence;
    public final EventType type;
    public final FacultyData faculty;
    public final String description;

    public EventData(
            int eventYear,
            int eventSequence,
            EventType eventType,
            FacultyData eventFaculty,
            String eventDescription) {

        this.year = eventYear;
        this.sequence = eventSequence;
        this.type = eventType;
        this.faculty = eventFaculty;
        this.description = eventDescription;
    }
}
