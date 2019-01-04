package idc.symphony.visual.scheduling;

import idc.symphony.data.FacultyData;

public class NotePlayed implements VisualEvent {
    public final double time;
    public final double duration;
    public final double amp;
    public final FacultyData faculty;

    public NotePlayed(FacultyData faculty, double time, double duration, double amp) {
        this.time = time;
        this.faculty = faculty;
        this.duration = duration;
        this.amp = amp;
    }

    @Override
    public double time() {
        return this.time;
    }
}
