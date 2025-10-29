package presentation.pharmacists;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractModelPharmacist {
    protected PropertyChangeSupport propertyChangeSupport;

    public AbstractModelPharmacist() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName) {
        propertyChangeSupport.firePropertyChange(propertyName, null, null);
    }
}