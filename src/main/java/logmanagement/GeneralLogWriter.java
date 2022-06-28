package logmanagement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import exceptions.DatabaseException;
import services.MetadataServices;
import services.MetadataServicesImpl;

public class GeneralLogWriter {

	private static void addGeneralLog(String message) {

		String GENERAL_LOG_FILE = "./src/main/resources/logs/general_logs.txt";

		File file = new File(GENERAL_LOG_FILE);	

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
			writer.append(message);
			writer.append("\n");
		}
		
		catch (final IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void addMetadata() {
		MetadataServices meta = new MetadataServicesImpl();	
		List<String> databases = new ArrayList<String>();
		String message = "";
		try {
			databases = meta.getDatabases();
			for (String database:databases) {
				message+="DatabaseName:"+database+"|";
				List<String> tables = new ArrayList<String>();
				tables = meta.getTables(database);
				message+="NoOfTables:"+tables.size()+"|Tables:{";
				message+= String.join(",", tables);
				message+="}~";
			}
			
		addGeneralLog(message);
		
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
}
