package main.java.presentation.dispensing;


import prescription_dispatch.logic.Patient;
import prescription_dispatch.logic.Prescription;
import prescription_dispatch.logic.Service;
import prescription_dispatch.presentation.search_patients.SearchPatientsController;
import prescription_dispatch.presentation.search_patients.SearchPatientsModel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControllerDispensing {
    ModelDispensing model;
    ViewDispensing view;
    Service service;

    public ControllerDispensing(ViewDispensing view, ModelDispensing model) {
        this.model = model;
        this.view = view;
        this.service = Service.instance();
        this.view.setController(this);
        this.view.setModel(this.model);
        loadPrescriptions();
        loadPatients();
    }

    private void loadPrescriptions() {
        try {
            model.setPrescriptionsList(service.prescription().getPrescriptions());
        } catch (Exception e) {
            System.err.println("Error loading prescriptions: " + e.getMessage());
        }
    }

    private void loadPatients() {
        try {
            model.setPatientsList(service.patient().getPatients());
        } catch (Exception e) {
            System.err.println("Error loading patients: " + e.getMessage());
        }
    }

    public void setPharmacists(String id) {
        try{
            this.model.setCurrentPharmacist(service.pharmacist().readById(id));
        } catch (Exception e) {
            System.err.println("Error loading pharmacist: " + e.getMessage());
        }

    }

    public void setPatientAndLoadPrescriptions() {
        Patient p = model.getCurrentPatient();
        if (p != null) {
            try {
                List<Prescription> prescriptions = service.prescription().getPrescriptionsByPatientID(p.getId());
                model.setPrescriptionsList(prescriptions);
                view.updatePrescriptionsTable(prescriptions);
            }catch (Exception e){
                System.err.println("Error loading prescriptions: " + e.getMessage());
            }


        } else {
            view.updatePrescriptionsTable(new ArrayList<>());
        }
    }

    public List<Prescription> getPrescriptionsByDateRange(LocalDate selectedDate) {
        try{
            return service.prescription().getPrescriptionsByDate(selectedDate,this.model.getCurrentPatient().getId());
        } catch (Exception e) {
            System.err.println("Error filtering prescriptions by date: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public void setCurrentPrescriptionStatus(String status) {
        if (model.getCurrentPrescription() != null) {
            try {
                model.getCurrentPrescription().setStatus(status);
                //setPatientAndLoadPrescriptions();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getPanel(),
                        "Error updating prescription status: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void onSearchPatientRequested() {

        prescription_dispatch.presentation.search_patients.View searchView =
                new prescription_dispatch.presentation.search_patients.View();
        SearchPatientsModel searchModel = new SearchPatientsModel();
        SearchPatientsController searchCtrl = new SearchPatientsController(searchView, searchModel);

        //Callback
        searchCtrl.setSelectionListener(p -> {
            model.setCurrentPatient(p); // la view pondrá el nombre
            try {
                model.setPrescriptionsList(service.prescription().getPrescriptionsByPatientID(p.getId()));
            } catch (Exception e) {
                System.err.println("Error loading prescriptions: " + e.getMessage());
            }
        });

        Window owner = SwingUtilities.getWindowAncestor(view.getPanel());
        JDialog dlg = (owner != null)
                ? new JDialog(owner, "Search patients", Dialog.ModalityType.DOCUMENT_MODAL)
                : new JDialog((Frame) null, "Search patients", true); // modal simple si no hay owner

        dlg.setContentPane(searchView.getRoot());
        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true); // bloquea hasta Save/Cancel del buscador

    }
    public Prescription getCurrentPrescription() {
        return model.getCurrentPrescription();
    }
    void setCurrentPrescription(Prescription p) {
        model.setCurrentPrescription(p);
        view.setCurrentPrescription(p);
    }

    public void savePrescription() {

        try {
            service.prescription().update(model.getCurrentPrescription());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
