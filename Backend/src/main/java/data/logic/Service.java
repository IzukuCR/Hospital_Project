package data.logic;

import data.dao.*;
import logic.*;
import java.util.List;

public class Service {
    private static Service theInstance;

    public static Service instance(){
        if (theInstance == null) theInstance = new Service();
        return theInstance;
    }

    private DoctorDAO doctorDao;
    private PatientDAO patientDao;
    private PharmacistDAO pharmacistDao;
    private MedicineDAO medicineDao;
    private PrescriptionDAO prescriptionDao;
    private PrescriptionItemDAO prescriptionItemDao;

    public Service() {
        try{
            doctorDao = new DoctorDAO();
            patientDao = new PatientDAO();
            pharmacistDao = new PharmacistDAO();
            medicineDao = new MedicineDAO();
            prescriptionDao = new PrescriptionDAO();
            prescriptionItemDao = new PrescriptionItemDAO();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        // Cleanup resources if needed
    }

    // ================= DOCTORS ============
    public void create(Doctor doctor) throws Exception {
        doctorDao.create(doctor);
    }

    public Doctor readDoctor(String id) throws Exception {
        return doctorDao.findById(id);
    }

    public void update(Doctor doctor) throws Exception {
        doctorDao.update(doctor);
    }

    public void deleteDoctor(String id) throws Exception {
        doctorDao.deleteById(id);
    }

    public List<Doctor> searchDoctorsByName(String name) throws Exception {
        return doctorDao.searchByName(name);
    }

    public List<Doctor> findAllDoctors() throws Exception {
        return doctorDao.findAll();
    }

    // ================= PATIENTS ============
    public void create(Patient patient) throws Exception {
        patientDao.create(patient);
    }

    public Patient readPatient(String id) throws Exception {
        return patientDao.findById(id);
    }

    public void update(Patient patient) throws Exception {
        patientDao.update(patient);
    }

    public void deletePatient(String id) throws Exception {
        patientDao.deleteById(id);
    }

    public List<Patient> searchPatientsByName(String name) throws Exception {
        return patientDao.searchByName(name);
    }

    public List<Patient> findAllPatients() throws Exception {
        return patientDao.findAll();
    }

    // ================= PHARMACISTS ============
    public void create(Pharmacist pharmacist) throws Exception {
        pharmacistDao.create(pharmacist);
    }

    public Pharmacist readPharmacist(String id) throws Exception {
        return pharmacistDao.findById(id);
    }

    public void update(Pharmacist pharmacist) throws Exception {
        pharmacistDao.update(pharmacist);
    }

    public void deletePharmacist(String id) throws Exception {
        pharmacistDao.deleteById(id);
    }

    public List<Pharmacist> searchPharmacistsByName(String name) throws Exception {
        return pharmacistDao.searchByName(name);
    }

    public List<Pharmacist> findAllPharmacists() throws Exception {
        return pharmacistDao.findAll();
    }

    // ================= MEDICINES ============
    public void create(Medicine medicine) throws Exception {
        medicineDao.create(medicine);
    }

    public Medicine readMedicine(String code) throws Exception {
        return medicineDao.findByCode(code);
    }

    public void update(Medicine medicine) throws Exception {
        medicineDao.update(medicine);
    }

    public void deleteMedicine(String code) throws Exception {
        medicineDao.deleteByCode(code);
    }

    public List<Medicine> searchMedicinesByName(String name) throws Exception {
        return medicineDao.searchByName(name);
    }

    public List<Medicine> findAllMedicines() throws Exception {
        return medicineDao.findAll();
    }

    // ================= PRESCRIPTIONS ============
    public void create(Prescription prescription) throws Exception {
        prescriptionDao.create(prescription);
    }

    public Prescription readPrescription(String id) throws Exception {
        return prescriptionDao.findById(id);
    }

    public void update(Prescription prescription) throws Exception {
        prescriptionDao.update(prescription);
    }

    public void deletePrescription(String id) throws Exception {
        prescriptionDao.deleteById(id);
    }

    public List<Prescription> findPrescriptionsByPatient(String patientId) throws Exception {
        return prescriptionDao.findByPatient(patientId);
    }

    public List<Prescription> findAllPrescriptions() throws Exception {
        return prescriptionDao.findAll();
    }

    // ================= PRESCRIPTION ITEMS ============
    public void create(PrescriptionItem item) throws Exception {
        prescriptionItemDao.create(item);
    }

    public List<PrescriptionItem> findItemsByPrescription(String prescriptionId) throws Exception {
        return prescriptionItemDao.findByPrescriptionId(prescriptionId);
    }

    public void update(PrescriptionItem item) throws Exception {
        prescriptionItemDao.update(item);
    }

    public void deletePrescriptionItems(String prescriptionId) throws Exception {
        prescriptionItemDao.deleteAllForPrescription(prescriptionId);
    }
}