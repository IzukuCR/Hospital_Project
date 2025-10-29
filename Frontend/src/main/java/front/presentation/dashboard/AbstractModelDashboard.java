package front.presentation.dashboard;

import logic.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractModelDashboard {
    protected PropertyChangeSupport propertyChangeSupport;

    public static final String MEDICINE_DATA = "medicineData";
    public static final String PRESCRIPTION_STATUS = "prescriptionStatus";

    public AbstractModelDashboard() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public abstract Map<String, Map<String, Integer>> getMedicinesPrescribedByMonth(Date startDate, Date endDate);
    public abstract Map<String, Integer> getPrescriptionsByStatus();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
