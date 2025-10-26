package logic;

import java.io.Serializable;

public class Medicine implements Serializable {
    private String code;
    private String name;
    private String presentation;

    public Medicine(String code, String name, String presentation) {
        this.code = code;
        this.name = name;
        this.presentation = presentation;
    }

    public Medicine() {
        this("", "", "");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }
}