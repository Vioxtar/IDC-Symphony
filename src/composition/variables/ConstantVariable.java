package composition.variables;

import composition.CompositionContext;

public class ConstantVariable implements Variable {
    private Object value;

    public ConstantVariable(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue(CompositionContext context) {
        return value;
    }
}
