package main.java.logic;

public class Administrator extends User{
    private String department;

    public Administrator(String id, String name, String password, String department) {
        super(id, name, password);
    }
    public Administrator() {
        this("","","",null);
    }

    @Override
    public String getType(){return "ADMIN";}
    
}
