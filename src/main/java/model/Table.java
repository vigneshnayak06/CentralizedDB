package model;

import java.util.ArrayList;

public class Table {
    private String[] columns;
    private ArrayList<String[]> rows;
    private int columnCount;


    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public ArrayList<String[]> getRows() {
        if(rows == null || rows.isEmpty()){
            rows = new ArrayList<>();
        }
        return rows;
    }

    public void setRows(ArrayList<String[]> rows) {
        this.rows = rows;
    }

    public void setRow(String[] rows) {
        if(this.rows == null){
            this.rows = new ArrayList<>();
        }
        this.rows.add(rows);
    }
    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
}
