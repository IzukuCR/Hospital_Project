package front.logic;

import java.sql.SQLException;
import java.time.LocalDate;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import logic.Protocol;
import logic.Doctor;
import logic.Pharmacist;
import logic.Patient;
import logic.Medicine;
import logic.Prescription;

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
            os.writeInt(Protocol.MEDICINE_GET_ALL);
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

    private void disconnect() throws Exception {
        os.writeInt(Protocol.DISCONNECT);
        os.flush();
        s.shutdownOutput();
        s.close();
    }

    public void stop() {
        try {
            disconnect();
        } catch (Exception e) {
            System.exit(-1);
        }
    }

}
