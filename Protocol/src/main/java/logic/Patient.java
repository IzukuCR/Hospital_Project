package logic;

import java.io.Serializable;

public class Patient extends User implements Serializable {
    private String birthDate;
    private String phoneNumber;

    public Patient(String id, String name, String password, String birthDate, String phoneNumber) {
        super(id, name, password);
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    public Patient(){
        this("", "", "", "", "");
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getType() {
        return "Patient";
    }
}