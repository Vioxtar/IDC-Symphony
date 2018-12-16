package composition.variables.Interpolations;

public class ExponentialInterpolator extends Interpolator {
    private float coef;

    public ExponentialInterpolator(float coef) {
        this.coef = coef;
    }

    @Override
    public float interpolate(float t) {
        t = clamp(t);

        // coef 0 leads to division by 0, function approaches linear interpolation
        if (Math.signum(t) == 0) return t;

        // Exponential interpolation formula (e^kt - 1) / (e^k - 1)
        t = (float)((Math.exp(coef * t) - 1) / (Math.exp(coef) - 1));

        return t;
    }

    public static String getInterpolatorName() {
        return "Exp";
    }
}
