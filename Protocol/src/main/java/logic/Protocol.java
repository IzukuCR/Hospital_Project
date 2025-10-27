package logic;

public class Protocol {
    public static final String SERVER = "localhost";
    public static final int PORT = 1234;

    // --- DOCTOR ---
    public static final int DOCTOR_CREATE = 101;
    public static final int DOCTOR_READ_BY_ID = 102;
    public static final int DOCTOR_UPDATE = 103;
    public static final int DOCTOR_DELETE = 104;
    public static final int DOCTOR_SEARCH_BY_NAME = 105;
    public static final int DOCTOR_GET_ALL = 106;
    public static final int DOCTOR_VALIDATE_LOGIN = 107;

    // --- PHARMACIST ---
    public static final int PHARMACIST_CREATE = 201;
    public static final int PHARMACIST_READ_BY_ID = 202;
    public static final int PHARMACIST_UPDATE = 203;
    public static final int PHARMACIST_DELETE = 204;
    public static final int PHARMACIST_SEARCH_BY_NAME = 205;
    public static final int PHARMACIST_GET_ALL = 206;
    public static final int PHARMACIST_VALIDATE_LOGIN = 207;

    // --- PATIENT ---
    public static final int PATIENT_CREATE = 301;
    public static final int PATIENT_READ_BY_ID = 302;
    public static final int PATIENT_UPDATE = 303;
    public static final int PATIENT_DELETE = 304;
    public static final int PATIENT_SEARCH_BY_NAME = 305;
    public static final int PATIENT_GET_ALL = 306;

    // --- MEDICINE ---
    public static final int MEDICINE_CREATE = 401;
    public static final int MEDICINE_READ_BY_CODE = 402;
    public static final int MEDICINE_UPDATE = 403;
    public static final int MEDICINE_DELETE = 404;
    public static final int MEDICINE_GET_ALL = 405;
    public static final int MEDICINE_SEARCH_BY_NAME = 406;

    // --- PRESCRIPTION ---
    public static final int PRESCRIPTION_CREATE = 501;
    public static final int PRESCRIPTION_READ_BY_ID = 502;
    public static final int PRESCRIPTION_UPDATE = 503;
    public static final int PRESCRIPTION_DELETE = 504;
    public static final int PRESCRIPTION_GET_ALL = 505;
    public static final int PRESCRIPTION_BY_PATIENT_ID = 506;
    public static final int PRESCRIPTION_BY_PATIENT_NAME = 507;
    public static final int PRESCRIPTION_BY_DOCTOR = 508;
    public static final int PRESCRIPTION_BY_DATE = 509;

    // --- LOGIN ---
    public static final int USER_VALIDATE = 601;

    // --- ERRORS & CONNECTIONS ---
    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_ERROR = 1;
    public static final int DISCONNECT = 99;
    public static final int ERROR_UNKNOWN_METHOD = -1;
}
