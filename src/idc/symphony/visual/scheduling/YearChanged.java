package idc.symphony.visual.scheduling;

public class YearChanged implements VisualEvent {
    public final double time;
    public final int year;

    public YearChanged(double time, int year) {
        this.time = time;
        this.year = year;
    }

    @Override
    public double time() {
        return time;
    }
}
