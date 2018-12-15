package Mapper.Variable.Interpolations;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExponentialInterpolatorTest extends InterpolatorTest {
    Float[] expected_coef2 = {0.0164f, 0.0346f, 0.1015f, 0.1481f, 0.3184f, 0.6187f, 0.7903f};
    Float[] expected_coefneg2 = {0.1100f, 0.2096f, 0.4550f, 0.5623f, 0.7753f, 0.9230f, 0.9653f};

    @BeforeAll
    static void setUpExp() {
        testInterpolator = new ExponentialInterpolator(1);
    }

    @Test
    void testVarShuffleCoef2() {
        long seed = 102049568269491L;

        List<Float> varsList = Arrays.asList(vars);
        List<Float> expList = Arrays.asList(expected_coef2);

        Collections.shuffle(varsList, new Random(seed));
        Collections.shuffle(expList, new Random(seed));

        Interpolator test = new ExponentialInterpolator(2);

        for (int i = 0; i < varsList.size(); i++) {
            assertTrue(equalsEpsilon(expList.get(i), test.interpolate(varsList.get(i))));
        }
    }

    @Test
    void testVarShuffleCoefNeg2() {
        long seed = 102049568269491L;

        List<Float> varsList = Arrays.asList(vars);
        List<Float> expList = Arrays.asList(expected_coefneg2);

        Collections.shuffle(varsList, new Random(seed));
        Collections.shuffle(expList, new Random(seed));

        Interpolator test = new ExponentialInterpolator(-2);

        for (int i = 0; i < varsList.size(); i++) {
            assertTrue(equalsEpsilon(expList.get(i), test.interpolate(varsList.get(i))));
        }
    }
}