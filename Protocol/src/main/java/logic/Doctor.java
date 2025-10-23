package main.java.logic;

public class Doctor extends User {
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
