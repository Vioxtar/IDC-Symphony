package Interpolations;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class LinearInterpolatorTest extends InterpolatorTest {
    Float[] expected = vars;

    @BeforeAll
    static void setUpLin() {
        testInterpolator = new LinearInterpolator();
    }

    @Test
    void testVarShuffle() {
        long seed = 102049568269491L;

        List<Float> varsList = Arrays.asList(vars);
        List<Float> expList = Arrays.asList(expected);

        Collections.shuffle(varsList, new Random(seed));
        Collections.shuffle(expList, new Random(seed));

        for (int i = 0; i < varsList.size(); i++) {
            assertTrue(equalsEpsilon(expList.get(i), testInterpolator.interpolate(varsList.get(i))));
        }
    }


}