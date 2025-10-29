package presentation.patients;

import logic.Patient;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ModelPatient {
    private List<Patient> patients;
    private Patient current;
    private PropertyChangeSupport support;

    public static final String CURRENT = "current";
    public static final String LIST = "list";

    public ModelPatient() {
        patients = new ArrayList<>(); // Lista vacía por defecto
        current = new Patient();
        support = new PropertyChangeSupport(this);
    }

    public List<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    public void setPatients(List<Patient> patients) {
        List<Patient> oldPatients = this.patients;
        this.patients = new ArrayList<>(patients);
        support.firePropertyChange(LIST, oldPatients, this.patients);
    }

    public Patient getCurrent() {
        return current;
    }

    public void setCurrent(Patient current) {
        Patient oldCurrent = this.current;
        this.current = current;
        support.firePropertyChange(CURRENT, oldCurrent, this.current);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}