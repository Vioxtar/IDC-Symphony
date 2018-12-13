package History;

import History.EventTypeModel.EventType;
import History.FacultyModel.Faculty;


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

    public short getYear() {
        return year;
    }

    public byte getMonth() {
        return month;
    }

    public byte getDay() {
        return day;
    }

    public EventType getEventType() {
        return type;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getRichDescription() {
        return richDescription;
    }

    @Override
    public int compareTo(Event other) {
        if (year > other.year) { return 1; } else if (year < other.year) { return -1; }
        if (month > other.month) { return 1; } else if (month < other.month) { return -1; }
        if (day > other.day) { return 1; } else if (day < other.day) { return -1; }
        return 0;
    }

    @Override
    public String toString() {
        return "["+year+" "+month+" "+day+"] ["+type+"] ["+faculty+"] // "+shortDescription;
    }

}
