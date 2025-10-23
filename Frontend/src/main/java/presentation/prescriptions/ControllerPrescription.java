package main.java.presentation.prescriptions;

import prescription_dispatch.Application;
import prescription_dispatch.data.Data;
import prescription_dispatch.logic.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControllerPrescription {
    prescription_dispatch.presentation.prescriptions.ViewPrescription view;
    ModelPrescription model;
    Service service;
    Data data;

    public ControllerPrescription(prescription_dispatch.presentation.prescriptions.ViewPrescription view, ModelPrescription model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();
        this.data = Application.getData();
        this.model.setPrescriptions(new ArrayList<>());
        view.setController(this);
        view.setModel(model);
    }

    public void create(Prescription p) throws Exception {
        if (p.getItems() == null || p.getItems().isEmpty()) {
            throw new Exception("Prescription must have at least one medicine");
        }

        Patient patient = data.findPatientById(p.getPatientId());
        if (patient == null) {
            throw new Exception("Patient not found with ID: " + p.getPatientId());
        }

        for (PrescriptionItem item : p.getItems()) {
            Medicine medicine = data.findMedicineByCode(item.getMedicineCode());
            if (medicine == null) {
                throw new Exception("Medicine not found with code: " + item.getMedicineCode());
            }
        }

        p.setCreationDate(new Date());
        p.setStatus("Issued");

        Doctor currentDoctor = data.getCurrentDoctor();
        if (currentDoctor != null) {
            p.setDoctorId(currentDoctor.getId());
        } else {
            throw new Exception("Theres no current doctor");
        }

        // Guardar en base de datos
        service.prescription().create(p);

        // Actualizar modelo
        data.getPrescriptions().add(p);
        model.setPrescriptions(data.getPrescriptions());
        model.setCurrent(p);
    }

    public void update(Prescription p) throws Exception {
        Prescription existing = data.getPrescriptions().stream()
                .filter(presc -> presc.getId().equals(p.getId()))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            throw new Exception("Prescription not found for update");
        }

        Doctor currentDoctor = data.getCurrentDoctor();
        if (currentDoctor == null || !existing.getDoctorId().equals(currentDoctor.getId())) {
            throw new Exception("NOT PERMISSIONED: You are not the owner of this prescription");
        }

        existing.setPatientId(p.getPatientId());
        existing.setItems(p.getItems());
        existing.setWithdrawalDate(p.getWithdrawalDate());
        existing.setStatus(p.getStatus());

        // Guardar en base de datos
        service.prescription().update(existing);

        model.setPrescriptions(data.getPrescriptions());
        model.setCurrent(existing);
    }


    public void delete(String id) throws Exception {
        Doctor currentDoctor = data.getCurrentDoctor();
        Prescription prescriptionToDelete = getPrescription(id);

        if (prescriptionToDelete != null && currentDoctor != null &&
                !prescriptionToDelete.getDoctorId().equals(currentDoctor.getId())) {
            throw new Exception("NOT PERMISSIONED: You are not the owner of this prescription");
        }

        // Eliminar de base de datos
        service.prescription().delete(id);

        // Actualizar lista local
        data.getPrescriptions().removeIf(p -> p.getId().equals(id));
        model.setPrescriptions(data.getPrescriptions());
        model.setCurrent(new Prescription());
    }

    public List<Prescription> searchByPatient(String patientIdOrName) {
        List<Prescription> result = new ArrayList<>();
        String searchLower = patientIdOrName.toLowerCase();

        for (Prescription prescription : data.getPrescriptions()) {

            if (prescription.getPatientId().toLowerCase().contains(searchLower)) {
                result.add(prescription);
                continue;
            }

            Patient patient = data.findPatientById(prescription.getPatientId());
            if (patient != null && patient.getName().toLowerCase().contains(searchLower)) {
                result.add(prescription);
            }
        }

        return result;
    }

    public List<Prescription> getAllPrescriptions() {
        return new ArrayList<>(data.getPrescriptions());
    }

    public void addItemToCurrent(PrescriptionItem item) {
        Prescription current = model.getCurrent();
        if (current.getItems() == null) {
            current.setItems(new ArrayList<>());
        }
        current.getItems().add(item);
        model.setCurrent(current);
    }

    public void removeItemFromCurrent(int index) {
        Prescription current = model.getCurrent();
        if (current.getItems() != null && index >= 0 && index < current.getItems().size()) {
            current.getItems().remove(index);
            model.setCurrent(current);
        }
    }

    public void clearCurrent() {
        model.setCurrent(new Prescription());
    }

    public Prescription getPrescription(String id) {
        if (data.getPrescriptions() != null) {
            for (Prescription prescription : data.getPrescriptions()) {
                if (prescription.getId().equals(id)) {
                    return prescription;
                }
            }
        }
        return null;
    }


    public boolean canEditPrescription(String prescriptionId) {
        Doctor currentDoctor = data.getCurrentDoctor();
        Prescription prescription = getPrescription(prescriptionId);

        return currentDoctor != null &&
                prescription != null &&
                prescription.getDoctorId().equals(currentDoctor.getId());
    }
}