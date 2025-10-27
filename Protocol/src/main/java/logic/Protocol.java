package logic;

public class Protocol {
    public static final int PORT = 8888;

    // Códigos de operación
    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    public static final int GET_DOCTORS = 10;
    public static final int GET_PATIENTS = 11;
    public static final int GET_MEDICINES = 12;
    public static final int GET_PRESCRIPTIONS = 13;
    public static final int CREATE_PRESCRIPTION = 20;
    public static final int UPDATE_PRESCRIPTION = 21;
    public static final int DELETE_PRESCRIPTION = 22;
    public static final int SEARCH_MEDICINE = 30;
    public static final int GET_USER_LOGS = 40;

    // Códigos de respuesta
    public static final int OK = 200;
    public static final int ERROR = 500;
    public static final int NOT_FOUND = 404;
    public static final int UNAUTHORIZED = 401;
}
