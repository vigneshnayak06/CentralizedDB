package services;

import Constants.Datatype;
import exceptions.DatabaseException;
import model.Column;
import model.Table;
import model.WhereCondition;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static Constants.QueryConstants.CURRENT_DB;
import static Constants.QueryConstants.DB_PATH;

public class ValidationServicesImpl implements ValidationServices {

    private MetadataServices metadataServices;

    public ValidationServicesImpl() {
        metadataServices = new MetadataServicesImpl();
    }

    @Override
    public boolean isDBExist(String dbName) {
        File file = new File(DB_PATH);
        List<String> dbNames = Arrays.asList(file.list((file1, s) -> new File(file1, s).isDirectory()));
        return dbNames.contains(dbNames);
    }

    @Override
    public boolean isTableExist(String tableName) throws DatabaseException {
        return metadataServices.getTables(CURRENT_DB).contains(tableName);
    }

    @Override
    public boolean isInt(String value) {
        try {
            Integer.valueOf(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isString(String value) {
        try {
            String.valueOf(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isDouble(String value) {
        try {
            Double.valueOf(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isBoolean(String value) {
        try {
            Boolean.valueOf(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validateInsertData(String tableName, Table table) throws DatabaseException {
        List<Column> columns = metadataServices.getColumnDetailsForTable(tableName);
        boolean flag = true;
        for(int i=0;i<table.getRows().size();i++){
            String[]rows = table.getRows().get(i);
            for(int j=0;j<rows.length;j++){
                String column = table.getColumns()[j];
                String value = rows[j];
                Datatype datatype = columns.stream()
                        .filter(x -> x.getColumnName().equalsIgnoreCase(column))
                        .collect(Collectors.toList())
                        .get(0)
                        .getDatatype();
                if (datatype == Datatype.INT) {
                    if(!isInt(value)) flag = false;
                } else if (datatype == Datatype.STRING) {
                    if(!isString(value)) flag = false;
                } else if (datatype == Datatype.BOOL) {
                    if(!isBoolean(value)) flag = false;
                } else if (datatype == Datatype.DOUBLE) {
                    if(!isDouble(value)) flag = false;
                }
            }
        }
        return flag;
    }

    @Override
    public boolean validateWhereCondition(String tableName, WhereCondition whereCondition) throws DatabaseException {
        if(whereCondition == null) return true;
        List<Column> columns = metadataServices.getColumnDetailsForTable(tableName);
        Datatype datatype = columns.stream().
                filter(x -> x.getColumnName().equalsIgnoreCase(whereCondition.getColumn().trim()))
                .collect(Collectors.toList())
                .get(0)
                .getDatatype();
        if (datatype == Datatype.INT) {
            return isInt(whereCondition.getValue());
        } else if (datatype == Datatype.STRING) {
            return isString(whereCondition.getValue());
        } else if (datatype == Datatype.BOOL) {
            return isBoolean(whereCondition.getValue());
        } else if (datatype == Datatype.DOUBLE) {
            return isDouble(whereCondition.getValue());
        }
        return false;
    }
}
