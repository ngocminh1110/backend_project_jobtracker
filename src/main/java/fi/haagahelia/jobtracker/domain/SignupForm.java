package fi.haagahelia.jobtracker.domain;

import jakarta.validation.constraints.*;

// HOLDS THE DATA FROM THE SIGN UP FORM
// NOT AN ENTITY, SO IT IS NOT SAVED IN THE DATABASE
public class SignupForm {

    // USERNAME MUST BE 5-30 CHARACTERS
    @NotEmpty
    @Size(min = 5, max = 30)
    private String username = "";

    // PASSWORD MUST BE 7-30 CHARACTERS
    @NotEmpty
    @Size(min = 7, max = 30)
    private String password = "";

    // USER TYPES THE PASSWORD AGAIN, CONTROLLER CHECKS THAT BOTH ARE THE SAME
    @NotEmpty
    @Size(min = 7, max = 30)
    private String passwordCheck = "";

    // NOT USED IN THE CONTROLLER, NEW USERS ALWAYS GET "USER" ROLE
    @NotEmpty
    private String role = "USER";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordCheck() {
        return passwordCheck;
    }

    public void setPasswordCheck(String passwordCheck) {
        this.passwordCheck = passwordCheck;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}