package idc.symphony.music.melodygen;

import idc.symphony.music.band.Faculty;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

import java.util.HashMap;
import java.util.Map;

public class mains {

    public static void main(String[] args) {
        Key key = new Key("Dbmin");
        Pattern pattern = new Pattern();

        HashMap<Integer, Note> notesMap = new HashMap<>();

        int i = 0;
        for (Note note : Faculty.getKeyNotes(key)) {
            note.setDuration(1/16);
            pattern.add(note);
            notesMap.put(i++, note);
        }

        Player ply = new Player();

        WholeNode wn = new WholeNode(pattern, notesMap);
        System.out.println(pattern);
        ply.play(pattern);
        ply.play(wn.toPattern(notesMap));
        System.out.println(wn.toPattern(notesMap));

    }

}
