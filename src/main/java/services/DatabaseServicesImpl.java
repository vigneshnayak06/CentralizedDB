package services;

import Constants.Operation;
import Constants.QueryConstants;
import Utility.Utility;
import exceptions.DatabaseException;
import logmanagement.EventLogWriter;
import model.Column;
import model.DatabaseResponse;
import model.Table;
import model.WhereCondition;
import org.apache.commons.io.FileUtils;

//import javax.rmi.CORBA.Util;
import java.io.*;
import java.util.*;

public class DatabaseServicesImpl implements DatabaseServices{

    private static int lock = 0;
    private final MetadataServices metadataServices;
    private final ValidationServices validationServices;

    public DatabaseServicesImpl(){
        metadataServices = new MetadataServicesImpl();
        validationServices = new ValidationServicesImpl();
    }

    @Override
    public DatabaseResponse createDatabase(String dbName) {
        if(checkLock()) {
            QueryConstants.DB_PATH = QueryConstants.TRANSACTION_DB_PATH;
        }
        String dbPath = QueryConstants.DB_PATH +dbName;
        File file = new File(dbPath);
        if(file.exists()) {
            Utility.displayMessage("Database already exist");
            return new DatabaseResponse(false,"Database already exist");
        }
        boolean  isCreated = file.mkdir();
        Utility.displayMessage("Database created successfully");
        
    	//Event Logs
        String eventLogMessage = "Database:"+dbName+" created";
        EventLogWriter.addEventLog(eventLogMessage);
        
        return new DatabaseResponse(isCreated,"Database created successfully");
    }

    @Override
    public DatabaseResponse useDatabase(String dbName) {
        boolean fileExist = new File(QueryConstants.DB_PATH+dbName).exists();
        if(fileExist) {
            QueryConstants.CURRENT_DB = dbName;
            Utility.displayMessage("Selected the database");
            System.out.println();
        	
            //Event Logs
            String eventLogMessage = "Database:"+dbName+" selected";
            EventLogWriter.addEventLog(eventLogMessage);
            
            return new DatabaseResponse(true,"Selected the database"+dbName);
            
        } else {
            Utility.displayMessage("Database does not exist");
            return new DatabaseResponse(false,"Database does not exist");
        }
    }

    @Override
    public DatabaseResponse createTable(String tableName, List<Column> columns) throws IOException {
        if(checkLock()){
            QueryConstants.DB_PATH = QueryConstants.TRANSACTION_DB_PATH;
        }
        if(QueryConstants.CURRENT_DB=="") {
            Utility.displayMessage("Please select a database");
            return new DatabaseResponse(false,"Please select a database");
        }
        String tablePath = QueryConstants.DB_PATH +QueryConstants.CURRENT_DB+"/"+tableName+".tsv";
        File file = new File(tablePath);
        if(file.exists()) {
            Utility.displayMessage("Table exists");
            return new DatabaseResponse(false,"Table already exists");
        }
        boolean isCreated = false;
        try {
            isCreated = file.createNewFile();

        } catch (IOException e) {
        	
        	//Event Logs
            String eventLogMessage = "Database:"+QueryConstants.CURRENT_DB+" Table:"+tableName+" creation failed.";
            EventLogWriter.addEventLog(eventLogMessage);
            
            e.printStackTrace();
        }

        FileWriter columnNameWriter = new FileWriter(tablePath, true);
        StringBuilder sb = new StringBuilder();

        for(Column columnName: columns) {
            sb.append(columnName.getColumnName()+"~");
        }
        columnNameWriter.write(sb.toString() + "\n");
        columnNameWriter.close();
        //Insert Meta Table
        Table table = new Table();
        table.setRow(new String[]{QueryConstants.CURRENT_DB,tableName,"0"});
        metadataServices.insertTableDetailsTable(table);

        //Insert Meta Columns
        table = new Table();
        for (Column column:columns) {
            table.setRow(new String[]{
                    tableName,
                    column.getColumnName(),
                    column.getDatatype().toString(),
                    column.getConstraints() == null ? "" : String.join(",", column.getConstraints())});
        }
        metadataServices.insertColumnDetailsTable(table);
        Utility.displayMessage("Created");
        
    	//Event Logs
        String eventLogMessage = "Database:"+QueryConstants.CURRENT_DB+" Table:"+tableName+" Created";
        EventLogWriter.addEventLog(eventLogMessage);
        
        return new DatabaseResponse(true,"Created "+tableName);
    }

    @Override
    public DatabaseResponse insertTable(String tableName, Table tableDate) throws IOException, DatabaseException {
        if(checkLock()) {
            QueryConstants.DB_PATH = QueryConstants.TRANSACTION_DB_PATH;
        }
        if(!validationServices.validateInsertData(tableName, tableDate)){
            Utility.displayMessage("Table Constraint Failure");
            return new DatabaseResponse(false,"Table Constraint Failure");
        }
        if(QueryConstants.CURRENT_DB=="") {
            Utility.displayMessage("Please select a database");
            return new DatabaseResponse(false,"Please select a database");
        }

        String tablePath = QueryConstants.DB_PATH +QueryConstants.CURRENT_DB+"/"+tableName+".tsv";

        File file = new File(tablePath);
        if(!file.exists()) {
            Utility.displayMessage("Table doesn't exist");
            return new DatabaseResponse(false,"Table doesn't exist");
        }
        FileWriter columnNameWriter = new FileWriter(tablePath, true);
        StringBuilder insertValuesStringBuilder = new StringBuilder();

        ArrayList<String[]> rowVals = tableDate.getRows();
        for (String[] row: rowVals) {
            for (String rowV : row) {
                insertValuesStringBuilder.append(rowV + "~");
            }
            insertValuesStringBuilder.append("\n");
        }

        columnNameWriter.write(insertValuesStringBuilder.toString());
        columnNameWriter.close();
        Utility.displayMessage("Inserted successfully");
        
    	//Event Logs
        String eventLogMessage = "Database:"+QueryConstants.CURRENT_DB+" Table:"+tableName+" updated";
        EventLogWriter.addEventLog(eventLogMessage);
        
        return new DatabaseResponse(true,"Inserted successfully"+tableName);
    }

    // select * from users
    // select * from users where id = 5
    // select name from users
    // select name from users where id>5

    @Override
    public DatabaseResponse selectTable(String tableName, String columns, WhereCondition whereCondition) throws IOException, DatabaseException {
        if(checkLock()) {
            QueryConstants.DB_PATH = QueryConstants.TRANSACTION_DB_PATH;
        }
        if(!validationServices.validateWhereCondition(tableName, whereCondition)){
            Utility.displayMessage("WhereCondition Constraint Failure");
            return new DatabaseResponse(false,"WhereCondition Constraint Failure");
        }
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int counterColumn = 0;
        int indexOfColumnUserRequested = 0;
        int greaterThanCounterColumn = 0;
        int greaterThanIndexOfColumnUserRequested  = 0;
        int lessThanCounterColumn = 0;
        int lessThanIndexOfColumnUserRequested  = 0;
        int greaterThanCounterColumn2 = 0;
        int greaterThanIndexOfColumnUserRequested2  = 0;
        int lessThanCounterColumn2 = 0;
        int lessThanIndexOfColumnUserRequested2  = 0;

        if(QueryConstants.CURRENT_DB=="") {
            Utility.displayMessage("Please select a database");
            return new DatabaseResponse(false,"Please select a database");
        }

        String tablePath = QueryConstants.DB_PATH +QueryConstants.CURRENT_DB+"/"+tableName+".tsv";

        File file = new File(tablePath);
        if(!file.exists()) {
            Utility.displayMessage("Table doesn't exist");
            return new DatabaseResponse(false,"Table doesn't exist");
        }

        if(columns.contentEquals("*")) {
            if (whereCondition != null) {
                if (whereCondition.getOperation().equals(Operation.GREATER_THAN)) {
                    // GREATER THAN
                    // select * from users where id>5 //name and id are columns
                    String whereConditionValue = whereCondition.getValue();
                    String whereConditionColumn = whereCondition.getColumn();

                    BufferedReader columnLineReader = new BufferedReader(new FileReader(tablePath));
                    String firstLine = columnLineReader.readLine();
                    String[] columnsFromFirstLine = firstLine.split("~");
                    columnLineReader.close();

                    for (String column: columnsFromFirstLine) {
                        if (column.contentEquals(whereConditionColumn)) {
                            greaterThanIndexOfColumnUserRequested = greaterThanCounterColumn;
                        }
                        greaterThanCounterColumn += 1;
                    }
                    System.out.println(greaterThanIndexOfColumnUserRequested);
                    System.out.println(Integer.parseInt(whereConditionValue));
                    BufferedReader selectReader = new BufferedReader(new FileReader(tablePath));
                    // skip reading first line
                    selectReader.readLine();
                    String lineFromSelectReader = null;
                    while ((lineFromSelectReader = selectReader.readLine()) != null) {
                        String[] greaterThanLineParts = lineFromSelectReader.split("~");
                        if (Integer.parseInt(greaterThanLineParts[greaterThanIndexOfColumnUserRequested]) > Integer.parseInt(whereConditionValue)) {
                            System.out.println(lineFromSelectReader);
                        }
                    }
                    selectReader.close();

                } else if (whereCondition.getOperation().equals(Operation.LESS_THAN)) {
                    // LESS THAN
                    // select * from users where id<5 //name and id are columns
                    String whereConditionValue = whereCondition.getValue();
                    String whereConditionColumn = whereCondition.getColumn();

                    BufferedReader columnLineReader = new BufferedReader(new FileReader(tablePath));
                    String firstLine = columnLineReader.readLine();
                    String[] columnsFromFirstLine = firstLine.split("~");
                    columnLineReader.close();

                    for (String column: columnsFromFirstLine) {
                        if (column.contentEquals(whereConditionColumn)) {
                            lessThanIndexOfColumnUserRequested = lessThanCounterColumn;
                        }
                        lessThanCounterColumn += 1;
                    }
                    BufferedReader selectReader = new BufferedReader(new FileReader(tablePath));
                    // skip reading first line
                    selectReader.readLine();
                    String lineFromSelectReader = null;
                    while ((lineFromSelectReader = selectReader.readLine()) != null) {
                        String[] greaterThanLineParts = lineFromSelectReader.split("~");
                        if (Integer.parseInt(greaterThanLineParts[lessThanIndexOfColumnUserRequested]) < Integer.parseInt(whereConditionValue)) {
                            System.out.println(lineFromSelectReader);
                        }
                    }
                    selectReader.close();
                } else {
                    // EQUAL TO
                    // select * from users where name = "vig"
                    String value = whereCondition.getValue();
                    String column = whereCondition.getColumn();

                    BufferedReader selectReader = new BufferedReader(new FileReader(tablePath));
                    String lineFromSelectReader = null;
                    while ((lineFromSelectReader = selectReader.readLine()) != null) {
                        if (lineFromSelectReader.contains(value)) {
                            //print the entire line
                            System.out.println(lineFromSelectReader);
                        }
                    }
                    selectReader.close();
                }
            } else {
                BufferedReader selectReader = new BufferedReader(new FileReader(tablePath));
                String lineFromSelectReader = null;
                while((lineFromSelectReader=selectReader.readLine())!=null) {
                    System.out.println(lineFromSelectReader);
                }
                selectReader.close();
            }
        } else {
            String[] splitColumns = columns.split(","); // name,id
            if (splitColumns.length == 1) {
                if (whereCondition == null) {
                    BufferedReader columnLineReader = new BufferedReader(new FileReader(tablePath));
                    String firstLine = columnLineReader.readLine();
                    String[] columnsFromFirstLine = firstLine.split("~");
                    columnLineReader.close();

                    for (String column : columnsFromFirstLine) {
                        if (column.contentEquals(splitColumns[0])) {
                            indexOfColumnUserRequested = counterColumn;
                        }
                        counterColumn += 1;
                    }

                    BufferedReader selectReader = new BufferedReader(new FileReader(tablePath));
                    String lineFromSelectReader = null;
                    String row = "";
                    while ((lineFromSelectReader = selectReader.readLine()) != null) {
                        String[] rowWords = lineFromSelectReader.split("~");
                        System.out.println(rowWords[indexOfColumnUserRequested]);
                    }
                    selectReader.close();
                }
                else if (whereCondition.getOperation().equals(Operation.GREATER_THAN)) {
                    // select name from users where id>5 // name and id are columns

                    BufferedReader columnLineReader = new BufferedReader(new FileReader(tablePath));
                    String firstLine = columnLineReader.readLine();
                    String[] columnsFromFirstLine = firstLine.split("~");
                    columnLineReader.close();

                    for (String column : columnsFromFirstLine) {
                        if (column.contentEquals(splitColumns[0])) {
                            indexOfColumnUserRequested = counterColumn;
                        }
                        counterColumn += 1;
                    }

                    BufferedReader selectReader = new BufferedReader(new FileReader(tablePath));
                    String lineFromSelectReader = null;
                    String row = "";
                    while ((lineFromSelectReader = selectReader.readLine()) != null) {
                        if (lineFromSelectReader.contains(whereCondition.getValue())) {
                            row = lineFromSelectReader;
                            break;
                        }
                    }

                    /////////////////
                    String whereConditionValue = whereCondition.getValue();
                    String whereConditionColumn = whereCondition.getColumn();

                    BufferedReader columnLineReader2 = new BufferedReader(new FileReader(tablePath));
                    String firstLine2 = columnLineReader2.readLine();
                    String[] columnsFromFirstLine2 = firstLine2.split("~");
                    columnLineReader2.close();

                    for (String column: columnsFromFirstLine2) {
                        if (column.contentEquals(whereConditionColumn)) {
                            greaterThanIndexOfColumnUserRequested2 = greaterThanCounterColumn2;
                        }
                        greaterThanCounterColumn2 += 1;
                    }
//                   System.out.println(greaterThanIndexOfColumnUserRequested2);
//                   System.out.println(Integer.parseInt(whereConditionValue));
                    BufferedReader selectReader2 = new BufferedReader(new FileReader(tablePath));
                    // skip reading first line
                    selectReader2.readLine();
                    String lineFromSelectReader2 = null;
                    while ((lineFromSelectReader2 = selectReader2.readLine()) != null) {
                        String[] greaterThanLineParts = lineFromSelectReader2.split("~");
                        if (Integer.parseInt(greaterThanLineParts[greaterThanIndexOfColumnUserRequested2]) > Integer.parseInt(whereConditionValue)) {
                            String[] rowWords = lineFromSelectReader2.split("~");
                            System.out.println(rowWords[indexOfColumnUserRequested]);
                        }
                    }
                    selectReader.close();

                } else if (whereCondition.getOperation().equals(Operation.LESS_THAN)) {

                    // select name from users where id<5 // name and id are columns

                    BufferedReader columnLineReader = new BufferedReader(new FileReader(tablePath));
                    String firstLine = columnLineReader.readLine();
                    String[] columnsFromFirstLine = firstLine.split("~");
                    columnLineReader.close();

                    for (String column : columnsFromFirstLine) {
                        if (column.contentEquals(splitColumns[0])) {
                            indexOfColumnUserRequested = counterColumn;
                        }
                        counterColumn += 1;
                    }

                    BufferedReader selectReader = new BufferedReader(new FileReader(tablePath));
                    String lineFromSelectReader = null;
                    String row = "";
                    while ((lineFromSelectReader = selectReader.readLine()) != null) {
                        if (lineFromSelectReader.contains(whereCondition.getValue())) {
                            row = lineFromSelectReader;
                            break;
                        }
                    }

                    /////////////////
                    String whereConditionValue = whereCondition.getValue();
                    String whereConditionColumn = whereCondition.getColumn();

                    BufferedReader columnLineReader2 = new BufferedReader(new FileReader(tablePath));
                    String firstLine2 = columnLineReader2.readLine();
                    String[] columnsFromFirstLine2 = firstLine2.split("~");
                    columnLineReader2.close();

                    for (String column: columnsFromFirstLine2) {
                        if (column.contentEquals(whereConditionColumn)) {
                            greaterThanIndexOfColumnUserRequested2 = greaterThanCounterColumn2;
                        }
                        greaterThanCounterColumn2 += 1;
                    }
//                   System.out.println(greaterThanIndexOfColumnUserRequested2);
//                   System.out.println(Integer.parseInt(whereConditionValue));
                    BufferedReader selectReader2 = new BufferedReader(new FileReader(tablePath));
                    // skip reading first line
                    selectReader2.readLine();
                    String lineFromSelectReader2 = null;
                    while ((lineFromSelectReader2 = selectReader2.readLine()) != null) {
                        String[] greaterThanLineParts = lineFromSelectReader2.split("~");
                        if (Integer.parseInt(greaterThanLineParts[greaterThanIndexOfColumnUserRequested2]) < Integer.parseInt(whereConditionValue)) {
                            String[] rowWords = lineFromSelectReader2.split("~");
                            System.out.println(rowWords[indexOfColumnUserRequested]);
                        }
                    }
                    selectReader.close();
                } else {
                    // select name from users where id = 5;
                    BufferedReader columnLineReader = new BufferedReader(new FileReader(tablePath));
                    String firstLine = columnLineReader.readLine();
                    String[] columnsFromFirstLine = firstLine.split("~");
                    columnLineReader.close();

                    for (String column : columnsFromFirstLine) {
                        if (column.contentEquals(splitColumns[0])) {
                            indexOfColumnUserRequested = counterColumn;
                        }
                        counterColumn += 1;
                    }

                    BufferedReader selectReader = new BufferedReader(new FileReader(tablePath));
                    String lineFromSelectReader = null;
                    String row = "";
                    while ((lineFromSelectReader = selectReader.readLine()) != null) {
                        if (lineFromSelectReader.contains(whereCondition.getValue())) {
                            row = lineFromSelectReader;
                            break;
                        }
                    }
                    String[] rowWords = row.split("~");
                    System.out.println(rowWords[indexOfColumnUserRequested]);
                    selectReader.close();
                }
            } else {
                // no >/< when 2 columns to be displayed

                String[] splitColumns2 = columns.split(","); // name,id
                BufferedReader columnLineReader = new BufferedReader(new FileReader(tablePath));
                String firstLine = columnLineReader.readLine();
                String[] columnsFromFirstLine = firstLine.split("~");
                columnLineReader.close();
                LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
                int i = 0;
                for(String column:columnsFromFirstLine) {
                    if(Arrays.asList(splitColumns2).contains(column)) {
                        linkedHashMap.put(column, i);
                    }
                    i += 1;
                }
                Utility.displayMessage(linkedHashMap.entrySet().toString());

                BufferedReader selectReader = new BufferedReader(new FileReader(tablePath));
                String lineFromSelectReader = null;
                String row = "";
                while((lineFromSelectReader=selectReader.readLine())!=null) {
                    if (lineFromSelectReader.contains(whereCondition.getValue())){
                        row = lineFromSelectReader;
                        String[] temp = lineFromSelectReader.split("~");
                        List<String> tempArr2 = new ArrayList<>();
                        for (int ii:linkedHashMap.values()) {
                            tempArr2.add(temp[ii]);
                        }

                        databaseResponse.getTableData().setRow((String[]) tempArr2.toArray(new String[tempArr2.size()]));
                    }
                }

                for(String[]valuesss:databaseResponse.getTableData().getRows()){
                    for(int z=0;z< valuesss.length;z++){
                        System.out.println(valuesss[z]);
                    }
                }
                selectReader.close();
            }
        }
        return new DatabaseResponse(true,"Selected successfully"+tableName);
    }

    @Override
    public DatabaseResponse updateTable(String tableName, String column, String value, WhereCondition whereCondition) throws IOException, DatabaseException {
        if(!validationServices.validateWhereCondition(tableName, whereCondition)){
            Utility.displayMessage("WhereCondition Constraint Failure");
            return new DatabaseResponse(false,"WhereCondition Constraint Failure");
        }
        if(!validationServices.validateWhereCondition(tableName, new WhereCondition(column, value))){
            Utility.displayMessage("Datatype Constraint Failure");
            return new DatabaseResponse(false,"Datatype Constraint Failure");
        }

        if(checkLock()) {
            QueryConstants.DB_PATH = QueryConstants.TRANSACTION_DB_PATH;
        }

        if (QueryConstants.CURRENT_DB == "") {
            Utility.displayMessage("Please select a database");
            return new DatabaseResponse(false, "Please select a database");
        }

        String tablePath = QueryConstants.DB_PATH + QueryConstants.CURRENT_DB + "/" + tableName + ".tsv";
        String tempTablePath = QueryConstants.DB_PATH + QueryConstants.CURRENT_DB + "/" + "temp" + tableName + ".tsv";

        File file = new File(tablePath);
        if (!file.exists()) {
            Utility.displayMessage("Table doesn't exist");
            return new DatabaseResponse(false, "Table doesn't exist");
        }

        File createTempFile = new File(tempTablePath);
        createTempFile.createNewFile(); // create file

        BufferedReader updateReader = new BufferedReader(new FileReader(tablePath));
        BufferedWriter updateWriter = new BufferedWriter(new FileWriter(tempTablePath));

        // to check column index
        String columnIndex = updateReader.readLine();
        boolean isColumnNameWritten = false;
        int columnIndexCounter = 0;
        int columnCounter = 0;
        int columnIndexCounter2 = 0;
        int columnCounter2 = 0;
        String[] columnIndexColumns = columnIndex.split("~");
        if ((whereCondition.getOperation().equals(Operation.GREATER_THAN)) || (whereCondition.getOperation().equals(Operation.LESS_THAN))) {
            for (String column2 : columnIndexColumns) {
                if (column2.contentEquals(column)) {
                    columnCounter = columnIndexCounter;
                }
                columnIndexCounter += 1;
            }

            for (String column2 : columnIndexColumns) {
                if (column2.contentEquals(whereCondition.getColumn())) {
                    columnCounter2 = columnIndexCounter2;
                }
                columnIndexCounter2 += 1;
            }
        } else {
            for (String column2 : columnIndexColumns) {
                if (column2.contentEquals(column)) {
                    columnCounter = columnIndexCounter;
                }
                columnIndexCounter += 1;
            }
        }

        String updateFileLineReader = null;

        if (whereCondition.getOperation().equals(Operation.GREATER_THAN)) {
            while ((updateFileLineReader = updateReader.readLine()) != null) {
                String[] greaterThanLineParts = updateFileLineReader.split("~");
                if (Integer.parseInt(greaterThanLineParts[columnCounter2]) > Integer.parseInt(whereCondition.getValue())) {
                    String[] targetLineSplitWords = updateFileLineReader.split("~");
                    System.out.println(updateFileLineReader);
                    targetLineSplitWords[columnCounter] = value;
                    updateFileLineReader = "";
                    for (String rename : targetLineSplitWords) {
                        updateFileLineReader = updateFileLineReader + rename + "~";
                        System.out.println(rename);
                    }
                    System.out.println(updateFileLineReader);
                }

                if (!isColumnNameWritten) {
                    updateWriter.write(columnIndex + "\n");
                    isColumnNameWritten = true;
                }
                updateWriter.write(updateFileLineReader + "\n");
            }
        } else if (whereCondition.getOperation().equals(Operation.LESS_THAN)) {
            while ((updateFileLineReader = updateReader.readLine()) != null) {
                String[] greaterThanLineParts = updateFileLineReader.split("~");
                if (Integer.parseInt(greaterThanLineParts[columnCounter2]) < Integer.parseInt(whereCondition.getValue())) {
                    String[] targetLineSplitWords = updateFileLineReader.split("~");
                    System.out.println(updateFileLineReader);
                    targetLineSplitWords[columnCounter] = value;
                    updateFileLineReader = "";
                    for (String rename : targetLineSplitWords) {
                        updateFileLineReader = updateFileLineReader + rename + "~";
                        System.out.println(rename);
                    }
                    System.out.println(updateFileLineReader);
                }
                if (!isColumnNameWritten) {
                    updateWriter.write(columnIndex + "\n");
                    isColumnNameWritten = true;
                }
                updateWriter.write(updateFileLineReader + "\n");
            }
        } else {
            while ((updateFileLineReader = updateReader.readLine()) != null) {

                if (updateFileLineReader.contains(whereCondition.getValue())) {
                    String[] targetLineSplitWords = updateFileLineReader.split("~");
                    System.out.println(updateFileLineReader);
                    targetLineSplitWords[columnCounter] = value;
                    updateFileLineReader = "";
                    for (String rename : targetLineSplitWords) {
                        updateFileLineReader = updateFileLineReader + rename + "~";
                        System.out.println(rename);
                    }
                    System.out.println(updateFileLineReader);
                }
                if (!isColumnNameWritten) {
                    updateWriter.write(columnIndex + "\n");
                    isColumnNameWritten = true;
                }
                updateWriter.write(updateFileLineReader + "\n");
            }
        }
        updateWriter.close();
        updateReader.close();

        file.delete();
        createTempFile.renameTo(file);

    	//Event Logs
        String eventLogMessage = "Database:"+QueryConstants.CURRENT_DB+" Table:"+tableName+" updated";
        EventLogWriter.addEventLog(eventLogMessage);
        
        return new DatabaseResponse(true,"Updated successfully"+tableName);
    }

    @Override
    public DatabaseResponse deleteTable(String tableName, WhereCondition whereCondition) throws IOException, DatabaseException {
        if(checkLock()) {
            QueryConstants.DB_PATH = QueryConstants.TRANSACTION_DB_PATH;
        }
        if(!validationServices.validateWhereCondition(tableName, whereCondition)) {
            Utility.displayMessage("WhereCondition Constraint Failure");
            return new DatabaseResponse(false, "WhereCondition Constraint Failure");
        }
        if(QueryConstants.CURRENT_DB=="") {
            Utility.displayMessage("Please select a database");
            return new DatabaseResponse(false,"Please select a database");
        }

        String tablePath = QueryConstants.DB_PATH +QueryConstants.CURRENT_DB+"/"+tableName+".tsv";
        String tempTablePath = QueryConstants.DB_PATH +QueryConstants.CURRENT_DB+"/"+"temp"+tableName+".tsv";

        File file = new File(tablePath);
        if(!file.exists()) {
            Utility.displayMessage("Table doesn't exist");
            return new DatabaseResponse(false,"Table doesn't exist");
        }

        File createTempDeleteFile = new File(tempTablePath);
        createTempDeleteFile.createNewFile(); // create file

        String value = whereCondition.getValue();
        String column = whereCondition.getColumn();

        BufferedReader deleteRowReader = new BufferedReader(new FileReader(tablePath));
        BufferedWriter deleteWriter = new BufferedWriter(new FileWriter(tempTablePath));
        String deleteRowLine = null;

        while((deleteRowLine=deleteRowReader.readLine())!=null) {
            if(!deleteRowLine.contains(value)) {
                deleteWriter.write(deleteRowLine + "\n");
            }
        }

        deleteRowReader.close();
        deleteWriter.close();

        file.delete();
        createTempDeleteFile.renameTo(file);

        Utility.displayMessage("Deleted row");
        
    	//Event Logs
        String eventLogMessage = "Database:"+QueryConstants.CURRENT_DB+" Table:"+tableName+" updated";
        EventLogWriter.addEventLog(eventLogMessage);
        
        return new DatabaseResponse(true,"Deleted successfully from "+tableName);
    }

    @Override
    public DatabaseResponse dropTable(String tableName) {
        if(QueryConstants.CURRENT_DB=="") {
            Utility.displayMessage("DB NOT SELECTED");
            return new DatabaseResponse(false,"Database not selected");
        }
        boolean fileExist = new File(QueryConstants.DB_PATH +QueryConstants.CURRENT_DB+"/"+tableName+".tsv").exists();
        if(fileExist) {
            File file = new File(QueryConstants.DB_PATH + QueryConstants.CURRENT_DB + "/" + tableName + ".tsv");
            Boolean success = file.delete();

            if (success) {
                Utility.displayMessage("success");
                metadataServices.dropTable(tableName);
            	//Event Logs
                String eventLogMessage = "Database:"+QueryConstants.CURRENT_DB+" Table:"+tableName+" dropped";
                EventLogWriter.addEventLog(eventLogMessage);
                return new DatabaseResponse(true, tableName + " has been deleted successfully");
            }
        }
        return new DatabaseResponse(false, tableName + " Error deleting");
    }

    @Override
    public DatabaseResponse beginTransaction() throws IOException {
        if(QueryConstants.CURRENT_DB=="") {
            return new DatabaseResponse(false, "Please Select the database");
        }
        File srcDir = new File(QueryConstants.DB_PATH+QueryConstants.CURRENT_DB);
        File destDir = new File(QueryConstants.TRANSACTION_DB_PATH+QueryConstants.CURRENT_DB);
        FileUtils.copyDirectory(srcDir, destDir);
        lock += 1;
        
    	//Event Logs
        String eventLogMessage = "Transaction Started";
        EventLogWriter.addEventLog(eventLogMessage);
        
        return new DatabaseResponse(true, "TRANSACTION STARTED");
    }

    @Override
    public DatabaseResponse endTransaction(String transactionEndKeyword) throws IOException {
        if(transactionEndKeyword.equalsIgnoreCase("COMMIT")) {
            QueryConstants.DB_PATH = QueryConstants.DB_PATH_PERMANENT;
            File srcDir = new File( QueryConstants.TRANSACTION_DB_PATH+QueryConstants.CURRENT_DB);
            File destDir = new File(QueryConstants.DB_PATH+QueryConstants.CURRENT_DB+"/");
            FileUtils.copyDirectory(srcDir, destDir);
            FileUtils.deleteDirectory(new File(QueryConstants.TRANSACTION_DB_PATH+QueryConstants.CURRENT_DB));
        }else if(transactionEndKeyword.equalsIgnoreCase("ROLLBACK")){
            QueryConstants.DB_PATH = QueryConstants.DB_PATH_PERMANENT;
            FileUtils.deleteDirectory(new File(QueryConstants.TRANSACTION_DB_PATH+QueryConstants.CURRENT_DB));
        }
        lock -= 1;
        
    	//Event Logs
        String eventLogMessage = "Transaction Ended";
        EventLogWriter.addEventLog(eventLogMessage);
        
        return new DatabaseResponse(true, "TRANSACTION ENDED");
    }


    private boolean checkLock(){
        if(lock==0) {
            return false;
        }
        return true;
    }


}