package front.presentation.dashboard;

import logic.Medicine;
import logic.Prescription;
import logic.PrescriptionItem;
import front.logic.Service;

import java.text.SimpleDateFormat;
import java.util.*;

public class ModelDashboard extends AbstractModelDashboard {

    private Map<String, Map<String, Integer>> medicinesData;
    private Map<String, Integer> statusData;
    private List<Medicine> medicinesList = new ArrayList<>();

    public static final String MEDICINES_LIST = "MEDICINES_LIST";

    public ModelDashboard() {
        super();
        this.medicinesData = new HashMap<>();
        this.statusData = new HashMap<>();
        this.medicinesList = new ArrayList<>();
    }

    public void setMedicinesList(List<Medicine> newList) {
        List<Medicine> old = this.medicinesList;
        this.medicinesList = newList != null ? newList : new ArrayList<>();
        firePropertyChange(MEDICINES_LIST, old, this.medicinesList);
    }

    public void setMedicinesData(Map<String, Map<String, Integer>> newData) {
        Map<String, Map<String, Integer>> old = this.medicinesData;
        this.medicinesData = newData != null ? newData : new HashMap<>();
        firePropertyChange(MEDICINE_DATA, old, this.medicinesData);
    }

    public void setStatusData(Map<String, Integer> newData) {
        Map<String, Integer> old = this.statusData;
        this.statusData = newData != null ? newData : new HashMap<>();
        firePropertyChange(PRESCRIPTION_STATUS, old, this.statusData);
    }

    public List<Medicine> getMedicinesList() { return medicinesList; }
    public Map<String, Map<String, Integer>> getMedicinesData() { return medicinesData; }
    public Map<String, Integer> getStatusData() { return statusData; }

}
