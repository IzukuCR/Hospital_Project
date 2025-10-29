package front.presentation.doctors;

import logic.Doctor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ModelDoctor {
    private List<Doctor> doctors;
    private Doctor current;
    private PropertyChangeSupport support;


    public static final String CURRENT = "current";
    public static final String LIST = "list";

    public ModelDoctor() {
        doctors = new ArrayList<>(); // Lista vacía por defecto
        current = new Doctor();
        support = new PropertyChangeSupport(this);
    }

    public List<Doctor> getDoctors() {
        return new ArrayList<>(doctors);
    }

    public void setDoctors(List<Doctor> doctors) {
        List<Doctor> oldDoctors = this.doctors;
        this.doctors = new ArrayList<>(doctors);
        support.firePropertyChange(LIST, oldDoctors, this.doctors);
    }

    public Doctor getCurrent() {
        return current;
    }

    public void setCurrent(Doctor current) {
        Doctor oldCurrent = this.current;
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