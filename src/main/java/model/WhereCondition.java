package model;

import Constants.Operation;

public class WhereCondition {
    private String column;
    private Operation operation;
    private String value;

    public WhereCondition(){}

    public WhereCondition(String column, String value) {
        this.column = column;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "WhereCondition{" +
                "column='" + column + '\'' +
                ", operation=" + operation +
                ", value='" + value + '\'' +
                '}';
    }
}
