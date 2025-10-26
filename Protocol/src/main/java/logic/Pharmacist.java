package logic;

import java.io.Serializable;

public class Pharmacist extends User implements Serializable {
    private String shift;

    public Pharmacist(String id, String name, String password, String shift) {
        super(id, name, password);
        this.shift = shift;
    }

    public Pharmacist() {
        this("","","", "");
    }

    @Override
    public String getType(){return "Pharmaceutical";}

    public String getShift(){return shift;}
    public void setShift(String shift){this.shift = shift;}
}