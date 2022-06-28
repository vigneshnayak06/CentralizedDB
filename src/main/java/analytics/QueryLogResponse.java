package analytics;

public class QueryLogResponse {

	private String status;
	private String database;
	private String table;
	private String operation;
	
	public QueryLogResponse(String status, String database, String table, String operation) {
		super();
		this.status = status;
		this.database = database;
		this.table = table;
		this.operation = operation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}
