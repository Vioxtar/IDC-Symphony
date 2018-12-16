package composition.arguments;

import composition.CompositionContext;

/**
 * Constant parameter - always returns value instantiated with,
 * regardless of context.
 * @param <T> Value Type
 */
public class ConstantArgument<T> implements Argument {
    T myValue;

    public ConstantArgument(T value) {
        this.myValue = value;
    }

    @Override
    public T value(CompositionContext context) {
        return myValue;
    }
}
