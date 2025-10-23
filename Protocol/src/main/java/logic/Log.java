package main.java.logic;

public class Log {
    private String logPasswd;

    public Log() {
        this.logPasswd= "";
    }

    public Log(String number) {
        this.logPasswd = number;
    }

    public String getPasswd() {
        return logPasswd;
    }

    public void setPasswd(String passwd) {
        this.logPasswd = passwd;
    }
}
