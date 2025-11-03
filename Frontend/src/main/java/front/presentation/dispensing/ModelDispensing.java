package front.presentation.dispensing;

import front.logic.Service;
import logic.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ModelDispensing extends AbstractModelDispensing {
    private String prescriptionId;
    private String status;
    private Pharmacist currentPharmacist;
    private Patient currentPatient;
    private Prescription currentPrescription;
    private List<Prescription> prescriptionsList;
    private List<Patient> patientsList;

    // 🔹 Claves de eventos claras y separadas
    public static final String CURRENT_PRESCRIPTION = "currentPrescription";
    public static final String PRESCRIPTION_LIST = "prescriptionList";
    public static final String CURRENT_PATIENT = "currentPatient";
    public static final String PATIENT_LIST = "patientList";

    public ModelDispensing() {
        this.prescriptionId = "";
        this.status = "Pending";
        this.currentPrescription = new Prescription();
        this.currentPharmacist = new Pharmacist();
        this.currentPatient = new Patient();
        this.prescriptionsList = new ArrayList<>();
        this.patientsList = new ArrayList<>();
    }

    public Prescription getCurrentPrescription() { return currentPrescription; }
    public Patient getCurrentPatient() { return currentPatient; }
    public Pharmacist getCurrentPharmacist() { return currentPharmacist; }
    public List<Prescription> getPrescriptionsList() { return prescriptionsList; }
    public List<Patient> getPatientsList() { return patientsList; }

    public void setCurrentPrescription(Prescription prescription) {
        Prescription old = this.currentPrescription;
        this.currentPrescription = prescription;
        propertyChangeSupport.firePropertyChange(CURRENT_PRESCRIPTION, old, this.currentPrescription);
    }

    public void setCurrentPatient(Patient patient) {
        Patient old = this.currentPatient;
        this.currentPatient = patient;
        propertyChangeSupport.firePropertyChange(CURRENT_PATIENT, old, this.currentPatient);
    }

    public void setCurrentPharmacist(Pharmacist pharmacist) {
        Pharmacist old = this.currentPharmacist;
        this.currentPharmacist = pharmacist;
        propertyChangeSupport.firePropertyChange("currentPharmacist", old, this.currentPharmacist);
    }

    public void setPrescriptionsList(List<Prescription> prescriptions) {
        List<Prescription> old = this.prescriptionsList;
        this.prescriptionsList = new ArrayList<>(prescriptions);
        propertyChangeSupport.firePropertyChange(PRESCRIPTION_LIST, old, this.prescriptionsList);
    }

    public void setPatientsList(List<Patient> patients) {
        List<Patient> old = this.patientsList;
        this.patientsList = new ArrayList<>(patients);
        propertyChangeSupport.firePropertyChange(PATIENT_LIST, old, this.patientsList);
    }
}
