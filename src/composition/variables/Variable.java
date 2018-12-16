package composition.variables;

import composition.CompositionContext;

public interface Variable {
    Object getValue(CompositionContext context);
}
