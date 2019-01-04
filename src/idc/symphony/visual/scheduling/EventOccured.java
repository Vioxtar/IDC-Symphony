package idc.symphony.visual.scheduling;

public class EventOccured implements VisualEvent {
    public final double time;
    public final int facultyID;
    public final int year;
    public final String description;

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
}
