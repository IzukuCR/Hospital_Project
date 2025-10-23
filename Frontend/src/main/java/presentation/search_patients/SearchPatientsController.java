package main.java.presentation.search_patients;

import prescription_dispatch.logic.Patient;
import prescription_dispatch.logic.Service;
import prescription_dispatch.presentation.search_patients.PatientSelectionListener;
import prescription_dispatch.presentation.table_models.TableModelPatient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SearchPatientsController {

    private final View view;
    private final SearchPatientsModel model;
    private final TableModelPatient tableModel;

    private PatientSelectionListener selectionListener;

    private Service service;

    private enum SearchBy { ID, NAME }
    private SearchBy searchBy = SearchBy.NAME;

    public SearchPatientsController(View view,SearchPatientsModel model) {
        this.view  = view;
        this.model = model;

        this.service = Service.instance();

        this.view.setController(this);
        try{
            this.tableModel = new TableModelPatient(new ArrayList<>(service.patient().getPatients()));
        }catch(Exception e){
            throw new RuntimeException("Error initializing TableModelPatient: " + e.getMessage());
        }
        this.view.setTableModel(tableModel);

        loadAll();
    }

    private void loadAll() {
        try{
            List<Patient> all = service.patient().getPatients();
            model.setPatients(all);
            tableModel.setData(all);
        }catch (Exception e){
            JOptionPane.showMessageDialog(view.getRoot(), "Error to load patients: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setSelectionListener(PatientSelectionListener listener) {
        this.selectionListener = listener;
    }


    public void onConditionChanged(String selected) {
        searchBy = "ID".equalsIgnoreCase(selected) ? SearchBy.ID : SearchBy.NAME;
        applyFilter(lastQuery);
    }

    private String lastQuery = "";

    public void onQueryChanged(String text) {
        lastQuery = text == null ? "" : text.trim();
        applyFilter(lastQuery);
    }

    public void onRowSelected(int modelRow) {
        Patient p = tableModel.getRowAt(modelRow);
        model.setCurrent(p);
    }

    public void onCancelRequested() {
        Window w = SwingUtilities.getWindowAncestor(view.getRoot());
        if (w != null) w.dispose();
    }

    public void onConfirmSelection() {
        Patient selected = model.getCurrent();
        if (selected == null || selected.getId() == null) {
            JOptionPane.showMessageDialog(view.getRoot(), "Seleccione un paciente.", "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        var w = SwingUtilities.getWindowAncestor(view.getRoot());
        if (w != null) w.dispose();
    }
    public void onSaveRequested() {
        Patient selected = model.getCurrent();
        if (selected == null || selected.getId() == null || selected.getId().isEmpty()) {
            JOptionPane.showMessageDialog(view.getRoot(), "Seleccione un paciente.", "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectionListener != null) selectionListener.onPatientSelected(selected);
        onCancelRequested(); // cierra la ventana
    }
    public void onClearRequested() {
        lastQuery = "";
        try{
            List<Patient> all = service.patient().getPatients();
            model.setPatients(all);
            tableModel.setData(all);
            model.setCurrent(new Patient());
        }catch (Exception e){
            JOptionPane.showMessageDialog(view.getRoot(), "Error to load patients: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter(String q) {

        try{
            List<Patient> base = service.patient().getPatients();
            List<Patient> filtered = new ArrayList<>();

            if (q.isEmpty()) {
                filtered = base;
            } else if (searchBy == SearchBy.ID) {
                q = q.replaceAll("\\D+", "");
                for (Patient p : base) {
                    if (p.getId() != null && p.getId().startsWith(q)) {
                        filtered.add(p);
                    }
                }
            } else { // NAME
                String ql = q.toLowerCase();
                for (Patient p : base) {
                    if (p.getName() != null && p.getName().toLowerCase().contains(ql)) {
                        filtered.add(p);
                    }
                }
            }

            model.setPatients(filtered);
            tableModel.setData(filtered);
        }catch (Exception e){
            JOptionPane.showMessageDialog(view.getRoot(), "Error to load patients: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}