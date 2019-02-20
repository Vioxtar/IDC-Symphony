package idc.symphony.music.band;

import idc.symphony.music.melodygen.HammingWhole;
import idc.symphony.music.melodygen.Node;
import idc.symphony.music.melodygen.Path;
import idc.symphony.music.melodygen.PathStyle;
import idc.symphony.music.transformers.AmplitudeTransformer;
import idc.symphony.music.transformers.BassRhythmTransformer;
import idc.symphony.music.transformers.OctaveTransformer;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

import java.util.HashMap;

public class CS extends Faculty {

    byte instrument;

    public CS(byte instrument) {
        this.instrument = instrument;

        // Set a seed for this generator
        long seed = 65536;
        this.gen.setSeed(seed);
    }


    @Override
    public int getTargetTrack() {
        return 3;
    }


    @Override
    public Pattern playMainMelody(int wholes, Key key) {

        HashMap<Integer, Note> notesMap = new HashMap<>();

        // Notes map, can create scales / our own notes that sound nice with ease
        // these key V values signify 'distance' of notes
        notesMap.put(0, new Note("G"));
        notesMap.put(1, new Note("A"));
        notesMap.put(2, new Note("B"));
        notesMap.put(3, new Note("C"));
        notesMap.put(4, new Note("D"));
        notesMap.put(5, new Note("E"));
        notesMap.put(6, new Note("F#"));

        // A 'starting' pattern to be given to our starting node
        Pattern pattern = new Pattern();
        pattern.add("G A R E"); // The starting node is the main motive

        // Set the new HammingWhole (goes in our starting node)
        HammingWhole wh = new HammingWhole(16, pattern, notesMap, 4);
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
        p.traverseWithStyle(n, ps1, 8527236, wholes);

        return p.toPattern().setInstrument(instrument);


    }

    @Override
    public Pattern playSecondary(int wholes, Key key) {

        HashMap<Integer, Note> notesMap = new HashMap<>();

        // Notes map, can create scales / our own notes that sound nice with ease
        // these key V values signify 'distance' of notes
        notesMap.put(0, new Note("G"));
        notesMap.put(1, new Note("A"));
        notesMap.put(2, new Note("B"));
        notesMap.put(3, new Note("C"));
        notesMap.put(4, new Note("D"));
        notesMap.put(5, new Note("E"));
        notesMap.put(6, new Note("F#"));

        // A 'starting' pattern to be given to our starting node
        Pattern pattern = new Pattern();
        pattern.add("G A R E"); // The starting node is the main motive

        // Set the new HammingWhole (goes in our starting node)
        HammingWhole wh = new HammingWhole(8, pattern, notesMap, 4);
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
        p.traverseWithStyle(n, ps1, 227274, wholes);

        return p.toPattern().setInstrument(instrument);

    }

    @Override
    public Pattern playCarpet3(int wholes, Key key) {
        //Pattern p = getLibrary().getPattern("chords_prog_v1");
        Pattern p = new Pattern("Gw Ew Bw Dw");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(0.85f, p);

        return BassRhythmTransformer.toBassRhythm(p, new boolean[]{true, true, true, false, true, true, true, false});
    }

    @Override
    public Pattern playCarpet2(int wholes, Key key) {
        //Pattern p = getLibrary().getPattern("chords_prog_v1");
        Pattern p = new Pattern("Gw Ew Bw Dw");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(0.85f, p);

        return BassRhythmTransformer.toBassRhythm(p, new boolean[]{true, true, true, true});
    }

    @Override
    public Pattern playCarpet1(int wholes, Key key) {
        //Pattern p = getLibrary().getPattern("chords_prog_v1");
        Pattern p = new Pattern("Gw Ew Bw Dw");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(0.85f, p);

        return BassRhythmTransformer.toBassRhythm(p, new boolean[]{true, false, true, false});
    }
}
