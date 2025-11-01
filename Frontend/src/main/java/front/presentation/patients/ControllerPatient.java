package front.presentation.patients;

import front.presentation.ThreadListener;
import logic.Patient;
import front.logic.Service;

import javax.swing.*;
import java.util.ArrayList;
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

        //new Thread(this::loadPatientsSafe, "Patient-Init-Thread").start();
    }

    private void loadPatientsSafe() {
        try {
            var patients = service.patient().getPatients();
            SwingUtilities.invokeLater(() -> model.setPatients(patients));
        } catch (Exception e) {
            System.err.println("[ControllerPatient] Error loading patients: " + e.getMessage());
        }
    }

    private void loadPatients() {
        try {
            model.setPatients(service.patient().getPatients());
        } catch (Exception e) {
            System.err.println("Error loading patients: " + e.getMessage());
        }
    }

    /*public void create(Patient p) throws Exception {
        Patient createdPatient = service.patient().create(p);
        loadPatients();
        model.setCurrent(createdPatient);
    }*/

    public void create(Patient p) {
        new Thread(() -> {
            try {
                var created = service.patient().create(p);
                var patients = service.patient().getPatients();
                SwingUtilities.invokeLater(() -> {
                    model.setPatients(patients);
                    model.setCurrent(created);
                });
            } catch (Exception e) {
                System.err.println("[ControllerPatient] Error creating patient: " + e.getMessage());
            }
        }, "Patient-Create").start();
    }

    /*public void search(String searchValue, String searchType) throws Exception {
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
    }*/

    public void search(String searchValue, String searchType) {
        new Thread(() -> {
            try {
                if ("id".equalsIgnoreCase(searchType)) {
                    var found = service.patient().readById(searchValue);
                    SwingUtilities.invokeLater(() -> model.setCurrent(found));
                } else if ("name".equalsIgnoreCase(searchType)) {
                    var foundPatients = service.patient().searchByName(searchValue);

                    SwingUtilities.invokeLater(() -> {
                        if (foundPatients.isEmpty()) {

                            model.setPatients(new ArrayList<>());
                        } else if (foundPatients.size() == 1) {
                            model.setCurrent(foundPatients.get(0));
                            model.setPatients(foundPatients);
                        } else {
                            model.setPatients(foundPatients);
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("[ControllerPatient] Error searching: " + e.getMessage());
            }
        }, "Patient-Search").start();
    }

    public List<Patient> getPatientsByName(String name) {
        try {
            return service.patient().searchByName(name);
        } catch (Exception e) {
            System.err.println("[ControllerPatient] Error searching by name: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /*public void update(Patient p) throws Exception {
        Patient updatedPatient = service.patient().update(p);
        loadPatients();
        model.setCurrent(updatedPatient);
    }*/

    public void update(Patient p) {
        new Thread(() -> {
            try {
                var updated = service.patient().update(p);
                var patients = service.patient().getPatients();
                SwingUtilities.invokeLater(() -> {
                    model.setPatients(patients);
                    model.setCurrent(updated);
                });
            } catch (Exception e) {
                System.err.println("[ControllerPatient] Error updating patient: " + e.getMessage());
            }
        }, "Patient-Update").start();
    }

    /*public void delete(String id) throws Exception {
        Patient pat = new Patient();
        pat.setId(id);
        service.patient().delete(pat);
        loadPatients();
        model.setCurrent(new Patient());
    }*/

    public void delete(String id) {
        new Thread(() -> {
            try {
                var pat = new Patient();
                pat.setId(id);
                service.patient().delete(pat);
                var patients = service.patient().getPatients();
                SwingUtilities.invokeLater(() -> {
                    model.setPatients(patients);
                    model.setCurrent(new Patient());
                });
            } catch (Exception e) {
                System.err.println("[ControllerPatient] Error deleting patient: " + e.getMessage());
            }
        }, "Patient-Delete").start();
    }

    /*public List<Patient> getAllPatients() throws Exception {
        List<Patient> allPatients = service.patient().getPatients();

        if (allPatients.isEmpty()) {
            throw new Exception("No patients on list.. Add some first");
        }

        model.setPatients(allPatients);
        return allPatients;
    }*/

    public List<Patient> getAllPatients() {
        new Thread(() -> {
            try {
                var allPatients = service.patient().getPatients();
                SwingUtilities.invokeLater(() -> model.setPatients(allPatients));
            } catch (Exception e) {
                System.err.println("[ControllerPatient] Error loading all patients: " + e.getMessage());
            }
        }, "Patient-GetAll").start();
        return model.getPatients();
    }
    @Override
    public void refresh() {
        try {
            var patients = service.patient().getPatients();
            model.setPatients(patients);
        } catch (Exception e) {
            System.err.println("[ControllerPatient] Refresh error: " + e.getMessage());
        }
    }
}
