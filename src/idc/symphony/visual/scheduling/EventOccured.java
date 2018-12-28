package idc.symphony.visual.scheduling;

public class EventOccured implements VisualEvent {
    private double time;
    private int facultyID;
    private int year;
    private String description;

    public EventOccured(double time, int facultyID, int year, String description) {
        this.time = time;
        this.facultyID = facultyID;
        this.year = year;
        this.description = description;
    }

    @Override
    public double time() {
        return time;
    }

    public int facultyID() {
        return facultyID;
    }

    public int year() {
        return year;
    }

    public String description() {
        return description;
    }
}
