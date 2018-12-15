package Mapper;

import Mapper.Variable.Variable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class MapperContext {
    private float currentTime;
    private ResultSet currentResultSet;
    private Map<String, Variable> variables;

    public MapperContext() {
        currentTime = 0;
        variables = new HashMap<>();
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public ResultSet getCurrentResultSet() {
        return currentResultSet;
    }

    public Map<String, Variable> getVariables() {
        return variables;
    }
}
