package composition.arguments;

import composition.CompositionContext;

public interface Argument<T> {
    T value(CompositionContext context);
}
