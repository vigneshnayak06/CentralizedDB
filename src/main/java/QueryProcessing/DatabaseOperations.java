package QueryProcessing;

import Constants.QueryConstants;
import Utility.QueryValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;


public class DatabaseOperations {

    public static void main(String args[]) {
        try{
            String createQuery = "CREATE DATABASE S;";
            String tableQuery = "CREATE TABLE ST;";
            String useQuery = "USE S";
            String dropTable = "Drop table ST;";
            boolean isDBQueryCorrect = QueryValidator.checkValidation(createQuery);
            boolean isTableQueryCorrect = QueryValidator.checkValidation(tableQuery);
            if(isDBQueryCorrect && isTableQueryCorrect) {
                System.out.println(createDatabase(createQuery));
                System.out.println(useDatabase(useQuery));
                System.out.println(createTable(tableQuery));
                System.out.println(dropTable(dropTable));
            }
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean createDatabase(String createDBQuery) throws FileAlreadyExistsException {
        int index = createDBQuery.lastIndexOf(" ");
        String dbName = createDBQuery.substring(index+1,createDBQuery.trim().length()-1);
        String dbPath = "src/DatabaseCollection/" +dbName;
        File file = new File(dbPath);
        if(file.exists()) {
            throw new FileAlreadyExistsException(file.getAbsolutePath());
        }
       boolean  isCreated = file.mkdir();
       return isCreated;
    }

    public static boolean createTable(String createTableQuery) throws FileAlreadyExistsException,FileNotFoundException {
        int index = createTableQuery.lastIndexOf(" ");
        String tableName = createTableQuery.substring(index+1,createTableQuery.trim().length()-1);
        if(QueryConstants.CURRENT_DB=="") {
            throw new FileNotFoundException();
        }
        String tablePath = "src/DatabaseCollection/" +QueryConstants.CURRENT_DB+"/"+tableName+".tsv";
        File file = new File(tablePath);
        if(file.exists()) {
            throw new FileAlreadyExistsException(file.getAbsolutePath());
        }
        boolean isCreated = false;
        try {
            isCreated = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isCreated;
    }

    public static boolean useDatabase(String createTableQuery) throws FileAlreadyExistsException,FileNotFoundException {
        int index = createTableQuery.lastIndexOf(" ");
        String dbName = createTableQuery.substring(index+1,createTableQuery.trim().length());
        boolean fileExist = new File("src/DatabaseCollection/" +dbName).exists();
        if(fileExist) {
            QueryConstants.CURRENT_DB = dbName;
            return true;
        } else {
            throw new FileNotFoundException();
        }
    }

    public static boolean dropTable(String dropTableQuery) throws FileNotFoundException {
        int index = dropTableQuery.lastIndexOf(" ");
        String tableName = dropTableQuery.substring(index+1,dropTableQuery.trim().length()-1);
        if(QueryConstants.CURRENT_DB=="") {
            throw new FileNotFoundException();
        }
        boolean fileExist = new File("src/DatabaseCollection/" +QueryConstants.CURRENT_DB+"/"+tableName+".tsv").exists();
        if(fileExist) {
            File file = new File("src/DatabaseCollection/" +QueryConstants.CURRENT_DB+"/"+tableName+".tsv");
            file.delete();
            return  true;
        } else {
            throw new FileNotFoundException();
        }
    }



}
