package idc.symphony.visual.scheduling;

public class SongTermination implements VisualEvent {
    public final double time;
    public final double duration;

    public SongTermination(double time, double duration) {
        this.time = time;
        this.duration = duration;
    }

    @Override
    public double time() {
        return time;
    }
}
