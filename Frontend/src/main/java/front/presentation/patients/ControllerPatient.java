package front.presentation.patients;

import front.presentation.ThreadListener;
import logic.Patient;
import front.logic.Service;

import java.util.List;

public class ControllerPatient implements ThreadListener {
    ViewPatient view;
    ModelPatient model;
    Service service;

    public ControllerPatient(ViewPatient view, ModelPatient model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();
        //loadPatients();
        view.setControllerPat(this);
        view.setModelPat(model);
    }

    private void loadPatients() {
        try {
            model.setPatients(service.patient().getPatients());
        } catch (Exception e) {
            System.err.println("Error loading patients: " + e.getMessage());
        }
    }

    public void create(Patient p) throws Exception {
        Patient createdPatient = service.patient().create(p);
        loadPatients();
        model.setCurrent(createdPatient);
    }

    public void search(String searchValue, String searchType) throws Exception {
        if ("id".equalsIgnoreCase(searchType)) {
            Patient foundPatient = service.patient().readById(searchValue);
            model.setCurrent(foundPatient);
        } else if ("name".equalsIgnoreCase(searchType)) {
            List<Patient> foundPatients = service.patient().searchByName(searchValue);

            if (foundPatients.isEmpty()) {
                throw new Exception("No patients found with name: " + searchValue);
            } else if (foundPatients.size() == 1) {
                model.setCurrent(foundPatients.get(0));
            } else {
                model.setPatients(foundPatients);
                throw new Exception("MULTIPLE_RESULTS:" + foundPatients.size());
            }
        }
    }

    public List<Patient> getPatientsByName(String name) throws Exception {
        return service.patient().searchByName(name);
    }

    public void update(Patient p) throws Exception {
        Patient updatedPatient = service.patient().update(p);
        loadPatients();
        model.setCurrent(updatedPatient);
    }

    public void delete(String id) throws Exception {
        Patient pat = new Patient();
        pat.setId(id);
        service.patient().delete(pat);
        loadPatients();
        model.setCurrent(new Patient());
    }

    public String getUserType(String username, String password) throws Exception {
        return service.log().validateUserType(username, password);
    }

    public List<Patient> getAllPatients() throws Exception {
        List<Patient> allPatients = service.patient().getPatients();

        if (allPatients.isEmpty()) {
            throw new Exception("No patients on list.. Add some first");
        }

        model.setPatients(allPatients);
        return allPatients;
    }
    @Override
    public void refresh() {
        try {
            loadPatients();
        } catch (Exception e) {
            System.err.println("Dashboard refresh error: " + e.getMessage());
        }
    }
}
