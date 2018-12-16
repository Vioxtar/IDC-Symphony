package composition.variables;

import composition.CompositionContext;
import composition.variables.Interpolations.Interpolator;

public class InterpolatedVariable implements Variable {
    private Interpolator interpolator;

    private float startTime;
    private float endTime;
    private float startVal;
    private float endVal;


    public InterpolatedVariable(float startTime, float endTime, float startVal, float endVal, Interpolator interp) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startVal = startVal;
        this.endVal = endVal;

        this.interpolator = interp;
    }


    @Override
    public Object getValue(CompositionContext context) {
        return interpolator.interpolate(
                startVal,
                endVal,
                (context.getTime() - startTime) / (endTime - startTime));
    }
}
