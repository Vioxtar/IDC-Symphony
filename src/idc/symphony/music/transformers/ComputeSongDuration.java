package idc.symphony.music.transformers;

import org.jfugue.parser.ParserListener;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Note;

public class ComputeSongDuration extends ParserListenerAdapter implements ParserListener {
    private double[] durations = new double[16];
    private double[] trackBeatTime = new double[16];
    private int currentTrack = 0;
    private double currentTrackBeatTime = 1;
    private int tempo = 120;

    public void onTempoChanged(int tempoBPM) {
        tempo = tempoBPM;
    }

    public void onTrackChanged(byte track) {
        this.currentTrack = track;
    }

    public void onTrackBeatTimeRequested(double time) {
        trackBeatTime[currentTrack] = time;
    }

    public void onNoteParsed(Note note) {
        trackBeatTime[currentTrack] += note.getDuration();

        if(!note.isRest()) {
            durations[currentTrack] = Math.max(durations[currentTrack], trackBeatTime[currentTrack]);
        }
    }

    public double getDuration() {
        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < durations.length; i++) {
            max = Math.max(max, durations[i]);
        }

        return beatTimeToDuration(max);
    }

    private double beatTimeToDuration(double beatTime) {
        return (beatTime * 4 * 60) / tempo;
    }
}
