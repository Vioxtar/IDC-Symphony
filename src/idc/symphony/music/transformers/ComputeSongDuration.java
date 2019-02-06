package idc.symphony.music.transformers;

import org.jfugue.parser.ParserListener;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

/**
 * For debug purposes, computes duration of each track, and durations of whole song.
 * TODO: Fix bug where layer isn't accounted for, corrupting drum track's duration
 */
public class ComputeSongDuration extends ParserListenerAdapter implements ParserListener {
    private double[][] durations = new double[16][16];
    private double[][] trackBeatTime = new double[16][16];
    private int[] currentLayers = new int[16];
    private int currentTrack = 0;
    private int tempo = 120;

    private int getCurrentLayer() {
        return currentLayers[currentTrack];
    }

    private void setCurrentLayer(int layer) {
        currentLayers[currentTrack] = layer;
    }

    public void onTempoChanged(int tempoBPM) {
        tempo = tempoBPM;
    }

    public void onTrackChanged(byte track) {
        this.currentTrack = track;
    }

    public void onLayerChanged(byte layer) {
        setCurrentLayer(layer);
    }

    public void onTrackBeatTimeRequested(double time) {
        trackBeatTime[currentTrack][getCurrentLayer()] = time;
    }

    public void onNoteParsed(Note note) {
        trackBeatTime[currentTrack][getCurrentLayer()] += note.getDuration();

        if(!note.isRest()) {
            durations[currentTrack][getCurrentLayer()] = Math.max(durations[currentTrack][getCurrentLayer()], trackBeatTime[currentTrack][getCurrentLayer()]);
        }
    }

    public double getDuration() {
        double max = Double.NEGATIVE_INFINITY;

        for (int track = 0; track < durations.length; track++) {
            for (int layer = 0; layer < durations[0].length; layer++) {
                max = Math.max(max, durations[track][layer]);
            }
        }

        return beatTimeToDuration(max);
    }

    public static double getDuration(Pattern pattern) {
        ComputeSongDuration songDurationComputer = new ComputeSongDuration();

        if (pattern != null) {
            pattern.measure(songDurationComputer);
            return songDurationComputer.getDuration();
        }

        return 0;
    }

    private double beatTimeToDuration(double beatTime) {
        return (beatTime * 4 * 60) / tempo;
    }
}
