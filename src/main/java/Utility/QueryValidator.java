package Utility;

import Constants.QueryConstants;

import java.util.Locale;

public class QueryValidator {

    public static boolean checkValidation(String query) {
        if(query.toUpperCase(Locale.ROOT).contains(QueryConstants.CREATE_DATABASE)) {
            if(query.matches("(?i)^create database .*")) {
                return true;
            }
            return false;
        }
        else if(query.toUpperCase(Locale.ROOT).contains(QueryConstants.USE_DATABASE)) {
            if(query.matches("(?i)^use database .*")) {
                return true;
            }
            return false;
        }
        else if(query.toUpperCase(Locale.ROOT).contains(QueryConstants.CREATE_TABLE)) {
            if(query.matches("(?i)^create table .*")) {
                return true;
            }
            return false;
        }

        else if(query.toUpperCase(Locale.ROOT).contains(QueryConstants.SELECT)) {
            if(query.matches("(?i)^select  table .*")) {
                return true;
            }
            return false;
        }
        else if(query.toUpperCase(Locale.ROOT).contains(QueryConstants.SELECT)) {
            if(query.matches("(?i)^update.*")) {
                return true;
            }
            return false;
        }

        else if(query.toUpperCase(Locale.ROOT).contains(QueryConstants.SELECT)) {
            if(query.matches("(?i)^delete from  .*")) {
                return true;
            }
            return false;
        }

        else if(query.toUpperCase(Locale.ROOT).contains(QueryConstants.SELECT)) {
            if(query.matches("(?i)^drop table.*")) {
                return true;
            }
            return false;
        }



        return false;
    }


}
