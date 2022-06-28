package services;

import exceptions.DatabaseException;
import logmanagement.EventLogWriter;
import model.Column;
import model.Table;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static Constants.QueryConstants.EOL;

public class ExportAndREServicesImpl implements ExportAndREServices {
    @Override
    public void exportStructure(String fileName) throws DatabaseException {
        System.out.println("Enter Database Name:");
        String dbName = new Scanner(System.in).next();
        MetadataServices metadataServices = new MetadataServicesImpl();
        List<String> tables = metadataServices.getTables(dbName);
        if (tables.size() == 0) {
            throw new DatabaseException("Database has no table!");
        }
        File outputFile = new File(fileName);
        if (outputFile.isFile()) {
            throw new DatabaseException("File Already Exist");
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {
            outputFile.createNewFile();
            for (String table : tables) {
                //Drop
                bufferedWriter.append("DROP TABLE IF EXISTS `" + table + "`;\n");
                System.out.println("DROP TABLE IF EXISTS `" + table + "`;\n");

                //Create
                bufferedWriter.append("CREATE TABLE `" + table + "` (").append(EOL);
                System.out.println("CREATE TABLE `" + table + "` ("+EOL);
                List<Column> columns = metadataServices.getColumnDetailsForTable(table);
                for (Column column : columns) {
                    String constraints = "";
                    if(column.getConstraints() != null) {
                        constraints = String.join(",", column.getConstraints());
                    }
                    bufferedWriter.append("\t `"
                            + column.getColumnName() + "` "
                            + column.getDatatype().type + " "
                            + String.join(" ", constraints)
                            + "\n");
                    System.out.println("\t `"
                            + column.getColumnName() + "` "
                            + column.getDatatype().type + " "
                            + String.join(" ", constraints)
                            + "\n");
                }
                bufferedWriter.append(");\n");
                System.out.println(");\n");

                //Lock Table
                bufferedWriter.append("LOCK TABLES `" + table + "` WRITE;\n");
                System.out.println("LOCK TABLES `" + table + "` WRITE;\n");

                //Insert
                bufferedWriter.append("INSERT INTO `" + table + "` VALUES ");
                System.out.println("INSERT INTO `" + table + "` VALUES ");
                DatabaseServices databaseServices = new DatabaseServicesImpl();
                Table table1 = databaseServices.selectTable(table, "*", null).getTableData();
                List<String> tempList = new ArrayList<>();
                for (String[] row : table1.getRows()) {
                    String temp = Arrays.stream(row).map(x -> "`" + x + "`").collect(Collectors.joining(","));
                    tempList.add("(" + temp + ")");
                }
                bufferedWriter.append(String.join(",", tempList));
                System.out.println(String.join(",", tempList));
                bufferedWriter.append(";\n");
                System.out.println(";\n");

                //UNLOCK
                bufferedWriter.append("UNLOCK TABLES;\n");
                System.out.println("UNLOCK TABLES;\n");
                
            	//Event Logs
                String eventLogMessage = "Dump creation successful for database:"+dbName;
                EventLogWriter.addEventLog(eventLogMessage);
                
                System.out.println("Dump created successfully: "+fileName);
            }
            bufferedWriter.flush();
        } catch (IOException io) {
        	
        	//Event Logs
            String eventLogMessage = "Dump creation unsuccessful for database:"+dbName;
            EventLogWriter.addEventLog(eventLogMessage);
            
            io.printStackTrace();
        }
    }

    @Override
    public void reverseEngineering(String fileName) throws DatabaseException {
        System.out.println("Enter Database Name:");
        String dbName = new Scanner(System.in).next();
        MetadataServices metadataServices = new MetadataServicesImpl();
        List<String> tables = metadataServices.getTables(dbName);
        if (tables.size() == 0) {
            throw new DatabaseException("Database has no table!");
        }
        File outputFile = new File(fileName);
        if (outputFile.isFile()) {
            throw new DatabaseException("File Already Exist");
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {
            outputFile.createNewFile();

            //Tables
            bufferedWriter.append("___________________________\n");
            System.out.println("___________________________\n");
            bufferedWriter.append(String.format("|%-25s|\n","Tables"));
            System.out.println(String.format("|%-25s|\n","Tables"));
            bufferedWriter.append("___________________________\n");
            System.out.println("___________________________\n");
            tables.stream()
                    .forEach(table->{
                        try {
                            bufferedWriter.append(String.format("|%-25s|\n",table));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(String.format("|%-25s|\n",table));
                    });
            bufferedWriter.append("___________________________\n");
            System.out.println("___________________________\n");
            bufferedWriter.append(EOL);
            System.out.println(EOL);
            bufferedWriter.append(EOL);
            System.out.println(EOL);
            bufferedWriter.append(EOL);
            System.out.println(EOL);
            for (String table : tables) {
                bufferedWriter.append("Table Name:"+table).append(EOL);
                System.out.println("Table Name:"+table+EOL);
                //Columns
                bufferedWriter.append("____________________________________________________________________________________________________________\n");
                System.out.println("____________________________________________________________________________________________________________\n");
                String temp = String.format("|%-10s|%-10s|%-10s|%-10s|%-10s|%-10s|\n","Field","Type","Null","Key","Default","Extra");
                bufferedWriter.append(temp);
                System.out.println(temp);
                bufferedWriter.append("____________________________________________________________________________________________________________\n");
                System.out.println("____________________________________________________________________________________________________________\n");
                List<Column> columns = metadataServices.getColumnDetailsForTable(table);
                for (Column column : columns) {
                    String constraints = "";
                    if(column.getConstraints() != null) {
                        constraints = String.join(",", column.getConstraints());
                    }
                    String tt= String.format("|%-10s|%-10s|%-10s|%-10s|%-10s|%-10s|\n",
                            column.getColumnName(),
                            column.getDatatype().toString(),
                            constraints.contains("not null") ? "NO":"YES",
                            constraints.contains("primary") ? "PRI":"",
                            "NULL","");
                    bufferedWriter.append(tt);
                    System.out.println(tt);
                }
                bufferedWriter.append("____________________________________________________________________________________________________________\n");
                System.out.println("____________________________________________________________________________________________________________\n");
            }
            bufferedWriter.flush();
            
        	//Event Logs
            String eventLogMessage = "Data Model creation successful for database:"+dbName;
            EventLogWriter.addEventLog(eventLogMessage);
            
            System.out.println("Data Model created successfully: "+fileName);
            
            
        } catch (IOException io) {
        	
        	//Event Logs
            String eventLogMessage = "Data Model creation unsuccessful for database:"+dbName;
            EventLogWriter.addEventLog(eventLogMessage);
            
            io.printStackTrace();
        }
        
    }
}
