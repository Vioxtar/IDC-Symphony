package composition.functions;

import composition.CompositionContext;
import composition.arguments.Argument;

import java.sql.SQLException;

public interface CompositionFunction {
    void call(CompositionContext context) throws SQLException;
}
