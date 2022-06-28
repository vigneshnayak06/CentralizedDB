package analytics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class AnalyticsWriter {
	
	public static void addAnalyticsLog(String user, String message) {

		String ANALYTICS_LOG_FILE = "./src/main/resources/logs/analytics_logs.txt";

		File file = new File(ANALYTICS_LOG_FILE);

		if (!file.exists()) {
			try {
				boolean isNewFileCreated = file.createNewFile();
				if (isNewFileCreated) {
					addLog(user, message, file);
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		} else {
			addLog(user, message, file);
		}
	}

	private static void addLog(String user, String message, File file) {

		try (FileWriter writer = new FileWriter(file, true)) {

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			writer.append("TimeStamp:" + timestamp.toString());
			writer.append("~");
			writer.append("User:"+user);
			writer.append("~");
			writer.append(message);
			writer.append("\n");
		}

		catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
