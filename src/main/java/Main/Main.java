package Main;

import Constants.Datatype;
import Constants.Operation;
import User.UserMenuDriver;
import exceptions.DatabaseException;
import logmanagement.GeneralLogWriter;

import model.Column;
import model.Table;
import model.WhereCondition;
import org.json.simple.parser.ParseException;
import services.DatabaseServices;
import services.DatabaseServicesImpl;
import services.QueryParsingServicesImpl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) throws IOException, ParseException, NoSuchAlgorithmException, DatabaseException {
        UserMenuDriver driver = new UserMenuDriver();

        Runnable helloRunnable = new Runnable() {
            public void run() {
            	GeneralLogWriter.addMetadata();
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 60, TimeUnit.SECONDS);

        driver.showForm();
        executor.shutdown();
        
        //new QueryParsingServicesImpl().receiveQuery();
        //transcation();
    }

    public static void transcation () throws IOException, DatabaseException {
        DatabaseServicesImpl obj = new DatabaseServicesImpl();
        obj.createDatabase("test2");
        obj.useDatabase("test2");

        //create table
        List<Column> list = new ArrayList<>();
        Column column = new Column();
        column.setColumnName("name");
        column.setDatatype(Datatype.STRING);
        Column column2 = new Column();
        column2.setColumnName("id");
        column2.setDatatype(Datatype.INT);
        list.add(column);
        list.add(column2);
        obj.createTable("users",list);
        //////////////////

        //insert table
        ArrayList<String[]> list2 = new ArrayList<String[]>();
        list2.add(new String[]{"rs","101"});
        list2.add(new String[]{"vk","102"});
        list2.add(new String[]{"bhuvi","12"});
        list2.add(new String[]{"shami","71"});
        list2.add(new String[]{"cr7","142"});
        list2.add(new String[]{"mssi","60"});
        Table table = new Table();
        table.setRows(list2);
        table.setColumnCount(2);
        table.setColumns(new String[]{"name","id"});
        obj.insertTable("users", table);
        ////////////////////

        obj.beginTransaction();

        //update table
        WhereCondition whereCondition = new WhereCondition();
        whereCondition.setColumn("id");
        whereCondition.setValue("99");
        whereCondition.setOperation(Operation.GREATER_THAN);
        obj.updateTable("users", "name", "greatest", whereCondition);
        ///////////////

        //delete table
        whereCondition.setColumn("name");
        whereCondition.setValue("shami");
        whereCondition.setOperation(Operation.EQUALS);
        obj.deleteTable("users", whereCondition);
        ///////////////

        obj.endTransaction("COMMIT");

    }

}
