package Interpolations;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InterpolatorTest {
    static Interpolator testInterpolator;

    Float[] edges = {0f, 1f};
    Float[] beyondEdge = {-1f, +1f};
    Float[] vars = {0.05f, 0.1f, 0.25f, 0.333f, 0.555f, 0.8f, 0.9f};
    Float epsilon = 0.001f;

    @BeforeAll
    static void setUp() {
        testInterpolator = new LinearInterpolator();
    }

    @Test
    void testEdges() {
        for (int i = 0; i < edges.length; i++) {
            assertTrue(equalsEpsilon(edges[i], testInterpolator.interpolate(edges[i])));
        }
    }

    @Test
    void testBeyondEdges() {
        for (int i = 0; i < edges.length; i++) {
            assertTrue(equalsEpsilon(edges[i], testInterpolator.interpolate(edges[i] + beyondEdge[i])));
        }
    }

    boolean equalsEpsilon(float a, float b) {
        float diff = Math.abs(a - b);

        return diff < epsilon;
    }
}