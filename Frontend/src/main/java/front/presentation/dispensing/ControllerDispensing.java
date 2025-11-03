package front.presentation.dispensing;

import front.presentation.Refresher;
import front.presentation.ThreadListener;
import front.presentation.search_patients.View;
import logic.Patient;
import logic.Prescription;
import front.logic.Service;
import front.presentation.search_patients.SearchPatientsController;
import front.presentation.search_patients.SearchPatientsModel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControllerDispensing implements ThreadListener {
    private final ModelDispensing model;
    private final ViewDispensing view;
    private final Service service;

    public ControllerDispensing(ViewDispensing view, ModelDispensing model) {
        this.model = model;
        this.view = view;
        this.service = Service.instance();

        this.view.setController(this);
        this.view.setModel(this.model);
    }

    public void setPharmacists(String id) {
        new Thread(() -> {
            try {
                var pharmacist = service.pharmacist().readById(id);
                SwingUtilities.invokeLater(() -> model.setCurrentPharmacist(pharmacist));
            } catch (Exception e) {
                System.err.println("[ControllerDispensing] Error loading pharmacist: " + e.getMessage());
            }
        }, "Dispensing-Pharmacist").start();
    }

    public void onSearchPatientRequested() {
        View searchView = new View();
        SearchPatientsModel searchModel = new SearchPatientsModel();
        SearchPatientsController searchCtrl = new SearchPatientsController(searchView, searchModel);

        searchCtrl.setSelectionListener(p -> {
            SwingUtilities.invokeLater(() -> model.setCurrentPatient(p));
            new Thread(() -> {
                try {
                    var prescriptions = service.prescription().getPrescriptionsByPatientID(p.getId());
                    SwingUtilities.invokeLater(() -> model.setPrescriptionsList(prescriptions));
                } catch (Exception e) {
                    System.err.println("[ControllerDispensing] Error loading prescriptions: " + e.getMessage());
                }
            }, "Dispensing-SearchPatient").start();
        });

        Window owner = SwingUtilities.getWindowAncestor(view.getPanel());
        JDialog dlg = new JDialog(owner, "Search patients", Dialog.ModalityType.DOCUMENT_MODAL);
        dlg.setContentPane(searchView.getRoot());
        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }

    public List<Prescription> getPrescriptionsByDateRange(LocalDate selectedDate) {
        try {
            return service.prescription().getPrescriptionsByDate(selectedDate, model.getCurrentPatient().getId());
        } catch (Exception e) {
            System.err.println("[ControllerDispensing] Error filtering prescriptions by date: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public void setCurrentPrescription(Prescription p) {
        SwingUtilities.invokeLater(() -> {
            model.setCurrentPrescription(p);
            view.setCurrentPrescription(p);
        });
    }

    public Prescription getCurrentPrescription() {
        return model.getCurrentPrescription();
    }

    public void setCurrentPrescriptionStatus(String status) {
        var prescription = model.getCurrentPrescription();
        if (prescription != null) {
            new Thread(() -> {
                try {
                    prescription.setStatus(status);
                    service.prescription().update(prescription);
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(view.getPanel(),
                                    "Error updating prescription: " + e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
            }, "Dispensing-UpdateStatus").start();
        }
    }

    public void savePrescription() {
        new Thread(() -> {
            try {
                service.prescription().update(model.getCurrentPrescription());
            } catch (Exception e) {
                System.err.println("[ControllerDispensing] Error saving prescription: " + e.getMessage());
            }
        }, "Dispensing-SavePrescription").start();
    }

    @Override
    public void refresh() {
        new Thread(() -> {
            try {
                var prescriptions = service.prescription().getPrescriptions();
                var patients = service.patient().getPatients();
                SwingUtilities.invokeLater(() -> {
                    model.setPrescriptionsList(prescriptions);
                    model.setPatientsList(patients);
                });
            } catch (Exception e) {
                System.err.println("[ControllerDispensing] Refresh error: " + e.getMessage());
            }
        }, "Dispensing-Refresh").start();
    }
}
