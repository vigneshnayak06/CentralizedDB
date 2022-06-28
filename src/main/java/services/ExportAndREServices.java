package services;

import exceptions.DatabaseException;

public interface ExportAndREServices {
    void exportStructure(String fileName) throws DatabaseException;
    void reverseEngineering(String fileName) throws DatabaseException;
}
