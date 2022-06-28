package Constants;

public enum MenuConstants {
    Welcome("Welcome"),
    Form("Please select \n 1. Registration \n 2. Login"),
    Selected1Registration("1"),
    Selected2Login("2"),
    IncorrectFormInput("Please enter either 1 or 2"),
    EnterName("Enter your user id"),
    EnterPassword("Enter your password"),
    EnterSecurityQuestion("Enter a security question"),
    EnterSecurityQuestionAnswer("Enter answer to your security question"),
    InvalidCredentials("Invalid credentials. Try again."),
    UserProfileFile("User_Profile.txt");

    private String value;

    private MenuConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
