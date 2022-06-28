package services;

import exceptions.DatabaseException;
import model.Table;
import model.WhereCondition;

public interface ValidationServices {
    boolean isDBExist(String dbName);

    boolean isTableExist(String tableName) throws DatabaseException;

    boolean isInt(String value);

    boolean isString(String value);

    boolean isDouble(String value);

    boolean isBoolean(String value);

    boolean validateInsertData(String tableName, Table table) throws DatabaseException;

    boolean validateWhereCondition(String tableName, WhereCondition whereCondition) throws DatabaseException;
}
