package front.presentation.prescriptions;

import logic.Doctor;
import logic.Medicine;
import logic.Patient;
import logic.Prescription;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ModelPrescription {
    private List<Prescription> prescriptions;
    private Prescription current;
    private PropertyChangeSupport support;
    private Doctor currentDoctor;
    private List<Medicine> medicines;
    private List<Patient> patients;

    public static final String CURRENT = "current";
    public static final String DOCTOR = "doctor";
    public static final String PATIENTS = "patients";
    public static final String LIST = "list";

    public ModelPrescription() {
        prescriptions = new ArrayList<>();
        medicines = new ArrayList<>();
        current = new Prescription();
        support = new PropertyChangeSupport(this);
        currentDoctor = new Doctor();
        patients = new ArrayList<>();
    }

    public List<Prescription> getPrescriptions() {
        return new ArrayList<>(prescriptions);
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        List<Prescription> oldPrescriptions = this.prescriptions;
        this.prescriptions = new ArrayList<>(prescriptions);
        support.firePropertyChange(LIST, oldPrescriptions, this.prescriptions);
    }

    public Prescription getCurrent() {
        return current;
    }

    public void setCurrent(Prescription current) {
        Prescription oldCurrent = this.current;
        this.current = current;

        if (current != null) {
            System.out.println("Model current set:");
            System.out.println("Patient ID: " + current.getPatientId());
            System.out.println("Withdrawal Date: " + current.getWithdrawalDate());
            System.out.println("Items: " + (current.getItems() != null ? current.getItems().size() : 0));
        }

        support.firePropertyChange(CURRENT, oldCurrent, this.current);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public Prescription getPrescription(String id) {
        if (prescriptions != null) {
            for (Prescription prescription : prescriptions) {
                if (prescription.getId().equals(id)) {
                    return prescription;
                }
            }
        }
        return null;
    }
    public void setDoctor(Doctor doctor) {
        Doctor oldDoctor = this.currentDoctor;
        this.currentDoctor = doctor;
        support.firePropertyChange(DOCTOR, oldDoctor, this.currentDoctor);
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        List<Patient> oldPatients = this.patients;
        this.patients= new ArrayList<>(patients);
        support.firePropertyChange(PATIENTS, oldPatients, this.patients);
    }

    public Doctor getDoctor() {
        return this.currentDoctor;
    }
    public List<Medicine> getMedicines() {
        return medicines;
    }
    public void setMedicines(List<Medicine> medicines) {
        List<Medicine> oldMedicines = this.medicines;
        this.medicines = new ArrayList<>(medicines);
        support.firePropertyChange(LIST, oldMedicines, this.medicines);
    }
}