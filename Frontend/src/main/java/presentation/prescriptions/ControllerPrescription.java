package presentation.prescriptions;

import logic.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControllerPrescription {
    ViewPrescription view;
    ModelPrescription model;
    Service service;

    public ControllerPrescription(ViewPrescription view, ModelPrescription model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();

        view.setController(this);
        view.setModel(model);

        loadInformation();
    }

    public void loadInformation(){
        try {
            model.setPrescriptions(service.prescription().getPrescriptions());
            model.setMedicines(service.medicine().getMedicines());
        }catch (Exception e){}
    }

    public void create(Prescription p) throws Exception {
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

    }

    public void update(Prescription p) throws Exception {
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
    }


    public void delete(String id) throws Exception {

        Prescription prescriptionToDelete = getPrescription(id);

        if (prescriptionToDelete != null && model.getDoctor() != null &&
                !prescriptionToDelete.getDoctorId().equals(model.getDoctor().getId())) {
            throw new Exception("NOT PERMISSIONED: You are not the owner of this prescription");
        }
        service.prescription().delete(id);

        loadInformation();
        model.setCurrent(new Prescription());
    }

    public List<Prescription> searchByPatient(String patientIdOrName) {
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
    }

    public void setDoctorSearch(String id){
        try{
            Doctor doc = service.doctor().searchByID(id);
            model.setDoctor(doc);
        }catch (Exception e){}
    }
    public List<Prescription> getAllPrescriptions() {
        try {
            return new ArrayList<>(service.prescription().getPrescriptions());
        }catch (Exception e){}
        return null;
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

    }


    public boolean canEditPrescription(String prescriptionId) {
        Doctor currentDoctor = model.getDoctor();
        Prescription prescription = getPrescription(prescriptionId);

        return currentDoctor != null &&
                prescription != null &&
                prescription.getDoctorId().equals(currentDoctor.getId());
    }
}