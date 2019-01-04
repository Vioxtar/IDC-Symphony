package idc.symphony.music.transformers.visualization.factory;

import idc.symphony.data.FacultyData;
import idc.symphony.music.band.BandRole;
import idc.symphony.visual.scheduling.*;
import org.jfugue.theory.Note;

import java.util.*;

public class VisualEventManager {
    List<LyricEventConverter> converters;

    /**
     *
     * @param converters
     */
    public VisualEventManager(List<LyricEventConverter> converters) {
        if (converters == null || converters.size() == 0) {
            throw new IllegalArgumentException("Provided converter list must be non-empty");
        }

        this.converters = converters;
    }

    public List<VisualEvent> getEventsFromLyric(double time, String lyric) {
        String lyrics[] = lyric.split(String.valueOf(LyricEventConverter.EVENT_TYPE_DELIM), 2);

        if (lyrics.length == 2) {
            for (LyricEventConverter converter : converters) {
                if (converter.isMatch(lyrics[0]))
                    return converter.convert(time, lyrics[1]);
            }
        }

        return Collections.emptyList();
    }

    public static List<LyricEventConverter> defaultConverters(Map<Integer, FacultyData> facultyMap) {
        List<LyricEventConverter> converters = new ArrayList<>();
        converters.add(new EventOccuredConverter());
        converters.add(new FacultyJoinedConverter(facultyMap));
        converters.add(new FacultyRoleChangedConverter(facultyMap));
        converters.add(new YearChangedConverter());

        return converters;
    }

    /**
     * For event info popups
     */
    private static class EventOccuredConverter implements LyricEventConverter {

        @Override
        public boolean isMatch(String name) {
            return "EVENT".equalsIgnoreCase(name);
        }

        @Override
        public List<VisualEvent> convert(double time, String lyric) {
            String[] args = lyric.split(",", 3);

            return Collections.singletonList(new EventOccured(
                    time,
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    args[2]
            ));
        }
    }

    /**
     * For creation of new trails
     * For each faculty that receives a role for the first time, give it a joined event.
     */
    private static class FacultyJoinedConverter implements LyricEventConverter {
        Map<Integer, FacultyData> facultyDataMap;
        Set<Integer> facultiesJoined;

        public FacultyJoinedConverter(Map<Integer, FacultyData> facultyMap) {
            this.facultyDataMap = facultyMap;
            this.facultiesJoined = new HashSet<>();
        }

        @Override
        public boolean isMatch(String name) {
            return "JOINED".equalsIgnoreCase(name);
        }

        @Override
        public List<VisualEvent> convert(double time, String lyric) {
            String[] args = lyric.split(",", 3);
            LinkedList<VisualEvent> result = new LinkedList<>();
            int facultyID = Integer.parseInt(args[0]);

            if (!facultiesJoined.contains(facultyID)) {
                FacultyData currFaculty = facultyDataMap.get(facultyID);
                while (currFaculty != null) {
                    if (!facultiesJoined.contains(currFaculty.ID)) {
                        // Imposes correct order of faculty joined event in negligible time difference
                        result.addFirst(new FacultyJoined(time -= 0.001, currFaculty));
                        facultiesJoined.add(currFaculty.ID);
                    }

                    currFaculty = currFaculty.parent;
                }
            }
            return result;
        }

    }

    /**
     * For highlighting of trails
     */
    private static class FacultyRoleChangedConverter implements LyricEventConverter {
        Map<Integer, FacultyData> facultyMap;

        public FacultyRoleChangedConverter(Map<Integer,FacultyData> map) {
            this.facultyMap = map;
        }

        @Override
        public boolean isMatch(String name) {
            return "ROLE".equalsIgnoreCase(name);
        }

        @Override
        public List<VisualEvent> convert(double time, String lyric) {
            String[] args = lyric.split(",", 2);

            return Collections.singletonList(new FacultyRoleChanged(
                    time,
                    facultyMap.get(Integer.parseInt(args[0])),
                    BandRole.values()[Integer.parseInt(args[1])]
            ));
        }
    }

    /**
     * For year indicator
     */
    private static class YearChangedConverter implements LyricEventConverter {
        @Override
        public boolean isMatch(String name) {
            return "YEAR".equalsIgnoreCase(name);
        }

        @Override
        public List<VisualEvent> convert(double time, String lyric) {
            String[] args = lyric.split(",", 1);

            return Collections.singletonList(new YearChanged(
                    time,
                    Integer.parseInt(args[0])
            ));
        }
    }
}
