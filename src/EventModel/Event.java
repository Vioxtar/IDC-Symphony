package EventModel;

import FacultyModel.Faculty;
import javafx.event.EventType;


public class Event implements Comparable<Event> {

    short id;

    short year;
    byte month;
    byte day;

    EventType type;
    Faculty faculty;

    String shortDescription;
    String richDescription;

    public Event() {
    }

    public int compareTo(Event other) {
        if (year > other.year) { return 1; } else if (year < other.year) { return -1; }
        if (month > other.month) { return 1; } else if (month < other.month) { return -1; }
        if (day > other.day) { return 1; } else if (day < other.day) { return -1; }
        return 0;
    }

}
