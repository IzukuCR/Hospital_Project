package logic;

import java.io.Serializable;

public class Log implements Serializable {
    private String logPasswd;

    public Log() {
        this.logPasswd = "";
    }

    public Log(String passwd) {
        this.logPasswd = passwd;
    }

    public String getPasswd() {
        return logPasswd;
    }

    public void setPasswd(String passwd) {
        this.logPasswd = passwd;
    }
}