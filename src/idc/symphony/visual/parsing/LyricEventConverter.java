package idc.symphony.visual.parsing;

import idc.symphony.visual.scheduling.VisualEvent;

import java.util.List;

/**
 * Converts a given lyric message into a visual event.
 */
public interface LyricEventConverter {
    String EVENT_TYPE_DELIM = ":";
    String EVENT_ARG_DELIM =",";

    boolean isMatch(String name);
    List<VisualEvent> convert(double time, String lyric);
}
