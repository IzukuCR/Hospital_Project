package front.logic;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import front.presentation.AsyncListener;
import logic.*;

public class Service {

    private static Service theInstance;

    private DoctorService doctorService;
    private PharmacistService pharmacistService;
    private PatientService patientService;
    private MedicineService medicineService;
    private PrescriptionService prescriptionService;
    private LogService logService;

    private Socket syncSocket, asyncSocket;
    private ObjectOutputStream syncOs, asyncOs;
    private ObjectInputStream syncIs, asyncIs;
    private String sessionId;

    private SocketListener socketListener;


    public static Service instance() {
        if (theInstance == null) theInstance = new Service();
        return theInstance;
    }

    private Service() {
        doctorService = new DoctorService();
        pharmacistService = new PharmacistService();
        patientService = new PatientService();
        medicineService = new MedicineService();
        prescriptionService = new PrescriptionService();
        logService = new LogService();

    }

    public void connectAfterLogin(String userId) {
        try {
            if (syncSocket == null || syncOs == null || !syncSocket.isConnected()) {
                System.err.println("Cannot update session: SYNC not initialized.");
                return;
            }

            this.sessionId = userId; // Actualiza ID de sesión real

            // Notificar al servidor el nuevo ID
            syncOs.writeInt(Protocol.USER_VALIDATE);
            syncOs.writeObject(new String[]{userId});
            syncOs.flush();

            System.out.println("Session updated for logged user: " + sessionId);
        } catch (IOException e) {
            System.err.println("Error in connectAfterLogin: " + e.getMessage());
        }
    }

    public void connect() throws IOException, ClassNotFoundException {
        System.out.println("Connecting to server...");

        // --- SYNC ---
        syncSocket = new Socket(Protocol.SERVER, Protocol.PORT_SYNC);
        syncOs = new ObjectOutputStream(syncSocket.getOutputStream());
        syncOs.flush();
        syncIs = new ObjectInputStream(syncSocket.getInputStream());
        System.out.println("SYNC connection established with server.");


        // --- ASYNC ---
        asyncSocket = new Socket(Protocol.SERVER, Protocol.PORT_ASYNC);
        asyncOs = new ObjectOutputStream(asyncSocket.getOutputStream());
        asyncOs.flush();
        asyncIs = new ObjectInputStream(asyncSocket.getInputStream());
        System.out.println("ASYNC connection established with server.");

        // --- Listener asíncrono ---
        socketListener = new SocketListener(asyncSocket, new AsyncListener() {
            @Override
            public void onAsyncNotification(Object obj) {
                if (obj instanceof Message msg) {
                    System.out.println("[ASYNC] " + msg.getSender() + " → " + msg.getReceiver() + ": " + msg.getContent());
                } else {
                    System.out.println("[ASYNC] Unknown object received: " + obj);
                }
            }
        });
        socketListener.start();

        System.out.println("Service fully connected (SYNC + ASYNC ready)");
    }


    private void listenAsync() {
        try {
            while (true) {
                Object obj = asyncIs.readObject();
                if (obj instanceof Message msg)
                    System.out.println("[ASYNC] " + msg.getSender() + " → " + msg.getReceiver() + ": " + msg.getContent());
            }
        } catch (Exception e) {
            System.err.println("Async listener stopped: " + e.getMessage());
        }
    }

    public void sendMessage(Message msg) throws IOException {
        asyncOs.writeObject(msg);
        asyncOs.flush();
    }

    public Socket getSyncSocket() {
        return syncSocket;
    }

    public String getSessionId() {
        return sessionId;
    }

    public DoctorService doctor() {
        return doctorService;
    }

    public PharmacistService pharmacist() {
        return pharmacistService;
    }

    public PatientService patient() {
        return patientService;
    }

    public MedicineService medicine() {
        return medicineService;
    }

    public PrescriptionService prescription() {
        return prescriptionService;
    }

    public LogService log() {
        return logService;
    }

    public class DoctorService {

        private DoctorService() {
        }

        public Doctor create(Doctor d) throws Exception {
            syncOs.writeInt(Protocol.DOCTOR_CREATE);
            syncOs.writeObject(d);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Doctor) syncIs.readObject();
            else throw new Exception();
        }

        public Doctor searchByID(String id) throws Exception {
            syncOs.writeInt(Protocol.DOCTOR_READ_BY_ID);
            syncOs.writeObject(id);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Doctor) syncIs.readObject();
            else throw new Exception();
        }

        public List<Doctor> searchByName(String name) throws Exception {
            syncOs.writeInt(Protocol.DOCTOR_SEARCH_BY_NAME);
            syncOs.writeObject(name);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Doctor>) syncIs.readObject();
            else throw new Exception();
        }

        public Doctor update(Doctor d) throws Exception {
            syncOs.writeInt(Protocol.DOCTOR_UPDATE);
            syncOs.writeObject(d);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Doctor) syncIs.readObject();
            else throw new Exception();
        }

        public boolean delete(Doctor doctor) throws Exception {
            syncOs.writeInt(Protocol.DOCTOR_DELETE);
            syncOs.writeObject(doctor);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) syncIs.readObject();
            else throw new Exception();
        }

        public Doctor validateLogin(Doctor doctor) throws Exception {
            syncOs.writeInt(Protocol.DOCTOR_VALIDATE_LOGIN);
            syncOs.writeObject(doctor);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Doctor) syncIs.readObject();
            else throw new Exception();
        }

        public List<Doctor> getDoctors() throws Exception {
            syncOs.writeInt(Protocol.DOCTOR_GET_ALL);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Doctor>) syncIs.readObject();
            else throw new Exception();
        }
    }

    public class PharmacistService {

        private PharmacistService() {
        }

        public Pharmacist create(Pharmacist ph) throws Exception {
            syncOs.writeInt(Protocol.PHARMACIST_CREATE);
            syncOs.writeObject(ph);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Pharmacist) syncIs.readObject();
            else throw new Exception();
        }

        public Pharmacist readById(String id) throws Exception {
            syncOs.writeInt(Protocol.PHARMACIST_READ_BY_ID);
            syncOs.writeObject(id);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Pharmacist) syncIs.readObject();
            else throw new Exception();
        }


        public Pharmacist update(Pharmacist ph) throws Exception {
            syncOs.writeInt(Protocol.PHARMACIST_UPDATE);
            syncOs.writeObject(ph);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Pharmacist) syncIs.readObject();
            else throw new Exception();
        }

        public boolean delete(Pharmacist ph) throws Exception {
            syncOs.writeInt(Protocol.PHARMACIST_DELETE);
            syncOs.writeObject(ph);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) syncIs.readObject();
            else throw new Exception();
        }

        public Pharmacist validateLogin(Pharmacist pharmacist) throws Exception {
            syncOs.writeInt(Protocol.PHARMACIST_VALIDATE_LOGIN);
            syncOs.writeObject(pharmacist);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Pharmacist) syncIs.readObject();
            else throw new Exception();
        }

        public List<Pharmacist> searchByName(String name) throws Exception {
            syncOs.writeInt(Protocol.PHARMACIST_SEARCH_BY_NAME);
            syncOs.writeObject(name);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Pharmacist>) syncIs.readObject();
            else throw new Exception();
        }


        public List<Pharmacist> getPharmacists() throws Exception {
            syncOs.writeInt(Protocol.PHARMACIST_GET_ALL);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Pharmacist>) syncIs.readObject();
            else throw new Exception();
        }
    }

    public class PatientService {
        private PatientService() {
        }

        public Patient create(Patient p) throws Exception {
            syncOs.writeInt(Protocol.PATIENT_CREATE);
            syncOs.writeObject(p);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Patient) syncIs.readObject();
            else throw new Exception();
        }

        public Patient readById(String id) throws Exception {
            syncOs.writeInt(Protocol.PATIENT_READ_BY_ID);
            syncOs.writeObject(id);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Patient) syncIs.readObject();
            else throw new Exception();
        }

        public List<Patient> searchByName(String name) throws Exception {
            syncOs.writeInt(Protocol.PATIENT_SEARCH_BY_NAME);
            syncOs.writeObject(name);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Patient>) syncIs.readObject();
            else throw new Exception();
        }

        public List<Patient> getPatients() throws Exception {
            syncOs.writeInt(Protocol.PATIENT_GET_ALL);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Patient>) syncIs.readObject();
            else throw new Exception();
        }

        public Patient update(Patient p) throws Exception {
            syncOs.writeInt(Protocol.PATIENT_UPDATE);
            syncOs.writeObject(p);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Patient) syncIs.readObject();
            else throw new Exception();
        }

        public boolean delete(Patient patient) throws Exception {
            syncOs.writeInt(Protocol.PATIENT_DELETE);
            syncOs.writeObject(patient);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) syncIs.readObject();
            else throw new Exception();
        }

    }

    public class MedicineService {

        private MedicineService() {
        }

        public Medicine create(Medicine m) throws Exception {
            syncOs.writeInt(Protocol.MEDICINE_CREATE);
            syncOs.writeObject(m);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Medicine) syncIs.readObject();
            else throw new Exception();
        }

        public Medicine readByCode(String code) throws Exception {
            syncOs.writeInt(Protocol.MEDICINE_READ_BY_CODE);
            syncOs.writeObject(code);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Medicine) syncIs.readObject();
            else throw new Exception();
        }

        public List<Medicine> searchByName(String name) throws Exception {
            syncOs.writeInt(Protocol.MEDICINE_SEARCH_BY_NAME);
            syncOs.writeObject(name);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Medicine>) syncIs.readObject();
            else throw new Exception();
        }

        public Medicine update(Medicine m) throws Exception {
            syncOs.writeInt(Protocol.MEDICINE_UPDATE);
            syncOs.writeObject(m);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Medicine) syncIs.readObject();
            else throw new Exception();
        }

        public boolean delete(String code) throws Exception {
            syncOs.writeInt(Protocol.MEDICINE_DELETE);
            syncOs.writeObject(code);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) syncIs.readObject();
            else throw new Exception();
        }

        public List<Medicine> getMedicines() throws Exception {
            syncOs.writeInt(Protocol.MEDICINE_GET_ALL);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Medicine>) syncIs.readObject();
            else throw new Exception();
        }
    }

    public class LogService {
        private LogService() {
        }

        /*public String validateUserType(String id, String password) {

            Doctor doctor = data.findDoctorById(id);
            if (doctor != null && doctor.getPassword().equals(password)) {
                return "DOCTOR";
            }

            Pharmacist pharmacist = data.findPharmacistById(id);
            if (pharmacist != null && pharmacist.getPassword().equals(password)) {
                return "PHARMACIST";
            }

            return null; // User not found or invalid credentials
        }*/

        public String validateUserType(String id, String password) throws Exception {
            try {
                Doctor doctor = doctorService.searchByID(id);
                if (doctor != null && doctor.getPassword().equals(password)) {
                    sessionId = id; // ✅ guardar ID real en el cliente
                    return "DOCTOR";
                }

                Pharmacist ph = pharmacistService.readById(id);
                if (ph != null && ph.getPassword().equals(password)) {
                    sessionId = id; // ✅ guardar ID real en el cliente
                    return "PHARMACIST";
                }

                return null;
            } catch (SQLException ex) {
                throw new Exception("Pharmacist not found to validate user: " + ex.getMessage(), ex);
            }
        }
    }

    public class PrescriptionService {

        private PrescriptionService() {
        }

        public Prescription create(Prescription p) throws Exception {
            syncOs.writeInt(Protocol.PRESCRIPTION_CREATE);
            syncOs.writeObject(p);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Prescription) syncIs.readObject();
            else throw new Exception();
        }

        public Prescription readById(String id) throws Exception {
            syncOs.writeInt(Protocol.PRESCRIPTION_READ_BY_ID);
            syncOs.writeObject(id);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Prescription) syncIs.readObject();
            else throw new Exception();
        }

        public Prescription update(Prescription p) throws Exception {
            syncOs.writeInt(Protocol.PRESCRIPTION_UPDATE);
            syncOs.writeObject(p);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (Prescription) syncIs.readObject();
            else throw new Exception();
        }

        public boolean delete(String id) throws Exception {
            syncOs.writeInt(Protocol.PRESCRIPTION_DELETE);
            syncOs.writeObject(id);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) syncIs.readObject();
            else throw new Exception();
        }

        public List<Prescription> getPrescriptions() throws Exception {
            syncOs.writeInt(Protocol.PRESCRIPTION_GET_ALL);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Prescription>) syncIs.readObject();
            else throw new Exception();
        }

        public List<Prescription> getPrescriptionsByPatientID(String patientId) throws Exception {
            syncOs.writeInt(Protocol.PRESCRIPTION_BY_PATIENT_ID);
            syncOs.writeObject(patientId);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Prescription>) syncIs.readObject();
            else throw new Exception();
        }

        public List<Prescription> getPrescriptionsByPatientName(String patientName) throws Exception {
            syncOs.writeInt(Protocol.PRESCRIPTION_BY_PATIENT_NAME);
            syncOs.writeObject(patientName);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Prescription>) syncIs.readObject();
            else throw new Exception();
        }

        public List<Prescription> getPrescriptionsByDate(LocalDate date, String patientId) throws Exception {
            syncOs.writeInt(Protocol.PRESCRIPTION_BY_DATE);
            syncOs.writeObject(new Object[]{ date, patientId }); 
            syncOs.flush();

            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR)
                return (List<Prescription>) syncIs.readObject();
            else
                throw new Exception();
        }

        public List<Prescription> getPrescriptionsByDoctor(String doctorId) throws Exception {
            syncOs.writeInt(Protocol.PRESCRIPTION_BY_DOCTOR);
            syncOs.writeObject(doctorId);
            syncOs.flush();
            if (syncIs.readInt() == Protocol.ERROR_NO_ERROR) return (List<Prescription>) syncIs.readObject();
            else throw new Exception();
        }

    }

    private void disconnect() throws Exception {
        syncOs.writeInt(Protocol.DISCONNECT);
        syncOs.flush();
        syncSocket.shutdownOutput();
        syncSocket.close();
    }

    public void stop() {
        try {
            disconnect();
        } catch (Exception e) {
            System.exit(-1);
        }
    }

}
