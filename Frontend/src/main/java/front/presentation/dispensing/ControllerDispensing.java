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
    ModelDispensing model;
    ViewDispensing view;
    Service service;


    public ControllerDispensing(ViewDispensing view, ModelDispensing model) {
        this.model = model;
        this.view = view;
        this.service = Service.instance();
        this.view.setController(this);
        this.view.setModel(this.model);

        //new Thread(this::loadPrescriptionsSafe, "Dispensing-LoadPrescriptions").start();
        //new Thread(this::loadPatientsSafe, "Dispensing-LoadPatients").start();

        this.model.setPatientsList(new ArrayList<>());
        this.model.setPrescriptionsList(new ArrayList<>());
        //loadPrescriptions();
        //loadPatients();

    }

    private void loadPrescriptionsSafe() {
        try {
            var prescriptions = service.prescription().getPrescriptions();
            SwingUtilities.invokeLater(() -> model.setPrescriptionsList(prescriptions));
        } catch (Exception e) {
            System.err.println("[ControllerDispensing] Error loading prescriptions: " + e.getMessage());
        }
    }

    private void loadPatientsSafe() {
        try {
            var patients = service.patient().getPatients();
            SwingUtilities.invokeLater(() -> model.setPatientsList(patients));
        } catch (Exception e) {
            System.err.println("[ControllerDispensing] Error loading patients: " + e.getMessage());
        }
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

    /*public void setPharmacists(String id) {
        try{
            this.model.setCurrentPharmacist(service.pharmacist().readById(id));
        } catch (Exception e) {
            System.err.println("Error loading pharmacist: " + e.getMessage());
        }

    }*/

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


    public List<Prescription> getPrescriptionsByDateRange(LocalDate selectedDate) {
        try {
            return service.prescription().getPrescriptionsByDate(
                    selectedDate, model.getCurrentPatient().getId());
        } catch (Exception e) {
            System.err.println("[ControllerDispensing] Error filtering prescriptions by date: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /*public void setCurrentPrescriptionStatus(String status) {
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
    }*/

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
                                    "Error updating prescription status: " + e.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE)
                    );
                }
            }, "Dispensing-UpdateStatus").start();
        }
    }

    /*public void onSearchPatientRequested() {

       View searchView =
                new View();
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

    }*/

    public void onSearchPatientRequested() {
        View searchView = new View();
        SearchPatientsModel searchModel = new SearchPatientsModel();
        SearchPatientsController searchCtrl = new SearchPatientsController(searchView, searchModel);

        searchCtrl.setSelectionListener(p -> {
            model.setCurrentPatient(p);
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
        JDialog dlg = (owner != null)
                ? new JDialog(owner, "Search patients", Dialog.ModalityType.DOCUMENT_MODAL)
                : new JDialog((Frame) null, "Search patients", true);

        dlg.setContentPane(searchView.getRoot());
        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }

    public Prescription getCurrentPrescription() {
        return model.getCurrentPrescription();
    }

    /*void setCurrentPrescription(Prescription p) {
        model.setCurrentPrescription(p);
        view.setCurrentPrescription(p);
    }*/

    void setCurrentPrescription(Prescription p) {
        SwingUtilities.invokeLater(() -> {
            model.setCurrentPrescription(p);
            view.setCurrentPrescription(p);
        });
    }

   /* public void savePrescription() {

        try {
            service.prescription().update(model.getCurrentPrescription());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

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
        try {
            var prescriptions = service.prescription().getPrescriptions();
            var patients = service.patient().getPatients();

            // Actualizamos el modelo directamente (ya estamos en el hilo de la vista)
            model.setPrescriptionsList(prescriptions);
            model.setPatientsList(patients);

        } catch (Exception e) {
            System.err.println("[ControllerDispensing] Refresh error: " + e.getMessage());
        }
    }
}
