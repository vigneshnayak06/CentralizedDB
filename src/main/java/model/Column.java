package model;

import Constants.Datatype;

import java.util.Arrays;

public class Column {
    private String columnName;
    private Datatype datatype;
    private String[] constraints;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public void setDatatype(Datatype datatype) {
        this.datatype = datatype;
    }

    public String[] getConstraints() {
        return constraints;
    }

    public void setConstraints(String[] constraints) {
        this.constraints = constraints;
    }

    @Override
    public String toString() {
        return "Column{" +
                "columnName='" + columnName + '\'' +
                ", datatype=" + datatype +
                ", constraints=" + Arrays.toString(constraints) +
                '}';
    }
}
