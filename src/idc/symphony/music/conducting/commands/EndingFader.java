package idc.symphony.music.conducting.commands;

import idc.symphony.music.band.Band;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.shared.ContextExtension;
import idc.symphony.music.transformers.FadingTransformer;
import org.jfugue.pattern.Pattern;
import org.staccato.StaccatoParserListener;

import java.util.Optional;

public class EndingFader implements Command {
    ContextExtension context;

    public EndingFader(ContextExtension context) {
        this.context = context;
    }


    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        if (state.getCurrentYear() == state.getStructure().getMaxYear()) {
            Optional<Integer> faculty =
                    state.getFacultyMap().keySet().stream().filter(
                            (facultyID) -> state.sequenceContext().facultyRole(facultyID) == BandRole.Melody).findFirst();

            if (faculty.isPresent()) {
                int track = state.getBand().bandMembers().get(faculty.get()).getTargetTrack();
                int section = state.getCurrentSequence();

                Pattern melody = state.getComposition().getPattern(track, section);
                melody = fade(
                        melody,
                        state.getWholesPerSequence() - 1,
                        state.getWholesPerSequence() + 2,
                        state.getWholesPerSequence() - 0.25);


                state.getComposition().put(track, section, melody);
            }

        }

        return (state.getCurrentYear() <= state.getStructure().getMaxYear());
    }

    private Pattern fade(Pattern pattern, double fadeStart, double fadeEnd, double cutoffPoint) {

        StaccatoParserListener builder = new StaccatoParserListener();
        FadingTransformer fader = new FadingTransformer(
                fadeStart,
                fadeEnd,
                cutoffPoint);
        fader.addParserListener(builder);
        pattern.transform(fader);
        fader.removeParserListener(builder);

        return builder.getPattern();
    }
}
