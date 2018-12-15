package Mapper.Variable;

import Mapper.MapperContext;
import Mapper.Variable.Interpolations.Interpolator;

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
    public float getValue(MapperContext context) {
        return interpolator.interpolate(
                startVal,
                endVal,
                (context.getCurrentTime() - startTime) / (endTime - startTime));
    }
}
