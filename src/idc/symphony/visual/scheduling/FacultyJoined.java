package idc.symphony.visual.scheduling;

public class FacultyJoined implements VisualEvent {
    private double time;
    private int facultyID;
    private int parentID;

    public FacultyJoined(double time, int facultyID, int parentID){
        this.time = time;
        this.facultyID = facultyID;
        this.parentID = parentID;
    }

    @Override
    public double time() {
        return time;
    }

    public int facultyID() {
        return facultyID;
    }

    public int parentID() {
        return parentID;
    }
}
