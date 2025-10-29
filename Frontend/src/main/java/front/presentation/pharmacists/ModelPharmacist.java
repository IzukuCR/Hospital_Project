package front.presentation.pharmacists;

import logic.Pharmacist;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ModelPharmacist {
    private List<Pharmacist> pharmacists;
    private Pharmacist current;
    private PropertyChangeSupport support;

    public static final String CURRENT = "current";
    public static final String LIST = "list";

    public ModelPharmacist() {
        pharmacists = new ArrayList<>(); // Lista vacía por defecto
        current = new Pharmacist();
        support = new PropertyChangeSupport(this);
    }

    public List<Pharmacist> getPharmacists() {
        return new ArrayList<>(pharmacists); // Devolver copia para evitar modificaciones externas
    }

    public void setPharmacists(List<Pharmacist> pharmacists) {
        List<Pharmacist> oldPharmacists = this.pharmacists;
        this.pharmacists = new ArrayList<>(pharmacists); // Crear nueva lista
        support.firePropertyChange(LIST, oldPharmacists, this.pharmacists);
    }

    public Pharmacist getCurrent() {
        return current;
    }

    public void setCurrent(Pharmacist current) {
        Pharmacist oldCurrent = this.current;
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