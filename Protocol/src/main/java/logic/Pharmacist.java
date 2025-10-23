package main.java.logic;

public class Pharmacist extends User {
    private String shift;

    public Pharmacist(String id, String name, String password, String shift) {
        super(id, name, password);
        this.shift = shift;
    }

    public Pharmacist() {
        this("","","",null);
    }

    @Override
    public String getType(){return "Pharmaceutical";}

    public String getShift(){return shift;}
    public void setShift(String shift){this.shift = shift;}
}

