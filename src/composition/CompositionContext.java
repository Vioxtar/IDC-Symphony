package composition;

import composition.variables.Variable;
import org.jfugue.pattern.Pattern;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * DB Composition Context required for procedural mapping from data to composition
 */
public class CompositionContext {
    /**
     * Builder in charge of this composition context
     */
    private DBCompositionBuilder builder;

    /**
     * Current time in song being handled
     * (Can be changed arbitrarily by functions, Composer takes care of inserting patterns into correct time)
     */
    private float time;

    /**
     * Current ResultSet being handled
     */
    private ResultSet resultSet;

    /**
     * Current pattern being handled/generated/transformed
     */
    private Pattern pattern;

    /**
     * Variable Map for saving composition-specific data, such as sequence length
     * Uses HashMap for efficiency
     */
    private Map<String, Variable> variables;

    public CompositionContext(DBCompositionBuilder builder) {
        this.builder = builder;
        this.time = 0;
        this.variables = new HashMap<>();
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    void setResultSet(ResultSet rs) {
        resultSet = rs;
    };

    public Map<String, Variable> getVariables() {
        return variables;
    }

    public DBCompositionBuilder getBuilder() {
        return builder;
    }
}
