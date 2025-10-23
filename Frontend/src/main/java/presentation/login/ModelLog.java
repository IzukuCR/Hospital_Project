package main.java.presentation.login;

public class ModelLog extends AbstractModelLog {

    private String username;
    private String password;
    private String errorMessage;

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ERROR = "errorMessage";

    public ModelLog() {
        username = "";
        password = "";
        errorMessage = "";
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        String old = this.username;
        this.username = username;
        propertyChangeSupport.firePropertyChange(USERNAME, old, this.username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String old = this.password;
        this.password = password;
        propertyChangeSupport.firePropertyChange(PASSWORD, old, this.password);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String old = this.errorMessage;
        this.errorMessage = errorMessage;
        propertyChangeSupport.firePropertyChange(ERROR, old, this.errorMessage);
    }

    public void clear(){
        setUsername("");
        setPassword("");
        setErrorMessage("");
    }


}
