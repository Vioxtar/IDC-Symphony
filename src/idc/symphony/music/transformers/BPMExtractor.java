package idc.symphony.music.transformers;

import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;

public class BPMExtractor extends ParserListenerAdapter {
    private int bpm = -1;

    public static int extract(Pattern pattern) {
        BPMExtractor extractor = new BPMExtractor();
        pattern.transform(extractor);

        return extractor.bpm;
    }

    public void onTempoChanged(int BPM) {
        bpm = BPM;
    }
}
