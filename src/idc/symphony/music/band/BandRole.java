package idc.symphony.music.band;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum BandRole {
    Melody((member) -> member::playMainMelody),
    Secondary((member) -> member::playSecondary),
    Carpet1((member) -> member::playCarpet1),
    Carpet2((member) -> member::playCarpet2),
    Carpet3((member) -> member::playCarpet3),
    RhythmRandom(null) {
        private int counter = 0;
        private final GetterProducer getters[] = {
                (member) -> member::playCarpet1,
                (member) -> member::playCarpet2,
                (member) -> member::playCarpet3
        };

        {
            patternGetter = (member) ->
                getters[counter++ % 3].apply(member);
        }
    },
    RhythmIntense((member) -> member::playCarpet3),
    RhythmMedium((member) -> member::playCarpet2),
    RhythmRelaxed((member) -> member::playCarpet1),
    None(null);

    GetterProducer patternGetter;

    public PatternGetter getPattern(BandMember member) {
        return patternGetter.apply(member);
    }

    BandRole(GetterProducer getter) {
        this.patternGetter = getter;
    }

    public interface PatternGetter extends BiFunction<Integer, Key, Pattern>{}
    private interface GetterProducer extends Function<BandMember, PatternGetter>{}
}
