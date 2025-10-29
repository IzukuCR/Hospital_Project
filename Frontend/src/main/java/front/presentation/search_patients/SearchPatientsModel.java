package front.presentation.search_patients;

import logic.Patient;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class SearchPatientsModel {
    public static final String LIST = "list";
    public static final String CURRENT = "current";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private List<Patient> patients = new ArrayList<>();
    private Patient current = new Patient();


    public List<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    public void setPatients(List<Patient> list) {
        var old = this.patients;
        this.patients = new ArrayList<>(list);
        support.firePropertyChange(LIST, old, this.patients);
    }

    public Patient getCurrent() {
        return this.current;
    }

    public void setCurrent(Patient d) {
        var old = this.current;
        this.current = d;
        support.firePropertyChange(CURRENT, old, this.current);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}

