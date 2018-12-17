package composition.arguments;

import composition.CompositionContext;

import java.sql.SQLException;

public class ResultSetArgument implements Argument {
    private boolean useName;
    private String columnName;
    private int    columnIndex;

    public ResultSetArgument(String columnName) {
        this.columnName = columnName;
        useName = true;
    }

    public ResultSetArgument(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public Object value(CompositionContext context) {
        
        try {
            return useName
                    ? context.getResultSet().getObject(columnName)
                    : context.getResultSet().getObject(columnIndex);

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
