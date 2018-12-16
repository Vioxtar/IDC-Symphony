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
        return context.getVariable(varname);
    }
}
