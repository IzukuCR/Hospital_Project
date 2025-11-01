package logic;

public class Protocol {
    public static final String SERVER = "localhost";
    public static final int PORT_SYNC = 6767;
    public static final int PORT_ASYNC = 6868;
    // ==========================
    // ⚙️ Errores y control
    // ==========================
    public static final int USER_VALIDATE = 1;
    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_ERROR = 1;
    public static final int DISCONNECT = 99;
    public static final int ERROR_UNKNOWN_METHOD = 1000;

    // ==========================
    // 🩺 Doctor
    // ==========================
    public static final int DOCTOR_CREATE = 100;
    public static final int DOCTOR_READ_BY_ID = 101;
    public static final int DOCTOR_SEARCH_BY_NAME = 102;
    public static final int DOCTOR_UPDATE = 103;
    public static final int DOCTOR_DELETE = 104;
    public static final int DOCTOR_VALIDATE_LOGIN = 105;
    public static final int DOCTOR_GET_ALL = 106;

    // ==========================
    // 💊 Pharmacist
    // ==========================
    public static final int PHARMACIST_CREATE = 200;
    public static final int PHARMACIST_READ_BY_ID = 201;
    public static final int PHARMACIST_SEARCH_BY_NAME = 202;
    public static final int PHARMACIST_UPDATE = 203;
    public static final int PHARMACIST_DELETE = 204;
    public static final int PHARMACIST_VALIDATE_LOGIN = 205;
    public static final int PHARMACIST_GET_ALL = 206;

    // ==========================
    // 🧍‍♂️ Patient
    // ==========================
    public static final int PATIENT_CREATE = 300;
    public static final int PATIENT_READ_BY_ID = 301;
    public static final int PATIENT_SEARCH_BY_NAME = 302;
    public static final int PATIENT_UPDATE = 303;
    public static final int PATIENT_DELETE = 304;
    public static final int PATIENT_GET_ALL = 305;

    // ==========================
    // 💉 Medicine
    // ==========================
    public static final int MEDICINE_CREATE = 400;
    public static final int MEDICINE_READ_BY_CODE = 401;
    public static final int MEDICINE_SEARCH_BY_NAME = 402;
    public static final int MEDICINE_UPDATE = 403;
    public static final int MEDICINE_DELETE = 404;
    public static final int MEDICINE_GET_ALL = 405;

    // ==========================
    // 📜 Prescription
    // ==========================
    public static final int PRESCRIPTION_CREATE = 500;
    public static final int PRESCRIPTION_READ_BY_ID = 501;
    public static final int PRESCRIPTION_UPDATE = 502;
    public static final int PRESCRIPTION_DELETE = 503;
    public static final int PRESCRIPTION_GET_ALL = 504;
    public static final int PRESCRIPTION_BY_PATIENT_ID = 505;
    public static final int PRESCRIPTION_BY_PATIENT_NAME = 506;
    public static final int PRESCRIPTION_BY_DATE = 507;          // ✅ actualizado
    public static final int PRESCRIPTION_BY_DOCTOR = 508;

    // ==========================
    // 📨 Mensajería (opcional)
    // ==========================
    public static final int MESSAGE_SEND = 600;
    public static final int MESSAGE_RECEIVE = 601;
    public static final int MESSAGE_ACTIVE_USERS = 602;
}
