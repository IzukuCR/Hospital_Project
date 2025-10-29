package front.presentation.patients;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractModelPatient {
    protected PropertyChangeSupport propertyChangeSupport;

    public AbstractModelPatient() {
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