package presentation.medicines;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractModelMedicine {
    protected PropertyChangeSupport propertyChangeSupport;

    public AbstractModelMedicine() {
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