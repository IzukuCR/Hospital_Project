package front.presentation.doctors;

import front.presentation.ThreadListener;
import logic.Doctor;
import front.logic.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerDoctor implements ThreadListener {
    ViewDoctor view;
    ModelDoctor model;
    Service service;

    public ControllerDoctor(ViewDoctor view, ModelDoctor model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();

        //new Thread(this::loadDoctorsSafe, "Doctor-Init-Thread").start();
        view.setControllerDoc(this);
        view.setModelDoc(model);
    }

    private void loadDoctorsSafe() {
        try {
            var doctors = service.doctor().getDoctors();
            SwingUtilities.invokeLater(() -> model.setDoctors(doctors));
        } catch (Exception e) {
            System.err.println("[ControllerDoctor] Error loading Doctors: " + e.getMessage());
        }
    }

    public void create(Doctor p) {
        new Thread(() -> {
            try {
                var created = service.doctor().create(p);
                var doctors = service.doctor().getDoctors();
                SwingUtilities.invokeLater(() -> {
                    model.setDoctors(doctors);
                    model.setCurrent(created);
                });
            } catch (Exception e) {
                System.err.println("[ControllerDoctor] Error creando doctor: " + e.getMessage());
            }
        }, "Doctor-Create").start();
    }

    /*public void create(Doctor p) throws Exception {
        Doctor createdDoctor = service.doctor().create(p);
        loadDoctors();
        model.setCurrent(createdDoctor);
    }*/

    public void search(String searchValue, String searchType) throws Exception {
        new Thread(() -> {
            try {
                if ("id".equalsIgnoreCase(searchType)) {
                    var foundDoctor = service.doctor().searchByID(searchValue);
                    SwingUtilities.invokeLater(() -> model.setCurrent(foundDoctor));
                } else if ("name".equalsIgnoreCase(searchType)) {
                    var foundDoctors = service.doctor().searchByName(searchValue);
                    SwingUtilities.invokeLater(() -> {
                        if (foundDoctors.size() == 1) {
                            model.setCurrent(foundDoctors.get(0));
                        } else {
                            model.setDoctors(foundDoctors);
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("[ControllerDoctor] Error searching: " + e.getMessage());
            }
        }, "Doctor-Search").start();
    }

    public List<Doctor> getDoctorsByName(String name) throws Exception {
        return service.doctor().searchByName(name);
    }

    /*public void update(Doctor p) throws Exception {
        Doctor updatedDoctor = service.doctor().update(p);
        loadDoctors();
        model.setCurrent(updatedDoctor);
    }*/

    public void update(Doctor p) {
        new Thread(() -> {
            try {
                var updated = service.doctor().update(p);
                var doctors = service.doctor().getDoctors();
                SwingUtilities.invokeLater(() -> {
                    model.setDoctors(doctors);
                    model.setCurrent(updated);
                });
            } catch (Exception e) {
                System.err.println("[ControllerDoctor] Error uptdating Doctors: " + e.getMessage());
            }
        }, "Doctor-Update").start();
    }

    /*public void delete(String id) throws Exception {
        Doctor doc = new Doctor();
        doc.setId(id);
        service.doctor().delete(doc);
        loadDoctors();
        model.setCurrent(new Doctor());
    }*/

    public void delete(String id) {
        new Thread(() -> {
            try {
                Doctor doc = new Doctor();
                doc.setId(id);
                service.doctor().delete(doc);
                var doctors = service.doctor().getDoctors();
                SwingUtilities.invokeLater(() -> {
                    model.setDoctors(doctors);
                    model.setCurrent(new Doctor());
                });
            } catch (Exception e) {
                System.err.println("[ControllerDoctor] Error eliminando doctor: " + e.getMessage());
            }
        }, "Doctor-Delete").start();
    }

    public void validateLogin(String id, String password) throws Exception {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setPassword(password);

        Doctor validatedDoctor = service.doctor().validateLogin(doctor);
        if (validatedDoctor != null) {
            model.setCurrent(validatedDoctor);
        } else {
            throw new Exception("Invalid doctor credentials");
        }
    }

    public String getUserType(String username, String password) throws Exception {
        return service.log().validateUserType(username, password);
    }

    public List<Doctor> getAllDoctors() throws Exception {
        new Thread(() -> {
            try {
                var allDoctors = service.doctor().getDoctors();
                SwingUtilities.invokeLater(() -> model.setDoctors(allDoctors));
            } catch (Exception e) {
                System.err.println("[ControllerDoctor] Error loading all doctors: " + e.getMessage());
            }
        }).start();
        return model.getDoctors();
    }

    @Override
    public void refresh() {
        try {
            var doctors = service.doctor().getDoctors();
            model.setDoctors(doctors);
        } catch (Exception e) {
            System.err.println("[ControllerDoctor] Error refreshing: " + e.getMessage());
        }
    }
}
