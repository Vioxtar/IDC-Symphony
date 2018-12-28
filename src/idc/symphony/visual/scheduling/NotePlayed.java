package idc.symphony.visual.scheduling;

public class NotePlayed implements VisualEvent {
    private double time;
    private int facultyID;
    private double amp;

    public NotePlayed(double time, int facultyID, double amp) {
        this.time = time;
        this.facultyID = facultyID;
        this.amp = amp;
    }

    @Override
    public double time() {
        return this.time;
    }

    public double amp() {
        return amp;
    }

    public int facultyID() {
        return facultyID;
    }
}
