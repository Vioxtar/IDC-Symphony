package sql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *  TODO: Work in Progress
 *  Just an option, could be fully implemented if we see a need to dynamically create simple select queries.
 */
public class SimpleSelect {
    private static Pattern SQL_NAME_REGEX =
            Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");

    private static String PREFIX = "SELECT ";

    private List<String> columnNames;
    private String tableName;
    private String tableAlias;



    private SimpleSelect() {
        columnNames = new ArrayList<>();
    }

    private void addColumn(String columnName) {

    }

    private void setTable(String tableName) {

    }



    private static class TableJoin {
        private enum JoinType {
            INNER,
            LEFT,
            RIGHT,
            FULL,
            CROSS
        }

        JoinType joinType;
        String joinedTable;

        TableJoin(String name){
            this(name, JoinType.INNER);
        }

        TableJoin(String name, JoinType joinType) {
            this.joinedTable = name;
            this.joinType = joinType;
        }

        @Override
        public String toString() {
            StringBuilder buddy = new StringBuilder();
            buddy.append(joinType.name());
            buddy.append(" ");
            buddy.append(joinedTable);

            return buddy.toString();
        }
    }
}
