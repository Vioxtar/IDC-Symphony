package composition.arguments;

import composition.CompositionContext;
import composition.variables.Variable;

public class VarArgument implements Argument {
    private String varname;

    public VarArgument(String varname) {
        this.varname = varname;
    }

    @Override
    public Object value(CompositionContext context) {
        if (!context.getVariables().containsKey(varname)) {
            return null;
        }

        return context.getVariables().get(varname).getValue(context);
    }
}
