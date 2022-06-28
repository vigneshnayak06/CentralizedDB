package services;

import exceptions.DatabaseException;
import model.Column;
import model.DatabaseResponse;
import model.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MetadataServicesTest {

    private static MetadataServices metadataServices;

    @BeforeAll
    static void setup(){
        metadataServices = new MetadataServicesImpl();
    }

    @Test
    void createMeta() throws IOException {
        metadataServices.createMeta();
    }

    @Test
    void insertColumnDetailsTable() {
        Table table = new Table();
        table.setRow(new String[]{"user","name","string","not null,unique"});
        table.setRow(new String[]{"user","id","int",""});
        table.setRow(new String[]{"test","id","int",""});
        DatabaseResponse databaseResponse = metadataServices.insertColumnDetailsTable(table);
        System.out.println(databaseResponse.isSuccess());
        System.out.println(databaseResponse.getMsg());
    }

    @Test
    void insertTableDetailsTable() {
        Table table = new Table();
        table.setRow(new String[]{"testDb","user","0"});
        table.setRow(new String[]{"testDb","test","0"});
        metadataServices.insertTableDetailsTable(table);
    }

    @Test
    void getColumnDetailsForTable() throws DatabaseException {
        List<Column> columns = metadataServices.getColumnDetailsForTable("user");
        columns.stream().forEach(System.out::println);
    }

    @Test
    void dropTable() {
        metadataServices.dropTable("test");
    }

    @Test
    void getTables() throws DatabaseException{
        System.out.println(metadataServices.getTables("testDb"));
    }
}