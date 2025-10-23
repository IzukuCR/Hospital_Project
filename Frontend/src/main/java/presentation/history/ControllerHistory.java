package main.java.presentation.history;

import prescription_dispatch.logic.Prescription;
import prescription_dispatch.logic.Service;

import java.util.ArrayList;
import java.util.List;

public class ControllerHistory {
    ViewHistory view;
    ModelHistory model;
    Service service;

    public ControllerHistory(ViewHistory view, ModelHistory model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();
        view.setControllerHistory(this);
        view.setModelHistory(model);
    }

    public List<Prescription> searchByPatient(String patientIdOrName) throws Exception {
        if (patientIdOrName == null || patientIdOrName.isBlank()) {
            throw new Exception("Empty search term");
        }
        try{
            return service.prescription().getPrescriptionsByPatientID(patientIdOrName);
        } catch (Exception e) {
            System.err.println("Error searching prescriptions by patient ID: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Prescription> searchByDoctorId(String doctorId) {

        try{
            return service.prescription().getPrescriptionsByDoctor(doctorId);
        } catch (Exception e) {
            System.err.println("Error searching prescriptions by doctor ID: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Prescription> getAllPrescriptions() {
        try{
            return service.prescription().getPrescriptions();
        } catch (Exception e) {
            System.err.println("Error searching prescriptions: " + e.getMessage());
            return new ArrayList<>();
        }

    }

    public Prescription getPrescriptionById(String id) {
        try{
            return service.prescription().readById(id);
        } catch (Exception e) {
            System.err.println("Error searching prescriptions: " + e.getMessage());
            return new Prescription();
        }
    }
}