package User;

import Constants.MenuConstants;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperations {

    public void writeToFile(UserProfile profile) throws IOException {

//        FileWriter writer = new FileWriter("/tmp/foo/bar.txt", true);
        FileWriter writer = new FileWriter(MenuConstants.UserProfileFile.getValue(), true);
        File file = new File(MenuConstants.UserProfileFile.getValue());
        JSONObject userProfile = new JSONObject();
        userProfile.put("userID", profile.getUserID());
        userProfile.put("userPwd", profile.getPassword());
        userProfile.put("userSecQtn", profile.getSecurityQuestion());
        userProfile.put("userSecAns", profile.getSecurityAnswer());
        if (file.length() == 0){
            writer.write(userProfile.toJSONString());
        } else {
            writer.write("\n" + userProfile.toJSONString());
        }
//        createDirectoryIfNotExist("");
        writer.flush();
        writer.close();
    }

    //    // place holder. delete later
//    public void createDirectoryIfNotExist(String filepath) throws IOException {
//
//        Path path = Paths.get("/tmp/foo/bar.txt");
//        Files.createDirectories(path.getParent());
//
//        try {
//            Files.createFile(path);
//        } catch (FileAlreadyExistsException e) {
//            System.err.println("already exists: " + e.getMessage());
//        }
//    }
}
