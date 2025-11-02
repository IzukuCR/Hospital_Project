package front.presentation.prescriptions;

import front.logic.Service;
import front.presentation.ThreadListener;
import logic.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControllerPrescription implements ThreadListener {
    ViewPrescription view;
    ModelPrescription model;
    Service service;

    public ControllerPrescription(ViewPrescription view, ModelPrescription model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();

        view.setController(this);
        view.setModel(model);

        new Thread(this::loadInformationSafe, "Prescription-Init-Thread").start();
    }

    private void loadInformationSafe() {
        try {
            var prescriptions = service.prescription().getPrescriptions();
            var medicines = service.medicine().getMedicines();

            SwingUtilities.invokeLater(() -> {
                model.setPrescriptions(prescriptions);
                model.setMedicines(medicines);
            });
        } catch (Exception e) {
            System.err.println("[ControllerPrescription] Error loading info: " + e.getMessage());
        }
    }

    /*public void create(Prescription p) throws Exception {
        try{
            if (p.getItems() == null || p.getItems().isEmpty()) {
                throw new Exception("Prescription must have at least one medicine");
            }

            Patient patient = service.patient().readById(p.getPatientId());
            if (patient == null) {
                throw new Exception("Patient not found with ID: " + p.getPatientId());
            }

            p.setCreationDate(new Date());
            p.setStatus("Issued");


            if (model.getDoctor() != null) {
                p.setDoctorId(model.getDoctor().getId());
            } else {
                throw new Exception("Theres no current doctor");
            }

            // Guardar en base de datos
            service.prescription().create(p);

            // Actualizar modelo
            loadInformation();
            model.setCurrent(p);
        }catch(Exception e){}

    }*/

    public void create(Prescription p) {
        new Thread(() -> {
            try {
                if (p.getItems() == null || p.getItems().isEmpty()) {
                    throw new Exception("Prescription must have at least one medicine");
                }

                var patient = service.patient().readById(p.getPatientId());
                if (patient == null) throw new Exception("Patient not found with ID: " + p.getPatientId());

                p.setCreationDate(new Date());
                p.setStatus("Issued");

                if (model.getDoctor() == null)
                    throw new Exception("No current doctor assigned");

                p.setDoctorId(model.getDoctor().getId());

                service.prescription().create(p);

                var prescriptions = service.prescription().getPrescriptions();
                var medicines = service.medicine().getMedicines();

                SwingUtilities.invokeLater(() -> {
                    model.setPrescriptions(prescriptions);
                    model.setMedicines(medicines);
                    model.setCurrent(p);
                });
            } catch (Exception e) {
                System.err.println("[ControllerPrescription] Error creating: " + e.getMessage());
            }
        }, "Prescription-Create").start();
    }

    /*public void update(Prescription p) throws Exception {
        try{
            Prescription existing = service.prescription().getPrescriptions().stream()
                    .filter(presc -> presc.getId().equals(p.getId()))
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                throw new Exception("Prescription not found for update");
            }


            if ( model.getDoctor() == null || !existing.getDoctorId().equals( model.getDoctor().getId())) {
                throw new Exception("NOT PERMISSIONED: You are not the owner of this prescription");
            }

            existing.setPatientId(p.getPatientId());
            existing.setItems(p.getItems());
            existing.setWithdrawalDate(p.getWithdrawalDate());
            existing.setStatus(p.getStatus());

            // Guardar en base de datos
            service.prescription().update(existing);

            loadInformation();
            model.setCurrent(existing);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    public void update(Prescription p) {
        new Thread(() -> {
            try {
                var prescriptions = service.prescription().getPrescriptions();
                var existing = prescriptions.stream()
                        .filter(pre -> pre.getId().equals(p.getId()))
                        .findFirst()
                        .orElse(null);

                if (existing == null)
                    throw new Exception("Prescription not found for update");

                if (model.getDoctor() == null ||
                        !existing.getDoctorId().equals(model.getDoctor().getId())) {
                    throw new Exception("NOT PERMISSIONED: You are not the owner of this prescription");
                }

                existing.setPatientId(p.getPatientId());
                existing.setItems(p.getItems());
                existing.setWithdrawalDate(p.getWithdrawalDate());
                existing.setStatus(p.getStatus());

                service.prescription().update(existing);

                var updatedPrescriptions = service.prescription().getPrescriptions();
                SwingUtilities.invokeLater(() -> {
                    model.setPrescriptions(updatedPrescriptions);
                    model.setCurrent(existing);
                });
            } catch (Exception e) {
                System.err.println("[ControllerPrescription] Error updating: " + e.getMessage());
            }
        }, "Prescription-Update").start();
    }


    /*public void delete(String id) throws Exception {

        Prescription prescriptionToDelete = getPrescription(id);

        if (prescriptionToDelete != null && model.getDoctor() != null &&
                !prescriptionToDelete.getDoctorId().equals(model.getDoctor().getId())) {
            throw new Exception("NOT PERMISSIONED: You are not the owner of this prescription");
        }
        service.prescription().delete(id);

        loadInformation();
        model.setCurrent(new Prescription());
    }*/

    public void delete(String id) {
        new Thread(() -> {
            try {
                var prescriptionToDelete = getPrescription(id);

                if (prescriptionToDelete != null && model.getDoctor() != null &&
                        !prescriptionToDelete.getDoctorId().equals(model.getDoctor().getId())) {
                    throw new Exception("NOT PERMISSIONED: You are not the owner of this prescription");
                }

                service.prescription().delete(id);

                var prescriptions = service.prescription().getPrescriptions();
                SwingUtilities.invokeLater(() -> {
                    model.setPrescriptions(prescriptions);
                    model.setCurrent(new Prescription());
                });
            } catch (Exception e) {
                System.err.println("[ControllerPrescription] Error deleting: " + e.getMessage());
            }
        }, "Prescription-Delete").start();
    }

    /*public List<Prescription> searchByPatient(String patientIdOrName) {
        try {
            List<Prescription> result = new ArrayList<>();
            List<Prescription> byId = service.prescription().getPrescriptionsByPatientID(patientIdOrName);
            if (byId != null) result.addAll(byId);

            List<Prescription> byName = service.prescription().getPrescriptionsByPatientName(patientIdOrName);
            if (byName != null) {
                for (Prescription p : byName) {
                    boolean exists = false;
                    for (Prescription r : result) {
                        if (r.getId().equals(p.getId())) { exists = true; break; }
                    }
                    if (!exists) result.add(p);
                }
            }
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }*/

    public List<Prescription> searchByPatient(String patientIdOrName) {
        new Thread(() -> {
            try {
                var result = new ArrayList<Prescription>();
                var byId = service.prescription().getPrescriptionsByPatientID(patientIdOrName);
                if (byId != null) result.addAll(byId);

                var byName = service.prescription().getPrescriptionsByPatientName(patientIdOrName);
                if (byName != null) {
                    for (Prescription p : byName) {
                        boolean exists = result.stream().anyMatch(r -> r.getId().equals(p.getId()));
                        if (!exists) result.add(p);
                    }
                }

                SwingUtilities.invokeLater(() -> model.setPrescriptions(result));
            } catch (Exception e) {
                System.err.println("[ControllerPrescription] Error searching by patient: " + e.getMessage());
            }
        }, "Prescription-SearchByPatient").start();

        return model.getPrescriptions();
    }

    /*public void setDoctorSearch(String id){
        try{
            Doctor doc = service.doctor().searchByID(id);
            model.setDoctor(doc);
        }catch (Exception e){}
    }*/

    public void setDoctorSearch(String id) {
        new Thread(() -> {
            try {
                var doc = service.doctor().searchByID(id);
                SwingUtilities.invokeLater(() -> model.setDoctor(doc));
            } catch (Exception e) {
                System.err.println("[ControllerPrescription] Error loading doctor: " + e.getMessage());
            }
        }, "Prescription-SetDoctor").start();
    }

    /*public List<Prescription> getAllPrescriptions() {
        try {
            return new ArrayList<>(service.prescription().getPrescriptions());
        }catch (Exception e){}
        return null;
    }*/

    public List<Prescription> getAllPrescriptions() {
        new Thread(() -> {
            try {
                var list = service.prescription().getPrescriptions();
                SwingUtilities.invokeLater(() -> model.setPrescriptions(list));
            } catch (Exception e) {
                System.err.println("[ControllerPrescription] Error getting all: " + e.getMessage());
            }
        }, "Prescription-GetAll").start();
        return model.getPrescriptions();
    }

    /*public void addItemToCurrent(PrescriptionItem item) {
        Prescription current = model.getCurrent();
        if (current.getItems() == null) {
            current.setItems(new ArrayList<>());
        }
        current.getItems().add(item);
        model.setCurrent(current);
    }*/

    public void addItemToCurrent(PrescriptionItem item) {
        var current = model.getCurrent();
        if (current.getItems() == null) current.setItems(new ArrayList<>());
        current.getItems().add(item);
        SwingUtilities.invokeLater(() -> model.setCurrent(current));
    }

    /*public void removeItemFromCurrent(int index) {
        Prescription current = model.getCurrent();
        if (current.getItems() != null && index >= 0 && index < current.getItems().size()) {
            current.getItems().remove(index);
            model.setCurrent(current);
        }
    }*/

    public void removeItemFromCurrent(int index) {
        var current = model.getCurrent();
        if (current.getItems() != null && index >= 0 && index < current.getItems().size()) {
            current.getItems().remove(index);
            SwingUtilities.invokeLater(() -> model.setCurrent(current));
        }
    }

    /*public void clearCurrent() {
        model.setCurrent(new Prescription());
    }*/

    public void clearCurrent() {
        SwingUtilities.invokeLater(() -> model.setCurrent(new Prescription()));
    }

    /*public Prescription getPrescription(String id) {
        try{
            if (service.prescription().getPrescriptions() != null) {
                for (Prescription prescription : service.prescription().getPrescriptions()) {
                    if (prescription.getId().equals(id)) {
                        return prescription;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }*/

    public Prescription getPrescription(String id) {
        try {
            var prescriptions = service.prescription().getPrescriptions();
            if (prescriptions != null) {
                for (Prescription prescription : prescriptions) {
                    if (prescription.getId().equals(id)) return prescription;
                }
            }
        } catch (Exception e) {
            System.err.println("[ControllerPrescription] Error getting prescription: " + e.getMessage());
        }
        return null;
    }

    public boolean canEditPrescription(String prescriptionId) {
        Doctor currentDoctor = model.getDoctor();
        Prescription prescription = getPrescription(prescriptionId);

        return currentDoctor != null &&
                prescription != null &&
                prescription.getDoctorId().equals(currentDoctor.getId());
    }

    public List<Patient> getAllPatients() throws Exception {
        return service.patient().getPatients();
    }

    public Patient getPatientById(String id) throws Exception {
        return service.patient().readById(id);
    }

    public Medicine getMedicineByCode(String code) throws Exception {
        return service.medicine().readByCode(code);
    }

    public Doctor getDoctorById(String id) throws Exception {
        return service.doctor().searchByID(id);
    }

    @Override
    public void refresh() {
        try {
            var prescriptions = service.prescription().getPrescriptions();
            var medicines = service.medicine().getMedicines();

            model.setPrescriptions(prescriptions);
            model.setMedicines(medicines);

        } catch (Exception e) {
            System.err.println("[ControllerPrescription] Refresh error: " + e.getMessage());
        }
    }
}