package idc.symphony.music.conducting;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public interface PatternRole {
    Pattern playRole(int wholes, Key key);
}
