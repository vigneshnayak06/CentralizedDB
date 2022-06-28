package User;

import Constants.MenuConstants;
import Constants.QueryConstants;
import Utility.Utility;
import analytics.AnalyticsQueryParser;

import exceptions.DatabaseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import services.ExportAndREServicesImpl;
import services.MetadataServicesImpl;
import services.QueryParsingServicesImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.management.Query;

public class UserMenuDriver {

    UserProfile profile;
    CryptoHash encryption;
    FileOperations fileOp;
    Boolean isSuccessfulValidation = false;
    Boolean isDuplicateUser = false;

    public UserMenuDriver() {
        profile = new UserProfile();
        encryption = new CryptoHash();
        fileOp = new FileOperations();
    }

    public void showForm() throws IOException, ParseException, NoSuchAlgorithmException, DatabaseException {

        do {
            Utility.displayMessage(MenuConstants.Form.getValue());
            String formInput = Utility.receiveInput();

            if (formInput.contentEquals(MenuConstants.Selected1Registration.getValue())) {
                showRegisterForm();
            } else if (formInput.contentEquals(MenuConstants.Selected2Login.getValue())) {
                showLoginForm();
            } else {
                Utility.displayMessage(MenuConstants.IncorrectFormInput.getValue());
            }
        }while (true);
    }

    public void showRegisterForm() throws IOException, NoSuchAlgorithmException, ParseException {

        Utility.displayMessage(MenuConstants.EnterName.getValue());
        String userID = Utility.receiveInput();
        checkIfUserExists(userID);

        if (!isDuplicateUser) {
            profile.setUserID(encryption.hashCredentials(userID));
            Utility.displayMessage(MenuConstants.EnterPassword.getValue());
            profile.setPassword(encryption.hashCredentials(Utility.receiveInput()));

            Utility.displayMessage(MenuConstants.EnterSecurityQuestion.getValue());
            profile.setSecurityQuestion( Utility.receiveInput());
            Utility.displayMessage(MenuConstants.EnterSecurityQuestionAnswer.getValue());
            profile.setSecurityAnswer(Utility.receiveInput());

            fileOp.writeToFile(profile);
        } else {
            Utility.displayMessage("DUPLICATE USERID");
        }

    }

    public void showLoginForm() throws IOException, ParseException, NoSuchAlgorithmException, DatabaseException {

        Utility.displayMessage(MenuConstants.EnterName.getValue());
        String id = Utility.receiveInput();

        Utility.displayMessage(MenuConstants.EnterPassword.getValue());
        String pwd = Utility.receiveInput();

        validateUser(id, pwd);

        if (isSuccessfulValidation) {
        	QueryConstants.CURRENT_USER = id;
            displayQueriesMenu();
        } else {
            Utility.displayMessage(MenuConstants.InvalidCredentials.getValue());
        }
    }

    public Boolean checkIfUserExists(String uid) throws IOException, ParseException, NoSuchAlgorithmException {
        BufferedReader reader = new BufferedReader((new FileReader(MenuConstants.UserProfileFile.getValue())));
        String line = reader.readLine();
        JSONParser parser = new JSONParser();
        String hashedID = encryption.hashCredentials(uid);
        while(line!=null) {
            JSONObject obj = (JSONObject) parser.parse(line);
            if (obj.get("userID").equals(hashedID)) {
                isDuplicateUser = true;
            }
            line = reader.readLine();
        }
        return isDuplicateUser;
    }

    // place holder. delete later
    public void displayQueriesMenu() throws IOException, DatabaseException {
        int userInput = 0;
        do {
            Utility.displayMessage("QUERIES MENU");
            Utility.displayMessage("1.Write Queries");
            Utility.displayMessage("2.Export");
            Utility.displayMessage("3.Data Model");
            Utility.displayMessage("4.Analytics");
            Utility.displayMessage("5.Exit");
            userInput = Integer.parseInt(Utility.receiveInput());
            switch (userInput) {
                case 1:
                    Utility.displayMessage("Write Queries");
                    new QueryParsingServicesImpl().receiveQuery();
                    break;
                case 2:
                    Utility.displayMessage("Export");
                    System.out.println("Enter the file Path:");
                    String filePath = Utility.receiveInput();
                    new ExportAndREServicesImpl().exportStructure(filePath);
                    break;
                case 3:
                    Utility.displayMessage("Data Model");
                    System.out.println("Enter the file Path:");
                    String filePath2 = Utility.receiveInput();
                    new ExportAndREServicesImpl().reverseEngineering(filePath2);
                    break;
                case 4:
                    Utility.displayMessage("Write Analytics Query");
                    AnalyticsQueryParser ana = new AnalyticsQueryParser();
                    ana.receiveQuery();
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid option selected. Kindly retry");
                    break;
            }
        }while (userInput!=5);
        System.out.println("Exiting the Program");
    }

    public Boolean validateUser(String id, String pwd) throws IOException, ParseException, NoSuchAlgorithmException {
        BufferedReader reader = new BufferedReader((new FileReader(MenuConstants.UserProfileFile.getValue())));
        String line = reader.readLine();

        String hashedID = encryption.hashCredentials(id);
        String hashedPWD = encryption.hashCredentials(pwd);

        JSONParser parser = new JSONParser();
        while(line!=null) {
            JSONObject obj = (JSONObject) parser.parse(line);
            if ((obj.get("userID").equals(hashedID)) && ((obj.get("userPwd").equals((hashedPWD))))) {
                isSuccessfulValidation = true;
            }
            line = reader.readLine();
        }
        return isSuccessfulValidation;
    }

}
