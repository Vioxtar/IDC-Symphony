package idc.symphony.music.transformers.visualization;

import idc.symphony.data.FacultyData;
import idc.symphony.music.band.Faculty;
import idc.symphony.music.transformers.visualization.factory.VisualEventManager;
import idc.symphony.visual.scheduling.FacultyJoined;
import idc.symphony.visual.scheduling.NotePlayed;
import idc.symphony.visual.scheduling.VisualEvent;
import org.jfugue.midi.MidiDefaults;
import org.jfugue.midi.TrackTimeManager;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

import java.util.*;

public class VisualEventsBuilder extends ParserListenerAdapter {
    private VisualEventManager eventManager;
    private LayerTrackTimeManager timeManager;
    private Map<Byte, FacultyData> trackToFacultyMap;
    private List<VisualEvent> events;
    private int tempo;

    public VisualEventsBuilder(VisualEventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void beforeParsingStarts() {
        events = new ArrayList<>();
        trackToFacultyMap = new HashMap<>();
        timeManager = new LayerTrackTimeManager();
        tempo = MidiDefaults.DEFAULT_TEMPO_BEATS_PER_MINUTE;
    }

    public Queue<VisualEvent> build(Pattern pattern) {
        pattern.transform(this);

        events.sort(Comparator.comparingDouble(VisualEvent::time));
        return new LinkedList<>(events);
    }

    public void onTempoChanged(int tempoBPM) {
        tempo = tempoBPM;
    }

    public void onTrackChanged(byte track) {
        timeManager.setCurrentTrack(track);
    }

    public void onLayerChanged(byte layer) {
        timeManager.setCurrentLayerNumber(layer);
    }

    public void onTrackBeatTimeBookmarked(String timeBookmarkId) {
        timeManager.addTrackTickTimeBookmark(timeBookmarkId);
    }

    public void onTrackBeatTimeBookmarkRequested(String timeBookmarkId) {
        double time = timeManager.getTrackBeatTimeBookmark(timeBookmarkId);
        timeManager.setTrackBeatTime(time);
    }

    public void onTrackBeatTimeRequested(double time) {
        timeManager.setTrackBeatTime(time);
    }

    public void onLyricParsed(String lyric) {
        List<VisualEvent> newEvents = eventManager.getEventsFromLyric(
                beatTimeToDuration(timeManager.getTrackBeatTime()),
                lyric
        );

        if (newEvents.size() > 0) {
            events.addAll(newEvents);
            VisualEvent lastEvent = newEvents.get(newEvents.size() - 1);

            if (lastEvent instanceof FacultyJoined) {
                trackToFacultyMap.put(
                        timeManager.getCurrentTrackNumber(),
                        ((FacultyJoined) lastEvent).faculty
                );
            }
        }
    }

    public void onNoteParsed(Note note) {
        FacultyData noteFaculty = trackToFacultyMap.get(timeManager.getCurrentTrackNumber());
        if (noteFaculty != null) {
            events.add(new NotePlayed(
                    noteFaculty,
                    beatTimeToDuration(timeManager.getTrackBeatTime()),
                    beatTimeToDuration(note.getDuration()),
                    note.getOnVelocity()
            ));
        }
    }

    private double beatTimeToDuration(double beatTime) {
        return (beatTime * 4 * 60) / tempo;
    }

    /**
     * Make layer getter public, required for builder and makes no sense to make it private anyhow.
     */
    private static class LayerTrackTimeManager extends TrackTimeManager {
        @Override
        public byte getCurrentLayerNumber() {
            return super.getCurrentLayerNumber();
        }
    }
}
