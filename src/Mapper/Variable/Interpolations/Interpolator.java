package Mapper.Variable.Interpolations;

public abstract class Interpolator {
    /**
     * Interpolates a linear percentage variable to its resulting true percentage
     * @param t - Variable [0.0 .. 1.0]
     * @return Result in [0.0 .. 1.0]
     */
    public abstract float interpolate(float t);

    /**
     * Interpolates a linear percentage variable to its resulting proportion between start and end
     * @param start Minimum Value
     * @param end Maximum Value
     * @param t
     * @return
     */
    public float interpolate(float start, float end, float t) {
        return (start + (end - start) * interpolate(t));
    }

    /**
     * Clamps a given value to between 0 and 1
     * @param t float value
     * @return value between 0 and 1 (0 if t is negative, 1 if t is greater than 1)
     */
    protected static float clamp(float t) {
        return Math.min(1, Math.max(0, t));
    }
}
