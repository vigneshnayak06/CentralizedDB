package services;

import Constants.Datatype;
import Constants.Operation;
import exceptions.DatabaseException;
import model.Column;
import model.Table;
import model.WhereCondition;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DatabaseServicesTest {

    @org.junit.jupiter.api.Test
    void createDatabase() {
        DatabaseServices services = new DatabaseServicesImpl();
//        services.createDatabase("test");
        services.createDatabase("seven");
    }

    @org.junit.jupiter.api.Test
    void useDatabase() {
        DatabaseServices services = new DatabaseServicesImpl();
//        services.useDatabase("test");
        services.useDatabase("seven");
    }

    @org.junit.jupiter.api.Test
    void createTable() throws IOException, DatabaseException {
//        DatabaseServices services = new DatabaseServicesImpl();
//        services.createDatabase("test");
//        services.useDatabase("test");
//        List<Column> list = new ArrayList<>();
//        Column column = new Column();
//        column.setColumnName("name");
//        column.setDatatype(Datatype.STRING);
//        Column column2 = new Column();
//        column2.setColumnName("id");
//        column2.setDatatype(Datatype.INT);
//
//        list.add(column);
//        list.add(column2);
//
//        services.createTable("users",list);
//
        DatabaseServices services = new DatabaseServicesImpl();
        services.useDatabase("seven");
        List<Column> list = new ArrayList<>();
        Column column = new Column();
        column.setColumnName("name");
        column.setDatatype(Datatype.STRING);
        Column column2 = new Column();
        column2.setColumnName("id");
        column2.setDatatype(Datatype.INT);

        list.add(column);
        list.add(column2);

        services.createTable("users",list);
    }

    @Test
    void insertTable() throws IOException, DatabaseException {
//        DatabaseServicesImpl services = new DatabaseServicesImpl();
//        services.useDatabase("test");
//        ArrayList<String[]> list = new ArrayList<String[]>();
//        list.add(new String[]{"shiv","101"});
//        list.add(new String[]{"shiv2","102"});
//        list.add(new String[]{"vignesh","112"});
//        Table table = new Table();
//        table.setRows(list);
//        table.setColumnCount(2);
//        table.setColumns(new String[]{"name","id"});
//        services.insertTable("users", table);

        DatabaseServicesImpl services = new DatabaseServicesImpl();
        services.useDatabase("seven");
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"rs","101"});
        list.add(new String[]{"vk","102"});
        list.add(new String[]{"bhuvi","12"});
        list.add(new String[]{"shami","71"});
        list.add(new String[]{"cr7","142"});
        list.add(new String[]{"mssi","60"});
        Table table = new Table();
        table.setRows(list);
        table.setColumnCount(2);
        table.setColumns(new String[]{"name","id"});
        services.insertTable("users", table);
    }

    @org.junit.jupiter.api.Test
    void selectTable() throws IOException, DatabaseException {
        //select name from users where id = 5;
        DatabaseServices services = new DatabaseServicesImpl();
        services.useDatabase("seven");
        WhereCondition whereCondition = new WhereCondition();
        whereCondition.setColumn("id");
        whereCondition.setOperation(Operation.LESS_THAN);
        whereCondition.setValue("71");
//        services.selectTable("users", "id,name", whereCondition);
        services.selectTable("users", "*", whereCondition);
//        services.selectTable("users", "*", whereCondition);
//        DatabaseServices services = new DatabaseServicesImpl();
//        services.useDatabase("test");
//        WhereCondition whereCondition = new WhereCondition();
//        whereCondition.setColumn("id");
//        whereCondition.setOperation(Operation.EQUALS);
//        whereCondition.setValue("112");
//        services.selectTable("users", "*", whereCondition);
    }

    @org.junit.jupiter.api.Test
    void updateTable() throws IOException, DatabaseException {
        // update users set name = "rohitsharma" where id = 112;

//        DatabaseServices services = new DatabaseServicesImpl();
//        services.useDatabase("test");
//        WhereCondition whereCondition = new WhereCondition();
//
//        whereCondition.setColumn("id");
//        whereCondition.setValue("112");
//        whereCondition.setOperation(Operation.EQUALS);
//
//        services.updateTable("users", "name", "vk", whereCondition);

        DatabaseServices services = new DatabaseServicesImpl();
        services.useDatabase("seven");
        WhereCondition whereCondition = new WhereCondition();

        whereCondition.setColumn("id");
        whereCondition.setValue("13");
        whereCondition.setOperation(Operation.LESS_THAN);

        services.updateTable("users", "name", "helolsj", whereCondition);
    }

    @org.junit.jupiter.api.Test
    void deleteTable() throws IOException, DatabaseException {
        DatabaseServices services = new DatabaseServicesImpl();
        services.useDatabase("seven");
        WhereCondition whereCondition = new WhereCondition();

        whereCondition.setColumn("name");
        whereCondition.setValue("helolsj");
        whereCondition.setOperation(Operation.EQUALS);
        services.deleteTable("users", whereCondition);
    }

    @org.junit.jupiter.api.Test
    void dropTable() throws FileNotFoundException {
        DatabaseServices services = new DatabaseServicesImpl();
        services.useDatabase("test");
        services.dropTable("users");
    }
}