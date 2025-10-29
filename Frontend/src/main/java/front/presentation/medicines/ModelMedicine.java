package front.presentation.medicines;

import logic.Medicine;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ModelMedicine {
    private List<Medicine> medicines;
    private Medicine current;
    private PropertyChangeSupport support;

    public static final String CURRENT = "current";
    public static final String LIST = "list";

    public ModelMedicine() {
        medicines = new ArrayList<>();
        current = new Medicine();
        support = new PropertyChangeSupport(this);
    }

    public List<Medicine> getMedicines() {
        return new ArrayList<>(medicines);
    }

    public void setMedicines(List<Medicine> medicines) {
        List<Medicine> oldMedicines = this.medicines;
        this.medicines = new ArrayList<>(medicines);
        support.firePropertyChange(LIST, oldMedicines, this.medicines);
    }

    public Medicine getCurrent() {
        return current;
    }

    public void setCurrent(Medicine current) {
        Medicine oldCurrent = this.current;
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