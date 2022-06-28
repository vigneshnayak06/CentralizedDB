package analytics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Constants.QueryConstants;
import exceptions.DatabaseException;
import services.MetadataServices;
import services.MetadataServicesImpl;

public class AnalyticsQueryProcessor {

	static String QUERY_LOG_FILE = "./src/main/resources/logs/query_logs.txt";

	private static List<QueryLogResponse> queryLogResponse = new ArrayList<QueryLogResponse>();

	private static String loggedInUser = QueryConstants.CURRENT_USER;
	
	private MetadataServices metaServices = new MetadataServicesImpl();

	public AnalyticsQueryProcessor() {

		readQueryLog();
	}

	public static void readQueryLog() {

		try (BufferedReader br = new BufferedReader(new FileReader(QUERY_LOG_FILE))) {
			String line;
			while ((line = br.readLine()) != null) {

				ArrayList<String> modifiedFields = new ArrayList<String>();

				String[] fields = line.split("~");
				for (String field : fields) {

					modifiedFields.add(field.substring(field.indexOf(":") + 1));
				}

				String user = modifiedFields.get(1);

				if (user.equalsIgnoreCase(loggedInUser)) {

					String status = modifiedFields.get(2);
					String database = modifiedFields.get(4);

					QueryLogResponse response;

					if (status.equalsIgnoreCase("invalid")) {

						response = new QueryLogResponse(status, database, "", "");

					} else {

						String table = modifiedFields.get(5);
						String operation = modifiedFields.get(6);

						response = new QueryLogResponse(status, database, table, operation);
					}

					queryLogResponse.add(response);
				}
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void processCountQueries(String query) {

		query = query.replace(";", "");
		String[] fields = query.split(" ");
		String database = fields[2];
		List<QueryLogResponse> response = AnalyticsQueryPredicates.filterQueries(queryLogResponse,
				AnalyticsQueryPredicates.countQueries(database));
		String result = "User " + loggedInUser + " submitted " + response.size() + " queries on " + database;
		System.out.println(result);
		String message = "Query:" + query + ";" + "~" + "Result:" + result+"!";
		AnalyticsWriter.addAnalyticsLog(loggedInUser, message);

	}

	public void processCountQueriesByStatus(String query) {

		query = query.replace(";", "");
		String[] fields = query.split(" ");
		String status = fields[1];
		String database = fields[3];
		List<QueryLogResponse> response = AnalyticsQueryPredicates.filterQueries(queryLogResponse,
				AnalyticsQueryPredicates.countQueriesByStatus(database, status));

		String result = "User " + loggedInUser + " submitted " + response.size() + " " + status + " queries on "
				+ database;
		System.out.println(result);
		String message = "Query:" + query + ";" + "~" + "Result:" + result+"!";
		AnalyticsWriter.addAnalyticsLog(loggedInUser, message);
	}

	public void processCountQueriesByOperation(String query) {

		List<String> tables = new ArrayList<String>();
		
		query = query.replace(";", "");
		String[] fields = query.split(" ");
		String operation = fields[1];
		String database = fields[3];
		String message = "Query:"+query+";"+"~Result:";
		
		try {
			tables = metaServices.getTables(database);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String table: tables) {
			List<QueryLogResponse> response = AnalyticsQueryPredicates.filterQueries(queryLogResponse,
					AnalyticsQueryPredicates.countQueriesByOperation(database, operation, table));
			String result = "User " + loggedInUser + " performed " + response.size() + " " + operation + " queries on "
					+ table + " in "+ database;
			System.out.println(result);
			message += result + "!";
		}
		AnalyticsWriter.addAnalyticsLog(loggedInUser, message);
	}
}
