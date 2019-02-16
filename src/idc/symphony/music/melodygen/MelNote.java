package idc.symphony.music.melodygen;

/**
 * A simple packet class holding a note and a duration value.
 */
public class MelNote {

    Byte note;
    Byte duration; // We consider 32 to be an entire whole

    /**
     * Create a new copy of an existing MelNote.
     * @param other
     */
    public MelNote(MelNote other) {
        this.note = other.note;
        this.duration = other.duration;
    }

}
