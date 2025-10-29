package logic;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;


public class Service {

    private static Service theInstance;

    private DoctorService doctorService;
    private PharmacistService pharmacistService;
    private PatientService patientService;
    private MedicineService medicineService;
    private PrescriptionService prescriptionService;
    private LogService logService;

    Socket s;
    ObjectOutputStream os;
    ObjectInputStream is;

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

        try {
            s = new Socket(Protocol.SERVER, Protocol.PORT);
            os = new ObjectOutputStream(s.getOutputStream());
            is = new ObjectInputStream(s.getInputStream());
        } catch (Exception e) {
            System.exit(-1);
        }
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
            os.writeInt(Protocol.DOCTOR_CREATE);
            os.writeObject(d);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Doctor) is.readObject();
            else throw new Exception();
        }

        public Doctor searchByID(String id) throws Exception {
            os.writeInt(Protocol.DOCTOR_READ_BY_ID);
            os.writeObject(id);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Doctor) is.readObject();
            else throw new Exception();
        }

        public List<Doctor> searchByName(String name) throws Exception {
            os.writeInt(Protocol.DOCTOR_SEARCH_BY_NAME);
            os.writeObject(name);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Doctor>) is.readObject();
            else throw new Exception();
        }

        public Doctor update(Doctor d) throws Exception {
            os.writeInt(Protocol.DOCTOR_UPDATE);
            os.writeObject(d);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Doctor) is.readObject();
            else throw new Exception();
        }

        public boolean delete(Doctor doctor) throws Exception {
            os.writeInt(Protocol.DOCTOR_DELETE);
            os.writeObject(doctor);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) is.readObject();
            else throw new Exception();
        }

        public Doctor validateLogin(Doctor doctor) throws Exception {
            os.writeInt(Protocol.DOCTOR_VALIDATE_LOGIN);
            os.writeObject(doctor);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Doctor) is.readObject();
            else throw new Exception();
        }

        public List<Doctor> getDoctors() throws Exception {
            os.writeInt(Protocol.DOCTOR_GET_ALL);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Doctor>) is.readObject();
            else throw new Exception();
        }
    }

    public class PharmacistService {

        private PharmacistService() {
        }

        public Pharmacist create(Pharmacist ph) throws Exception {
            os.writeInt(Protocol.PHARMACIST_CREATE);
            os.writeObject(ph);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Pharmacist) is.readObject();
            else throw new Exception();
        }

        public Pharmacist readById(String id) throws Exception {
            os.writeInt(Protocol.PHARMACIST_READ_BY_ID);
            os.writeObject(id);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Pharmacist) is.readObject();
            else throw new Exception();
        }


        public Pharmacist update(Pharmacist ph) throws Exception {
            os.writeInt(Protocol.PHARMACIST_UPDATE);
            os.writeObject(ph);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Pharmacist) is.readObject();
            else throw new Exception();
        }

        public boolean delete(Pharmacist ph) throws Exception {
            os.writeInt(Protocol.PHARMACIST_DELETE);
            os.writeObject(ph);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) is.readObject();
            else throw new Exception();
        }

        public Pharmacist validateLogin(Pharmacist pharmacist) throws Exception {
            os.writeInt(Protocol.PHARMACIST_VALIDATE_LOGIN);
            os.writeObject(pharmacist);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Pharmacist) is.readObject();
            else throw new Exception();
        }

        public List<Pharmacist> searchByName(String name) throws Exception {
            os.writeInt(Protocol.PHARMACIST_SEARCH_BY_NAME);
            os.writeObject(name);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Pharmacist>) is.readObject();
            else throw new Exception();
        }


        public List<Pharmacist> getPharmacists() throws Exception {
            os.writeInt(Protocol.PHARMACIST_GET_ALL);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Pharmacist>) is.readObject();
            else throw new Exception();
        }
    }

    public class PatientService {
        private PatientService() {
        }

        public Patient create(Patient p) throws Exception {
            os.writeInt(Protocol.PATIENT_CREATE);
            os.writeObject(p);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Patient) is.readObject();
            else throw new Exception();
        }

        public Patient readById(String id) throws Exception {
            os.writeInt(Protocol.PATIENT_READ_BY_ID);
            os.writeObject(id);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Patient) is.readObject();
            else throw new Exception();
        }

        public List<Patient> searchByName(String name) throws Exception {
            os.writeInt(Protocol.PATIENT_SEARCH_BY_NAME);
            os.writeObject(name);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Patient>) is.readObject();
            else throw new Exception();
        }

        public List<Patient> getPatients() throws Exception {
            os.writeInt(Protocol.PATIENT_GET_ALL);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Patient>) is.readObject();
            else throw new Exception();
        }

        public Patient update(Patient p) throws Exception {
            os.writeInt(Protocol.PATIENT_UPDATE);
            os.writeObject(p);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Patient) is.readObject();
            else throw new Exception();
        }

        public boolean delete(Patient patient) throws Exception {
            os.writeInt(Protocol.PATIENT_DELETE);
            os.writeObject(patient);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) is.readObject();
            else throw new Exception();
        }

    }

    public class MedicineService {

        private MedicineService() {
        }

        public Medicine create(Medicine m) throws Exception {
            os.writeInt(Protocol.MEDICINE_CREATE);
            os.writeObject(m);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Medicine) is.readObject();
            else throw new Exception();
        }

        public Medicine readByCode(String code) throws Exception {
            os.writeInt(Protocol.MEDICINE_READ_BY_CODE);
            os.writeObject(code);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Medicine) is.readObject();
            else throw new Exception();
        }

        public List<Medicine> searchByName(String name) throws Exception {
            os.writeInt(Protocol.MEDICINE_SEARCH_BY_NAME);
            os.writeObject(name);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Medicine>) is.readObject();
            else throw new Exception();
        }

        public Medicine update(Medicine m) throws Exception {
            os.writeInt(Protocol.MEDICINE_UPDATE);
            os.writeObject(m);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Medicine) is.readObject();
            else throw new Exception();
        }

        public boolean delete(String code) throws Exception {
            os.writeInt(Protocol.MEDICINE_DELETE);
            os.writeObject(code);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) is.readObject();
            else throw new Exception();
        }

        public List<Medicine> getMedicines() throws Exception {
            os.writeInt(Protocol.MEDICINE_CREATE);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Medicine>) is.readObject();
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
                    return "DOCTOR";
                }

                Pharmacist ph = pharmacistService.readById(id);
                if (ph != null && ph.getPassword().equals(password)) {
                    return "PHARMACIST";
                }
                return null;
            } catch (SQLException ex) {
                throw new Exception("Pharmacist not foud to validate user: " + ex.getMessage(), ex);
            }
        }
    }

    public class PrescriptionService {

        private PrescriptionService() {
        }

        public Prescription create(Prescription p) throws Exception {
            os.writeInt(Protocol.PRESCRIPTION_CREATE);
            os.writeObject(p);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Prescription) is.readObject();
            else throw new Exception();
        }

        public Prescription readById(String id) throws Exception {
            os.writeInt(Protocol.PRESCRIPTION_READ_BY_ID);
            os.writeObject(id);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Prescription) is.readObject();
            else throw new Exception();
        }

        public Prescription update(Prescription p) throws Exception {
            os.writeInt(Protocol.PRESCRIPTION_UPDATE);
            os.writeObject(p);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (Prescription) is.readObject();
            else throw new Exception();
        }

        public boolean delete(String id) throws Exception {
            os.writeInt(Protocol.PRESCRIPTION_DELETE);
            os.writeObject(id);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (boolean) is.readObject();
            else throw new Exception();
        }

        public List<Prescription> getPrescriptions() throws Exception {
            os.writeInt(Protocol.PRESCRIPTION_GET_ALL);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Prescription>) is.readObject();
            else throw new Exception();
        }

        public List<Prescription> getPrescriptionsByPatientID(String patientId) throws Exception {
            os.writeInt(Protocol.PRESCRIPTION_BY_PATIENT_ID);
            os.writeObject(patientId);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Prescription>) is.readObject();
            else throw new Exception();
        }

        public List<Prescription> getPrescriptionsByPatientName(String patientName) throws Exception {
            os.writeInt(Protocol.PRESCRIPTION_BY_PATIENT_NAME);
            os.writeObject(patientName);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Prescription>) is.readObject();
            else throw new Exception();
        }

        public List<Prescription> getPrescriptionsByDate(LocalDate date, String patientId) throws Exception {
            os.writeInt(Protocol.PRESCRIPTION_BY_DATE);
            os.writeObject(date == null ? null : date.toString());
            os.writeObject(patientId);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Prescription>) is.readObject();
            else throw new Exception();
        }

        public List<Prescription> getPrescriptionsByDoctor(String doctorId) throws Exception {
            os.writeInt(Protocol.PRESCRIPTION_BY_DOCTOR);
            os.writeObject(doctorId);
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) return (List<Prescription>) is.readObject();
            else throw new Exception();
        }

    }

    public static class MessagingService {
        private static MessagingService instance;
        private Map<String, Boolean> activeUsers;
        private Map<String, List<Message>> userMessages;
        private List<MessagingListener> listeners;

        private MessagingService() {
            activeUsers = new HashMap<>();
            userMessages = new HashMap<>();
            listeners = new ArrayList<>();
        }

        public static MessagingService getInstance() {
            if (instance == null) {
                instance = new MessagingService();
            }
            return instance;
        }

        public void addListener(MessagingListener listener) {
            listeners.add(listener);
        }

        public void removeListener(MessagingListener listener) {
            listeners.remove(listener);
        }

        public void userLoggedIn(String userId) {
            activeUsers.put(userId, true);
            notifyUserStatusChanged(userId, true);
        }

        public void userLoggedOut(String userId) {
            activeUsers.remove(userId);
            userMessages.remove(userId);
            notifyUserStatusChanged(userId, false);
        }

        public Set<String> getActiveUsers() {
            return activeUsers.keySet();
        }

        public void sendMessage(String sender, String recipient, String messageContent) {
            if (!activeUsers.containsKey(recipient)) {
                throw new IllegalArgumentException("Recipient is not active");
            }

            Message message = new Message(sender, recipient, messageContent);

            userMessages.computeIfAbsent(recipient, k -> new ArrayList<>()).add(message);

            notifyMessageReceived(recipient, message);

            System.out.println("Message sent from " + sender + " to " + recipient + ": " + messageContent);
        }

        public List<Message> getMessagesFor(String userId, String fromUser) {
            List<Message> allMessages = userMessages.getOrDefault(userId, new ArrayList<>());
            return allMessages.stream()
                    .filter(m -> m.getSender().equals(fromUser))
                    .collect(java.util.stream.Collectors.toList());
        }

        public void markMessagesAsRead(String userId, String fromUser) {
            List<Message> messages = userMessages.get(userId);
            if (messages != null) {
                messages.removeIf(m -> m.getSender().equals(fromUser));
            }
        }

        public boolean hasUnreadMessagesFrom(String userId, String fromUser) {
            return userMessages.getOrDefault(userId, new ArrayList<>()).stream()
                    .anyMatch(m -> m.getSender().equals(fromUser));
        }

        private void notifyUserStatusChanged(String userId, boolean loggedIn) {
            for (MessagingListener listener : listeners) {
                if (loggedIn) {
                    listener.onUserLoggedIn(userId);
                } else {
                    listener.onUserLoggedOut(userId);
                }
            }
        }

        private void notifyMessageReceived(String recipient, Message message) {
            for (MessagingListener listener : listeners) {
                listener.onMessageReceived(recipient, message);
            }
        }

        public interface MessagingListener {
            void onUserLoggedIn(String userId);
            void onUserLoggedOut(String userId);
            void onMessageReceived(String recipient, Message message);
        }

        public static class Message {
            private final String sender;
            private final String recipient;
            private final String content;
            private final long timestamp;

            public Message(String sender, String recipient, String content) {
                this.sender = sender;
                this.recipient = recipient;
                this.content = content;
                this.timestamp = System.currentTimeMillis();
            }

            public String getSender() {
                return sender;
            }

            public String getRecipient() {
                return recipient;
            }

            public String getContent() {
                return content;
            }

            public long getTimestamp() {
                return timestamp;
            }
        }
    }
}
