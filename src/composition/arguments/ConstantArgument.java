package composition.arguments;

import composition.CompositionContext;

/**
 * Constant parameter - always returns value instantiated with,
 * regardless of context.
 */
public class ConstantArgument implements Argument {
    Object myValue;

    public ConstantArgument(Object value) {
        this.myValue = value;
    }

    @Override
    public Object value(CompositionContext context) {
        return myValue;
    }
}
