package composition.arguments;

import composition.CompositionContext;

public interface Argument {
    Object value(CompositionContext context);
}
