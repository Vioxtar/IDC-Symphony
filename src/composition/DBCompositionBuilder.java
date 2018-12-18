package composition;

import composition.arguments.Argument;
import composition.functions.CompositionFunction;
import org.jfugue.pattern.Pattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 *      Provides a procedural context for delegating the composition of sequences in the song
 * <br> into arbitrary auxiliary functions.
 * <br>
 * <ul>
 *      <li> Able to support looping over tables to extract different views for each row in table,
 *              via ResultSet stack
 *      <li> Able to support injecting control events into arbitrary points in the song
 *              via control pattern heap
 * </ul>
 */
public class DBCompositionBuilder {
    /**
     * Responsible for merging all separated patterns into one pattern representing the entire song
     */
    // TODO: How do we use composer? What does composer do?
    private Composer composer;

    /**
     * Connection used for SQL Queries
     */
    private Connection dbConnection;

    /**
     * Context for auxiliary functions
     */
    // TODO: Do we need this?
    private CompositionContext context;

    // TODO: Pattern Library - should it be here, or top level? Definitely shouldn't be in composer

    /**
     * Stack of ResultSets for nested loops in complex databases
     */
    private Stack<ResultSet> resultSetStack;

    /**
     * Heap of control event patterns
     */
    private PriorityQueue<ControlPattern> controlPatternHeap;

    public DBCompositionBuilder(Connection dbConnection) throws SQLException {
        this(dbConnection, new Composer());
    }

    public DBCompositionBuilder(Connection dbConnection, Composer composer) throws SQLException
    {
        this.composer = composer;
        this.dbConnection = dbConnection;
        this.context = new CompositionContext(this);
        this.resultSetStack = new Stack<>();
        this.controlPatternHeap = new PriorityQueue<>();
    }

    public Composer getComposer() {
        return composer;
    }
    public Connection getDbConnection() { return dbConnection; }
    public CompositionContext getContext() { return context; }

    /**
     * Executes prepared statement and pushes result into stack if result succeeded
     * @param statement Statement to query
     * @return Whether query succeeded (returned non-empty result)
     * @throws SQLException
     */
    public boolean pushQuery(PreparedStatement statement) throws SQLException {
        ResultSet rs = statement.executeQuery();

        return pushRS(rs);
    }

    /**
     *      Pushes ResultSet into RS Stack
     * <br> Accepts only reset resultsets
     * <br> Sets ResultSet state into first row
     * @param set
     * @return
     */
    public boolean pushRS(ResultSet set) throws SQLException {
        if (set.isBeforeFirst()) {
            set.next();
            resultSetStack.push(set);
            context.setResultSet(set);

            return true;
        }

        return false;
    }

    /**
     * Returns ResultSet currently on top of stack (Also ResultSet in context)
     * @return
     */
    public ResultSet peekRS() {
        if (!resultSetStack.empty()) return resultSetStack.peek();

        return null;
    }

    /**
     * Pops results set from stack, returns null if empty
     * @return
     */
    public ResultSet popRS() {
        if (!resultSetStack.empty()){
            ResultSet pop = resultSetStack.pop();
            context.setResultSet(peekRS());

            return pop;
        }

        return null;
    }

    public void addControl(float time, Pattern pattern) {
        controlPatternHeap.add(new ControlPattern(time, pattern));
    }

    public void removeControl(float time, Pattern pattern) {
        controlPatternHeap.removeIf(p -> {
            return (p.time == time) && (p.pattern == pattern);
        });
    }

    /**
     * Remove all controls of this pattern at any time
     * @param pattern Pattern to remove controls of
     */
    public void removeControls(Pattern pattern) {
        controlPatternHeap.removeIf(p -> {
            return (p.pattern == pattern);
        });
    }



    /**
     * Remove all controls at time
     * @param time Time offset of patterns to remove
     */
    public void removeControls(float time) {
        controlPatternHeap.removeIf(p -> {
            return (p.time == time);
        });
    }

    public void execute(CompositionFunction func, Argument... Arguments) throws SQLException {
        func.call(context, Arguments);
    }

    /**
     * Control Pattern
     *
     */
    private class ControlPattern implements Comparable<ControlPattern> {
        float time;
        Pattern pattern;

        public ControlPattern(float time, Pattern pattern) {
            this.time = time;
            this.pattern = pattern;
        }

        @Override
        public int compareTo(ControlPattern other) {
            return Float.compare(this.time, other.time);
        }
    }
}
