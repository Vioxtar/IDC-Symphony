package idc.symphony.visual.scheduling;

import java.util.Comparator;

public interface VisualEvent {
    double time();

    Comparator<VisualEvent> timeComparator = Comparator.comparingDouble(VisualEvent::time);
}
