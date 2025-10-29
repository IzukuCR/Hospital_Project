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
    private Service service = Service.instance();
    private PropertyChangeSupport support;

    public static final String CURRENT = "current";
    public static final String LIST = "list";

    public ModelDispensing() {
        this.prescriptionId = "";
        this.status = "Pending";
        this.currentPrescription = new Prescription();
        this.currentPharmacist = new Pharmacist();
        this.currentPatient = new Patient();
        this.prescriptionsList = new ArrayList<>();
        this.support = new PropertyChangeSupport(this);
        this.patientsList = new ArrayList<>();
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public Pharmacist getCurrentPharmacist() {
        return currentPharmacist;
    }
    public Patient getCurrentPatient() {
        return currentPatient;
    }

    public Prescription getCurrentPrescription() {
        return currentPrescription;
    }

    public void setCurrentPrescription(Prescription prescription) {
        Prescription oldPrescription = this.currentPrescription;
        this.currentPrescription = prescription;
        support.firePropertyChange(CURRENT, oldPrescription, this.currentPrescription);
    }

    public void setCurrentPharmacist(Pharmacist pharmacist) {
        Pharmacist oldPharmacist = this.currentPharmacist;
        this.currentPharmacist = pharmacist;
        support.firePropertyChange(CURRENT, oldPharmacist, this.currentPharmacist);
    }
    public void setCurrentPatient(Patient patient) {
        Patient oldPatient = this.currentPatient;
        this.currentPatient = patient;
        support.firePropertyChange(CURRENT, oldPatient, this.currentPatient);
    }
    public void setPrescriptionId(String prescriptionId) {
        String oldPrescriptionId = this.prescriptionId;
        this.prescriptionId = prescriptionId;
        support.firePropertyChange(CURRENT, oldPrescriptionId, this.prescriptionId);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        String oldStatus = this.status;
        this.status = status;
        support.firePropertyChange(CURRENT, oldStatus, this.status);
    }

    public Prescription savePrescription() {
        try {
            if (this.currentPrescription == null) return null;
            System.out.println("Saving prescription: ");
            return service.prescription().update(this.currentPrescription);
        } catch (Exception e) {
            System.out.println("Error saving prescription (model): " + e.getMessage());
            return null;
        }
    }

    public void setPrescriptionsList(List<Prescription> prescriptionsLista) {
        List<Prescription> oldPrescription = this.prescriptionsList;
        this.prescriptionsList = new ArrayList<>(prescriptionsLista);
        support.firePropertyChange(LIST, oldPrescription, this.prescriptionsList);
    }

    public List<Prescription> getPrescriptionsList() {
        return prescriptionsList;
    }

    public  void setPatientsList(List<Patient> patientsList) {
        List<Patient> oldPatients = this.patientsList;
        this.patientsList = new ArrayList<>(patientsList);
        support.firePropertyChange(LIST, oldPatients, this.patientsList);
    }

    public List<Patient> getPatientsList() {
        return patientsList;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        this.support.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        this.support.removePropertyChangeListener(l);
    }
}
