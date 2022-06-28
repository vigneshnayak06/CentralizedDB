package services;

import exceptions.DatabaseException;

import java.io.IOException;

public interface QueryParsingServices {
    void queryParsing(String query) throws IOException, DatabaseException;

    void receiveQuery() throws IOException, DatabaseException;
}
