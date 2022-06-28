package services;

import Constants.Datatype;
import Constants.Operation;
import Constants.QueryConstants;
import Utility.Utility;
import exceptions.DatabaseException;
import logmanagement.QueryLogWriter;
import model.Column;
import model.DatabaseResponse;
import model.Table;
import model.WhereCondition;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParsingServicesImpl implements QueryParsingServices {

    @Override
    public void queryParsing(String query) throws IOException, DatabaseException {

        String query_type = query.replaceAll (" .*", "");
        query_type = query_type.toLowerCase ();
        DatabaseServices db= new DatabaseServicesImpl();
        query =query.toLowerCase();


        switch(query_type){

               case "create":

                   // String create_query = "CREATE TABLE Customer (COL1 char PRIMARY KEY, COL3 int FOREIGN KEY REFERENCES table col3);";
                    query = query.replaceAll(";$", "");
                    String[] words = query.split(" ");
                    if(words[1].equalsIgnoreCase("database")) {
                        db.createDatabase(words[2]);
                    }

                    else if(words[1].equalsIgnoreCase("table")){

                        String outerCreateQuery = "create\\stable\\s(\\w+)\\s";
                        String innerCreateQuery = "\\(((?:\\s*\\w+\\s+\\w+\\(?[0-9]*\\)?,?)+)\\)";
                        Pattern pattern1 = Pattern.compile(outerCreateQuery + innerCreateQuery);
                        Matcher matcher1 = pattern1.matcher(query);
                        List<Column> columnList=new ArrayList();

                        if (matcher1.matches()) {
                            String tableName = matcher1.group(1);
                            String columnString = matcher1.group(2);
                            String[] columns = columnString.split("\\,");

                            for (int i = 0; i < columns.length; i++) {
                                Column columnObject=new Column();

                                String column = columns[i].trim();
                                String columnName = column.substring(0, column.indexOf(' '));
                                columnObject.setColumnName(columnName);

                                String temp = column.substring(column.indexOf(' ')).trim();
                                System.out.println(temp);
                                String dataType = temp.indexOf(' ') == -1 ? temp : temp.substring(0, temp.indexOf(' '));
                                columnObject.setDatatype(Datatype.valueOf(dataType.toUpperCase(Locale.ROOT)));

                                if(temp.indexOf(' ')!=-1) {
                                    String constraint = temp.substring(temp.indexOf(' '));
                                    String[] constraints = {constraint};
                                    columnObject.setConstraints(constraints);
                                }

                                columnList.add(columnObject);
                            }

                            DatabaseResponse databaseResponse = db.createTable(tableName,columnList);
                            if(!databaseResponse.isSuccess()){
                                System.out.println("ERROR: "+databaseResponse.getMsg());
                            }
                            else {
                                
                            	//Query Logs
                                String queryLogMessage = "Status:Valid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB+"~Table:"+tableName+"~Operation:CREATE";
                                QueryLogWriter.addQueryLog(queryLogMessage);
                                
                            }

                        }
                        else{
                        	
                        	//Query Logs
                            String queryLogMessage = "Status:Invalid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB;
                            QueryLogWriter.addQueryLog(queryLogMessage);
                            
                            System.out.println("Invalid query !!");
                        }
                    }
                   break;


                case "use":
                    query = query.replaceAll(";", "");
                    String[] words1 = query.split(" ");
                    String dbName = words1[1];
                    db.useDatabase(dbName);
                    break;


                case "insert":
                    Pattern pattern2 = Pattern.compile("insert into\\s(.*?)\\s(.*?)\\svalues\\s(.*?);", Pattern.DOTALL);
                    Matcher matcher2 = pattern2.matcher(query);
                    matcher2.find();
                    if(matcher2.matches()) {
                    	String table = matcher2.group(1);
                        String[] columns = matcher2.group(2)
                                .replaceAll("[\\[\\](){}]","")
                                .replace(" ","")
                                .split(",");
                        String[] rows = matcher2.group(3)
                                .replaceAll("[\\[\\](){}]","")
                                .replace(" ","")
                                .replace("'","")
                                .replace("\"","")
                                .split(",");

                        ArrayList<String[]> rowValues = new ArrayList<>();
                        rowValues.add(rows);

                        Table table1 = new Table();
                        table1.setColumns(columns);
                        table1.setRows(rowValues);

                        DatabaseResponse databaseResponse = db.insertTable(table,table1);
                        if(!databaseResponse.isSuccess()){
                            System.out.println("ERROR: "+databaseResponse.getMsg());
                        }
                        else {
                        	
                        	//Query Logs
                            String queryLogMessage = "Status:Valid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB+"~Table:"+table+"~Operation:INSERT";
                            QueryLogWriter.addQueryLog(queryLogMessage);
                            
                        }
                
                    }
                    else {
                    	
                    	//Query Logs
                        String queryLogMessage = "Status:Invalid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB;
                        QueryLogWriter.addQueryLog(queryLogMessage);
                        
                        System.out.println("Invalid query !!");
                        
                    }
                    
                    break;

               case "select":

                    //query = "SELECT * FROM Customers WHERE Country = 'Mexico';

                    Pattern pattern3 = Pattern.compile("select\\s+(.*?)\\s*from\\s+(.*?)\\s*(where\\s(.*?)\\s*)?;", Pattern.DOTALL);
                    Matcher matcher3 = pattern3.matcher(query);
                    matcher3.find();
                    boolean match = matcher3.matches ();

                    if(match == true) {

                        String columnName = matcher3.group(1);
                        String tableName = matcher3.group (2);
                        String whereConditionStr = matcher3.group (4);
                        WhereCondition whereCondition = null;
                        if(whereConditionStr != null) {
                            String[] condition = whereConditionStr.split(" ");

                            whereCondition = new WhereCondition();
                            whereCondition.setColumn(condition[0]);
                            whereCondition.setOperation(getOperation(condition[1]));
                            whereCondition.setValue(condition[2].replaceAll("\'", ""));
                        }
                        DatabaseResponse databaseResponse = db.selectTable(tableName,columnName,whereCondition);
                        if(!databaseResponse.isSuccess()){
                            System.out.println("ERROR: "+databaseResponse.getMsg());
                        }
                        else {
                        	
                        	//Query Logs
                            String queryLogMessage = "Status:Valid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB+"~Table:"+tableName+"~Operation:SELECT";
                            QueryLogWriter.addQueryLog(queryLogMessage);
                            
                        }

                    }
                    else {
                    	
                    	//Query Logs
                        String queryLogMessage = "Status:Invalid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB;
                        QueryLogWriter.addQueryLog(queryLogMessage);
                        
                        System.out.println("Invalid query !!");
                    }
                    
                    break;

                case "update":

                    //UPDATE Customers SET ContactName='Juan' WHERE Country = 'Mexico';
                   Pattern pattern4 = Pattern.compile("update\\s(.*?)set\\s(.*?)where\\s(.*?)?;");
                    Matcher matcher4 = pattern4.matcher(query);
                    boolean match2 = matcher4.matches ();

                    if(match2) {
                        String tableName = matcher4.group (1).trim();
                        String[] columnNameAndValue = matcher4.group(2).split("=");
                        String[] condition = matcher4.group (3).split(" ");
                        columnNameAndValue[1] = columnNameAndValue[1].substring(1, columnNameAndValue[1].length()-2);

                        WhereCondition whereCondition=new WhereCondition();
                        whereCondition.setColumn(condition[0]);
                        whereCondition.setOperation(getOperation(condition[1]));
                        whereCondition.setValue(condition[2].replaceAll("\'",""));
                        columnNameAndValue[1].replaceAll("\'","");

                        DatabaseResponse databaseResponse = db.updateTable(tableName,columnNameAndValue[0].trim(),columnNameAndValue[1].trim(),whereCondition);
                        if(!databaseResponse.isSuccess()){
                            System.out.println("ERROR: "+databaseResponse.getMsg());
                        }
                        else {
                        	
                        	//Query Logs
                            String queryLogMessage = "Status:Valid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB+"~Table:"+tableName+"~Operation:UPDATE";
                            QueryLogWriter.addQueryLog(queryLogMessage);
                            
                        }
                    }
                    else {
                    	
                    	//Query Logs
                        String queryLogMessage = "Status:Invalid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB;
                        QueryLogWriter.addQueryLog(queryLogMessage);

                        System.out.println("Invalid query !!");
                    }
                    break;


                case "delete":

                    // query = "DELETE FROM Customers WHERE CustomerName='Alfreds Futterkiste';";

                    Pattern pattern5 = Pattern.compile("delete\\s(.*?)from\\s(.*?)where\\s(.*?)?;");
                    Matcher matcher5 = pattern5.matcher(query);
                    boolean match3 = matcher5.matches ();

                    if(match3) {
                        String tableName = matcher5.group (2).trim();
                        String condition[] = matcher5.group (3).split(" ");

                        WhereCondition whereCondition=new WhereCondition();
                        whereCondition.setColumn(condition[0]);
                        whereCondition.setOperation(getOperation(condition[1]));
                        whereCondition.setValue(condition[2].replaceAll("\'",""));

                        
                        
                        DatabaseResponse databaseResponse = db.deleteTable(tableName,whereCondition);
                        if(!databaseResponse.isSuccess()){
                            System.out.println("ERROR: "+databaseResponse.getMsg());
                        }
                        else {
                        	
                        	//Query Logs
                            String queryLogMessage = "Status:Valid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB+"~Table:"+tableName+"~Operation:DELETE";
                            QueryLogWriter.addQueryLog(queryLogMessage);
                            
                        }
                    }
                    else {
                    	
                    	//Query Logs
                        String queryLogMessage = "Status:Invalid~Query:"+query+"~Database:"+QueryConstants.CURRENT_DB;
                        QueryLogWriter.addQueryLog(queryLogMessage);
                        
                        System.out.println("Invalid query !!");
                    }
                    break;

                case "drop":
                    //query=DROP TABLE table_name;
                    Pattern pattern6 = Pattern.compile("drop\\s(.*?)table\\s(.*?)?;");
                    Matcher matcher6 = pattern6.matcher(query);
                    boolean match4 = matcher6.matches();

                    if (match4) {
                        String tableName = matcher6.group(2);
                        db.dropTable(tableName);
                    }
                    else {
                    	
                    	//Query Logs
                        String queryLogMessage = "Status:Invalid~Query:"+query+"Database:"+QueryConstants.CURRENT_DB;
                        QueryLogWriter.addQueryLog(queryLogMessage);
                        
                        System.out.println("Invalid query");
                    }
                    break;

                case "begin":
                    query = query.replaceAll(";", "");
                    if(query.equalsIgnoreCase("BEGIN TRANSACTION")){
                        db.beginTransaction();
                    }
                    else{
                        System.out.println("Invalid query");
                    }
                    break;

                case "commit":
                case "rollback":
                    query = query.replaceAll(";", "");
                    db.endTransaction(query);

            }


    }

    private Operation getOperation(String temp){
        if(temp.equalsIgnoreCase("=")) return Operation.EQUALS;
        if(temp.equalsIgnoreCase("<")) return Operation.LESS_THAN;
        if(temp.equalsIgnoreCase(">")) return Operation.GREATER_THAN;
        if(temp.equalsIgnoreCase("!=")) return Operation.NOT_EQUALS;
        if(temp.equalsIgnoreCase("like")) return Operation.LIKE;
        return null;
    }
    @Override
    public void receiveQuery() throws IOException, DatabaseException {
        String query;
        do {
            query = Utility.receiveInput();
            if (!query.equals("exit;")) {
                queryParsing(query);
                System.out.println("Query Executed.");
            }
        } while(!query.equals("exit;"));
    }
}
