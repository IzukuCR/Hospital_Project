package front.presentation.history;

import front.presentation.ThreadListener;
import logic.Doctor;
import logic.Medicine;
import logic.Patient;
import logic.Prescription;
import front.logic.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerHistory implements ThreadListener {
    ViewHistory view;
    ModelHistory model;
    Service service  = Service.instance();

    public ControllerHistory(ViewHistory view, ModelHistory model) {
        this.view = view;
        this.model = model;

        view.setControllerHistory(this);
        view.setModelHistory(model);

        //new Thread(this::loadHistorySafe, "History-Init-Thread").start();
    }

    private void loadHistorySafe() {
        try {
            var prescriptions = service.prescription().getPrescriptions();
            SwingUtilities.invokeLater(() -> model.setPrescriptions(prescriptions));
        } catch (Exception e) {
            System.err.println("[ControllerHistory] Error loading prescriptions: " + e.getMessage());
        }
    }


    /*public List<Prescription> searchByPatient(String patientIdOrName) throws Exception {
        if (patientIdOrName == null || patientIdOrName.isBlank()) {
            throw new Exception("Empty search term");
        }
        try{
            return service.prescription().getPrescriptionsByPatientID(patientIdOrName);
        } catch (Exception e) {
            System.err.println("Error searching prescriptions by patient ID: " + e.getMessage());
            return new ArrayList<>();
        }
    }*/

    public List<Prescription> searchByPatient(String patientIdOrName) {
        if (patientIdOrName == null || patientIdOrName.isBlank()) {
            System.err.println("[ControllerHistory] Empty search term");
            return new ArrayList<>();
        }
        new Thread(() -> {
            try {
                var prescriptions = service.prescription().getPrescriptionsByPatientID(patientIdOrName);
                SwingUtilities.invokeLater(() -> model.setPrescriptions(prescriptions));
            } catch (Exception e) {
                System.err.println("[ControllerHistory] Error searching prescriptions by patient ID: " + e.getMessage());
                SwingUtilities.invokeLater(() -> model.setPrescriptions(new ArrayList<>()));
            }
        }, "History-SearchByPatient").start();
        return model.getPrescriptions();
    }

    /*public List<Prescription> searchByDoctorId(String doctorId) {

        try{
            return service.prescription().getPrescriptionsByDoctor(doctorId);
        } catch (Exception e) {
            System.err.println("Error searching prescriptions by doctor ID: " + e.getMessage());
            return new ArrayList<>();
        }
    }*/

    public List<Prescription> searchByDoctorId(String doctorId) {
        new Thread(() -> {
            try {
                var prescriptions = service.prescription().getPrescriptionsByDoctor(doctorId);
                SwingUtilities.invokeLater(() -> model.setPrescriptions(prescriptions));
            } catch (Exception e) {
                System.err.println("[ControllerHistory] Error searching prescriptions by doctor ID: " + e.getMessage());
                SwingUtilities.invokeLater(() -> model.setPrescriptions(new ArrayList<>()));
            }
        }, "History-SearchByDoctor").start();
        return model.getPrescriptions();
    }

    /*public List<Prescription> getAllPrescriptions() {
        try{
            return service.prescription().getPrescriptions();
        } catch (Exception e) {
            System.err.println("Error searching prescriptions: " + e.getMessage());
            return new ArrayList<>();
        }

    }

    public void getAllPrescriptions() {
        new Thread(() -> {
            try {
                var prescriptions = service.prescription().getPrescriptions();
                SwingUtilities.invokeLater(() -> model.setPrescriptions(prescriptions));
            } catch (Exception e) {
                System.err.println("[ControllerHistory] Error loading all prescriptions: " + e.getMessage());
                SwingUtilities.invokeLater(() -> model.setPrescriptions(new ArrayList<>()));
            }
        }, "History-GetAll").start();
    }

    public Prescription getPrescriptionById(String id) {
        try{
            return service.prescription().readById(id);
        } catch (Exception e) {
            System.err.println("Error searching prescriptions: " + e.getMessage());
            return new Prescription();
        }
    }*/

    public Patient getPatientById(String id) throws Exception {
        return Service.instance().patient().readById(id);
    }

    public Doctor getDoctorById(String id) throws Exception {
        return Service.instance().doctor().searchByID(id);
    }

    public Medicine getMedicineByCode(String code) throws Exception {
        return Service.instance().medicine().readByCode(code);
    }

    @Override
    public void refresh() {
        try {
            var prescriptions = service.prescription().getPrescriptions();
            model.setPrescriptions(prescriptions);
        } catch (Exception e) {
            System.err.println("[ControllerHistory] Refresh error: " + e.getMessage());
        }
    }
}