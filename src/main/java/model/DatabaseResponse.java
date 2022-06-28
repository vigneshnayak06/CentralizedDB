package model;

public class DatabaseResponse {
    private boolean success;
    private String msg;
    private Table tableData;

    public DatabaseResponse(){}

    public DatabaseResponse(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Table getTableData() {
        if(tableData == null)
            tableData = new Table();
        return tableData;
    }

    public void setTableData(Table tableData) {
        this.tableData = tableData;
    }
}
