package Mapper.Variable.Interpolations;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InterpolatorFactoryTest {
    @Test
    void makeInterpolator() {
        Map<String, InterpolatorFactory> interpolatorCreators;

        // Instantiate map with factories
        interpolatorCreators = new HashMap<>();

        interpolatorCreators.put(ExponentialInterpolator.getInterpolatorName(),
                ExponentialInterpolator::new);
        interpolatorCreators.put(LinearInterpolator.getInterpolatorName(),
                (float arg) -> new LinearInterpolator());

        // Use factories
        Interpolator exp = interpolatorCreators.get("Exp").makeInterpolator(1);
        Interpolator lin = interpolatorCreators.get("Lin").makeInterpolator(0);

        // Assert difference in implementations
        assertNotEquals(exp.interpolate(0.5f), lin.interpolate(0.5f));
    }

}