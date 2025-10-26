package logic;

import java.io.Serializable;

public class Administrator extends User implements Serializable {
    private String department;

    public Administrator(String id, String name, String password, String department) {
        super(id, name, password);
        this.department = department;
    }

    public Administrator() {
        this("","","", "");
    }

    @Override
    public String getType(){return "ADMIN";}

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}