import idc.symphony.music.PatternLibrary;
import idc.symphony.music.band.CS;
import idc.symphony.music.band.Faculty;
import idc.symphony.music.band.Law;
import idc.symphony.music.sequence.MidiTrimmer;
import org.jfugue.midi.MidiDictionary;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Key;

import java.io.File;
import java.io.IOException;

public class IDCSymphony {
    public static void main(String [] args) {
        Faculty cs = new CS((byte) 40);
        Faculty law = new Law(MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("VIOLIN"));
        Faculty admin = new Law(MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("TAIKO_DRUM"));

        Player player = new Player();
        Key key = new Key("Gmaj");
        player.play(admin.playCarpet2(10, key));
//
//        float[] help = cs.divideDuration(4, 2);
//        for (int i = 0; i < help.length; i++) {
//            System.out.println(help[i]);
//        }

//        PatternLibrary patternLib = new PatternLibrary();
//        String[] goodNames = patternLib.loadPatternsFromFile(new File("usableplaceholderpatterns"));
//
//        for (int i = 0; i < goodNames.length; i++) {
//            System.out.println(goodNames[i]);
//        }
//
//        Player player = new Player();
//        player.play(patternLib.getAllPatternsMap().get("chords_progv2").setTempo(130));

    }
}