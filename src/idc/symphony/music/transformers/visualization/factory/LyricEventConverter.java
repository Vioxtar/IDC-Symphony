package idc.symphony.music.transformers.visualization.factory;

import idc.symphony.visual.scheduling.VisualEvent;

import java.util.List;

public interface LyricEventConverter {
    char EVENT_TYPE_DELIM = ':';
    char EVENT_ARG_DELIM =',';

    boolean isMatch(String name);
    List<VisualEvent> convert(double time, String lyric);
}
