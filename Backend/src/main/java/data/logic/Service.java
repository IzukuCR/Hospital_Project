package data.logic;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import data.dao.PrescriptionDAO;
import data.dao.MedicineDAO;
import data.dao.DoctorDAO;
import data.dao.PatientDAO;
import data.dao.PrescriptionItemDAO;
import data.dao.PharmacistDAO;

import logic.Medicine;
import logic.Patient;
import logic.Doctor;
import logic.Prescription;
import logic.PrescriptionItem;
import logic.Pharmacist;

public class Service {

    private static Service theInstance;

    private DoctorService doctorService;
    private PharmacistService pharmacistService;
    private PatientService patientService;
    private MedicineService medicineService;
    private PrescriptionService prescriptionService;
    private LogService logService;

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

    public PrescriptionService prescription(){return prescriptionService;}

    public LogService log() {
        return logService;
    }

    public class DoctorService {

        DoctorDAO dao = new DoctorDAO();
        private DoctorService() {}

        public Doctor create(Doctor d) throws Exception {
            try {
                if (dao.findById(d.getId()) != null) throw new Exception("Doctor already exists");
                dao.create(d);
                return dao.findById(d.getId());
            } catch (SQLException ex) {
                throw new Exception("Database error creating doctor: " + ex.getMessage(), ex);
            }
        }
        public Doctor searchByID(String id) throws Exception {
            try {
                Doctor d = dao.findById(id);
                if (d == null) throw new Exception("Doctor not found");
                return d;
            } catch (SQLException ex) {
                throw new Exception("Database error reading doctor: " + ex.getMessage(), ex);
            }
        }

        public List<Doctor> searchByName(String name) throws Exception {
            try {
                List<Doctor> list = dao.searchByName(name);
                if (list.isEmpty()) throw new Exception("No doctors found with name: " + name);
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error searching doctors: " + ex.getMessage(), ex);
            }
        }

        public Doctor update(Doctor d) throws Exception {
            try {
                if (dao.findById(d.getId()) == null) throw new Exception("Doctor not found for update");
                int rows = dao.update(d);
                if (rows == 0) throw new Exception("Update affected no rows");
                return dao.findById(d.getId());
            } catch (SQLException ex) {
                throw new Exception("Database error updating doctor: " + ex.getMessage(), ex);
            }
        }

        public boolean delete(Doctor doctor) throws Exception {
            try {
                String id = doctor.getId();
                if (dao.findById(id) == null) throw new Exception("Doctor not found for deletion");
                return dao.deleteById(id) > 0;
            } catch (SQLException ex) {
                throw new Exception("Database error deleting doctor: " + ex.getMessage(), ex);
            }
        }

        public Doctor validateLogin(Doctor doctor) throws Exception {

            Doctor found = dao.findById(doctor.getId());
            if (found != null && found.getPassword().equals(doctor.getPassword())) {
                return found;
            }
            return null;
        }

        public List<Doctor> getDoctors() throws Exception {
            try {
                List<Doctor> list = dao.findAll();
                if (list.isEmpty()) throw new Exception("No doctors on list.. Add some first");
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error loading doctors: " + ex.getMessage(), ex);
            }
        }
    }

    public class PharmacistService {
        private final PharmacistDAO dao = new PharmacistDAO();
        private PharmacistService() {}

        public Pharmacist create(Pharmacist ph) throws Exception {
            try {
                if (dao.findById(ph.getId()) != null) {
                    throw new Exception("Pharmacist already exists");
                }
                dao.create(ph);
                return dao.findById(ph.getId()); // Recupera el farmacéutico recién insertado
            } catch (SQLException ex) {
                throw new Exception("Database error creating pharmacist: " + ex.getMessage(), ex);
            }
        }

        public Pharmacist readById(String id) throws Exception {
            try {
                Pharmacist ph = dao.findById(id);
                if (ph == null) throw new Exception("Pharmacist not found");
                return ph;
            } catch (SQLException ex) {
                throw new Exception("Database error reading pharmacist: " + ex.getMessage(), ex);
            }
        }


        public Pharmacist update(Pharmacist ph) throws Exception {
            try {
                Pharmacist existing = dao.findById(ph.getId());
                if (existing == null) throw new Exception("Pharmacist not found for update");

                int rows = dao.update(ph);
                if (rows == 0) throw new Exception("Update affected no rows");
                return dao.findById(ph.getId()); // Recupera el farmacéutico actualizado
            } catch (SQLException ex) {
                throw new Exception("Database error updating pharmacist: " + ex.getMessage(), ex);
            }
        }

        public boolean delete(Pharmacist ph) throws Exception {
            try {
                if (dao.findById(ph.getId()) == null) {
                    throw new Exception("Pharmacist not found for deletion");
                }
                int rows = dao.deleteById(ph.getId());
                return rows > 0;
            } catch (SQLException ex) {
                throw new Exception("Database error deleting pharmacist: " + ex.getMessage(), ex);
            }
        }

        public Pharmacist validateLogin(Pharmacist pharmacist) throws Exception {
            try {
                if (pharmacist == null ||
                        pharmacist.getId() == null || pharmacist.getId().isBlank() ||
                        pharmacist.getPassword() == null) {
                    throw new Exception("Missing credentials");
                }

                Pharmacist found = dao.findById(pharmacist.getId());
                if (found == null) {
                    throw new Exception("User not found");
                }

                if (!found.getPassword().equals(pharmacist.getPassword())) {
                    throw new Exception("Invalid credentials");
                }

                return found; // ok
            } catch (SQLException ex) {
                throw new Exception("Database error validating login: " + ex.getMessage(), ex);
            }
        }

        public List<Pharmacist> searchByName(String name) throws Exception {
            try {
                List<Pharmacist> list = dao.searchByName(name);
                if (list.isEmpty()) {
                    throw new Exception("No pharmacists found with name: " + name);
                }
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error searching pharmacists: " + ex.getMessage(), ex);
            }
        }


        public List<Pharmacist> getPharmacists() throws Exception {
            try {
                List<Pharmacist> list = dao.findAll();
                if (list.isEmpty()) {
                    throw new Exception("No pharmacists on list.. Add some first");
                }
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error loading pharmacists: " + ex.getMessage(), ex);
            }
        }
    }

    public class PatientService {
        private PatientService() {
        }
        private final PatientDAO dao = new PatientDAO();

        public Patient create(Patient p) throws Exception {
            try {
                if (dao.findById(p.getId()) != null) throw new Exception("Patient already exists");
                dao.create(p);
                return dao.findById(p.getId());
            } catch (SQLException ex) {
                throw new Exception("Database error creating patient: " + ex.getMessage(), ex);
            }
        }

        public Patient readById(String id) throws Exception {
            try {
                Patient p = dao.findById(id);
                if (p == null) throw new Exception("Patient not found");
                return p;
            } catch (SQLException ex) {
                throw new Exception("Database error reading patient: " + ex.getMessage(), ex);
            }
        }

        public List<Patient> searchByName(String name) throws Exception {
            try {
                List<Patient> list = dao.searchByName(name);
                if (list.isEmpty()) throw new Exception("No patients found with name: " + name);
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error searching patients: " + ex.getMessage(), ex);
            }
        }

        public List<Patient> getPatients() throws Exception {
            try {
                List<Patient> list = dao.findAll();
                if (list.isEmpty()) throw new Exception("No patients on list.. Add some first");
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error loading patients: " + ex.getMessage(), ex);
            }
        }

        public Patient update(Patient p) throws Exception {
            try {
                if (dao.findById(p.getId()) == null) throw new Exception("Patient not found for update");
                if (dao.update(p) == 0) throw new Exception("Update affected no rows");
                return dao.findById(p.getId());
            } catch (SQLException ex) {
                throw new Exception("Database error updating patient: " + ex.getMessage(), ex);
            }
        }

        public boolean delete(Patient patient) throws Exception {
            try {
                String id = patient.getId();
                if (dao.findById(id) == null) throw new Exception("Patient not found for deletion");
                return dao.deleteById(id) > 0;
            } catch (SQLException ex) {
                throw new Exception("Database error deleting patient: " + ex.getMessage(), ex);
            }
        }

    }

    public class MedicineService {

        private final MedicineDAO dao = new MedicineDAO();
        private MedicineService() {
        }

        public Medicine create(Medicine m) throws Exception {
            try {
                if (dao.findByCode(m.getCode()) != null) throw new Exception("Medicine already exists");
                dao.create(m);
                return dao.findByCode(m.getCode());
            } catch (SQLException ex) {
                throw new Exception("Database error creating medicine: " + ex.getMessage(), ex);
            }
        }

        public Medicine readByCode(String code) throws Exception {
            try {
                Medicine m = dao.findByCode(code);
                if (m == null) throw new Exception("Medicine not found");
                return m;
            } catch (SQLException ex) {
                throw new Exception("Database error reading medicine: " + ex.getMessage(), ex);
            }
        }

        public List<Medicine> searchByName(String name) throws Exception {
            try {
                List<Medicine> list = dao.searchByName(name);
                if (list.isEmpty()) throw new Exception("No medicines found with name: " + name);
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error searching medicines: " + ex.getMessage(), ex);
            }
        }

        public Medicine update(Medicine m) throws Exception {
            try {
                if (dao.findByCode(m.getCode()) == null) throw new Exception("Medicine not found for update");
                if (dao.update(m) == 0) throw new Exception("Update affected no rows");
                return dao.findByCode(m.getCode());
            } catch (SQLException ex) {
                throw new Exception("Database error updating medicine: " + ex.getMessage(), ex);
            }
        }

        public boolean delete(String code) throws Exception {
            try {
                if (dao.findByCode(code) == null) throw new Exception("Medicine not found for deletion");
                return dao.deleteByCode(code) > 0;
            } catch (SQLException ex) {
                throw new Exception("Database error deleting medicine: " + ex.getMessage(), ex);
            }
        }

        public List<Medicine> getMedicines() throws Exception {
            try {
                List<Medicine> list = dao.findAll();
                if (list.isEmpty()) throw new Exception("No medicines on list.. Add some first");
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error loading medicines: " + ex.getMessage(), ex);
            }
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

        public String validateUserType(String id, String password) throws Exception{
            DoctorDAO doctorDao = new DoctorDAO();
            Doctor doctor = doctorDao.findById(id);
            if (doctor != null && doctor.getPassword().equals(password)) {
                return "DOCTOR";
            }

            PharmacistDAO pharmacistDao = new PharmacistDAO();
            try {
                Pharmacist ph = pharmacistDao.findById(id);
                if (ph != null && ph.getPassword().equals(password)){
                    return "PHARMACIST";
                }
                return null;
            } catch (SQLException ex) {
                throw new Exception("Pharmacist not foud to validate user: " + ex.getMessage(), ex);
            }
        }
    }
    public class PrescriptionService {

        private final PrescriptionDAO dao = new PrescriptionDAO();
        private final PrescriptionItemDAO itemDao = new PrescriptionItemDAO();

        private PrescriptionService() {}

        public Prescription create(Prescription p) throws Exception {
            try {
                if (dao.findById(p.getId()) != null) throw new Exception("Prescription already exists");
                dao.create(p);
                return dao.findById(p.getId());
            } catch (SQLException ex) {
                throw new Exception("Database error creating prescription: " + ex.getMessage(), ex);
            }
        }

        public Prescription readById(String id) throws Exception {
            try {
                Prescription p = dao.findById(id);
                if (p == null) throw new Exception("Prescription not found");

                List<PrescriptionItem> items = itemDao.findByPrescriptionId(id);
                p.setItems(items);
                return p;
            } catch (SQLException ex) {
                throw new Exception("Database error reading prescription: " + ex.getMessage(), ex);
            }
        }

        public Prescription update(Prescription p) throws Exception {
            try {
                if (dao.findById(p.getId()) == null) throw new Exception("Prescription not found for update");
                if (dao.update(p) == 0) throw new Exception("Update affected no rows");
                return dao.findById(p.getId());
            } catch (SQLException ex) {
                throw new Exception("Database error updating prescription: " + ex.getMessage(), ex);
            }
        }

        public boolean delete(String id) throws Exception {
            try {
                if (dao.findById(id) == null) throw new Exception("Prescription not found for deletion");
                // Borra items primero por FK
                itemDao.deleteAllForPrescription(id);
                return dao.deleteById(id) > 0;
            } catch (SQLException ex) {
                throw new Exception("Database error deleting prescription: " + ex.getMessage(), ex);
            }
        }

        public List<Prescription> getPrescriptions() throws Exception {
            try {
                List<Prescription> list = dao.findAll();
                if (list.isEmpty()) throw new Exception("No prescriptions on list.. Add some first");
                for (Prescription p : list) {
                    p.setItems(itemDao.findByPrescriptionId(p.getId()));
                }
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error loading prescriptions: " + ex.getMessage(), ex);
            }
        }

        public List<Prescription> getPrescriptionsByPatientID(String patientId) throws Exception {
            try {
                List<Prescription> list = dao.findByPatientId(patientId);
                if (list.isEmpty()) throw new Exception("No prescriptions for patient: " + patientId);

                for (Prescription p : list) {
                    p.setItems(itemDao.findByPrescriptionId(p.getId()));
                }
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error loading prescriptions by patient: " + ex.getMessage(), ex);
            }
        }

        public List<Prescription> getPrescriptionsByPatientName(String patientName) throws Exception {
            try {
                List<Prescription> list = dao.findByPatientName(patientName);
                if (list.isEmpty()) throw new Exception("No prescriptions for patient: " + patientName);

                for (Prescription p : list) {
                    p.setItems(itemDao.findByPrescriptionId(p.getId()));
                }
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error loading prescriptions by patient: " + ex.getMessage(), ex);
            }
        }

        public List<Prescription> getPrescriptionsByDate(LocalDate date, String patientId) throws  Exception {
            try {
                ZoneId zone = ZoneId.systemDefault();
                Timestamp from = Timestamp.valueOf(date.atStartOfDay());
                Timestamp to   = Timestamp.valueOf(date.plusDays(1).atStartOfDay());

                List<Prescription> list = dao.findByPatientIdAndWithdrawalRange(patientId, from, to);
                if (list.isEmpty()) throw new Exception("No prescriptions for patient " + patientId + " on " + date);

                for (Prescription p : list) {
                    p.setItems(itemDao.findByPrescriptionId(p.getId()));
                }
                return list;
            } catch (SQLException ex) {
                throw new Exception("Database error filtering prescriptions: " + ex.getMessage(), ex);
            }
        }

        public List<Prescription> getPrescriptionsByDoctor(String doctorId) throws Exception {
            try {
                List<Prescription> list = dao.findByDoctorId(doctorId);
                if (list.isEmpty()) {
                    throw new Exception("No prescriptions for doctor: " + doctorId);
                }

                PrescriptionItemDAO itemDao = new PrescriptionItemDAO();
                for (Prescription p : list) {
                    p.setItems(itemDao.findByPrescriptionId(p.getId()));
                }
                return list;

            } catch (SQLException ex) {
                throw new Exception("Database error loading prescriptions by doctor: " + ex.getMessage(), ex);
            }
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
