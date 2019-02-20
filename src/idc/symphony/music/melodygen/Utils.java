package idc.symphony.music.melodygen;

import idc.symphony.music.PatternLibrary;
import idc.symphony.music.band.Faculty;
import org.jfugue.midi.MidiDictionary;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.random;

public class Utils {

    public static void main(String[] args) {

        HashMap<Integer, Note> notesMap = new HashMap<>();

        // Notes map, can create scales / our own notes that sound nice with ease
        // these key V values signify 'distance' of notes
        notesMap.put(0, new Note("C"));
        notesMap.put(1, new Note("Db"));
        notesMap.put(2, new Note("F"));
        notesMap.put(3, new Note("Ab"));

        // A 'starting' pattern to be given to our starting node
        Pattern pattern = new Pattern();
        pattern.add("R C R R"); // The starting node is the main motive

        // Set the new HammingWhole (goes in our starting node)
        HammingWhole wh = new HammingWhole(8, pattern, notesMap, 5);
        Node n = new Node(wh); Path p = new Path();

        // Define a path traversal style
        PathStyle ps1 = new PathStyle(); ps1
                .addCommand(PathStyle.GotoStartingNode)
                .addCommand(PathStyle.GoForward, "7, 7").addCommand(PathStyle.Collect)
                .addCommand(PathStyle.GoBackward, "2, 2").addCommand(PathStyle.Collect)
                .addCommand(PathStyle.GoBackward, "1, 1").addCommand(PathStyle.Collect)
                .addCommand(PathStyle.GoBackward, "1, 1").addCommand(PathStyle.Collect)
        ;

        // Perform the traversal
        p.traverseWithStyle(n, ps1, 13412, 4);

        // Play the result
        Player ply = new Player();
        Pattern result = p.toPattern();
//        result.setInstrument((MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("TREMOLO_STRINGS")));
//        result.setTempo(240);

        // Save
        boolean save = false;
        if (save) {
            try {
                MidiFileManager.savePatternToMidi(result, new File("traversal8.mid"));
            } catch (IOException ex) {}
        }

        ply.play(result);
    }



    public static int clamp(int val, int min, int max) {
        return Math.min(Math.max(val, min), max);
    }
}


