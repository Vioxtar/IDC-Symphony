package Mapper.Variable.Interpolations;

public class LinearInterpolator extends Interpolator {
    @Override
    public float interpolate(float t) {
        return clamp(t);
    }

    public static String getInterpolatorName() {
        return "Lin";
    }
}
