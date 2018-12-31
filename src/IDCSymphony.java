import idc.symphony.music.band.Administration;
import idc.symphony.music.band.CS;
import idc.symphony.music.band.Faculty;
import idc.symphony.music.band.Law;
import org.jfugue.midi.MidiDictionary;
import org.jfugue.player.Player;
import org.jfugue.theory.Key;
import visual.Visualizer;
import visual.Window;

import javax.swing.*;

public class IDCSymphony {
    public static void main(String [] args) {


        Visualizer vis = new Visualizer();


        Faculty cs = new CS(MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("SQUARE"));
        Faculty law = new Law(MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("VIOLIN"));
        Faculty admin = new Administration(MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("TAIKO_DRUM"));

        Player player = new Player();
        Key key = new Key("Gmaj");
        player.play(cs.playCarpet2(1, key));

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