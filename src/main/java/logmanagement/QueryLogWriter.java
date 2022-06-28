package logmanagement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

import Constants.QueryConstants;

public class QueryLogWriter {
	
	private static String loggedInUser = QueryConstants.CURRENT_USER;
	
	public static void addQueryLog(String message) {
		
		String QUERY_LOG_FILE = "./src/main/resources/logs/query_logs.txt";
		
		File file = new File(QUERY_LOG_FILE);	
		
		if (!file.exists()) {
			try {
				boolean isNewFileCreated = file.createNewFile();
				if (isNewFileCreated) {
					addLog(message, file);
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		else {
			addLog(message,file);
		}	
	}
	
	private static void addLog(String message, File file) {
		
		try (FileWriter writer = new FileWriter(file,true)) {
		
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			writer.append("TimeStamp:" + timestamp.toString());
			writer.append("~");
			writer.append("User:"+loggedInUser);
			writer.append("~");
			writer.append(message);
			writer.append("\n");
		}
		
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
