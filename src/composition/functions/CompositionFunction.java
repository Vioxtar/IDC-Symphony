package composition.functions;

import composition.CompositionContext;
import composition.arguments.Argument;

import java.sql.SQLException;

public abstract class CompositionFunction {
    CompositionContext currentContext;

    public void call(CompositionContext context, Argument... Arguments) throws SQLException {
        currentContext = context;
    }

    protected final float getFloat(Argument arg) {
        Object var = arg.value(currentContext);

        if (Float.class.isInstance(var)) {
            return (float)var;
        }

        return Float.NaN;
    }


}
