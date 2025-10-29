package front.presentation.history;

import logic.Prescription;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ModelHistory {
    private List<Prescription> prescriptions;
    private Prescription current;
    private PropertyChangeSupport support;

    public static final String CURRENT = "current";
    public static final String LIST = "list";

    public ModelHistory() {
        prescriptions = new ArrayList<>();
        current = null;
        support = new PropertyChangeSupport(this);
    }

    public void setPrescriptionList(List<Prescription> prescriptions) {
        List<Prescription> oldPrescriptions = this.prescriptions;
        this.prescriptions = new ArrayList<>(prescriptions);
        support.firePropertyChange(LIST, oldPrescriptions, this.prescriptions);
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
        support.firePropertyChange(CURRENT, oldCurrent, this.current);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}