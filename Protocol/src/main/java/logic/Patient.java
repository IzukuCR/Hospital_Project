package main.java.logic;

public class Patient extends User {
    private String birthDate;
    private String phoneNumber;

    public Patient(){}

    public Patient(String id, String name, String birthDate, String phoneNumber) {
        super(id,name);
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
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
        return "";
    }

}
