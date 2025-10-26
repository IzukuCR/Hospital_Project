package logic;

import java.io.Serializable;

public class Doctor extends User implements Serializable {
    private String specialty;

    public Doctor(String id, String name, String password, String specialty) {
        super(id, name, password);
        this.specialty = specialty;
    }

    public Doctor() {
        this("", "", "", "");
    }

    @Override
    public String getType() {
        return "Doctor";
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}