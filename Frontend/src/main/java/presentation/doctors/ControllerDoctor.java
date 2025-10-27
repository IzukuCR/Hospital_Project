package presentation.doctors;

import logic.Doctor;
import logic.Service;

import java.util.List;

public class ControllerDoctor {
    ViewDoctor view;
    ModelDoctor model;
    Service service;

    public ControllerDoctor(ViewDoctor view, ModelDoctor model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();
        loadDoctors();
        view.setControllerDoc(this);
        view.setModelDoc(model);
    }

    private void loadDoctors() {
        try {
            model.setDoctors(service.doctor().getDoctors());
        } catch (Exception e) {
            System.err.println("Error loading doctors: " + e.getMessage());
        }
    }

    public void create(Doctor p) throws Exception {
        Doctor createdDoctor = service.doctor().create(p);
        loadDoctors();
        model.setCurrent(createdDoctor);
    }

    public void search(String searchValue, String searchType) throws Exception {
        if ("id".equalsIgnoreCase(searchType)) {
            Doctor foundDoctor = service.doctor().searchByID(searchValue);
            model.setCurrent(foundDoctor);
        } else if ("name".equalsIgnoreCase(searchType)) {
            List<Doctor> foundDoctors = service.doctor().searchByName(searchValue);

            if (foundDoctors.isEmpty()) {
                throw new Exception("No doctors found with name: " + searchValue);
            } else if (foundDoctors.size() == 1) {
                model.setCurrent(foundDoctors.get(0));
            } else {
                model.setDoctors(foundDoctors);
            }
        }
    }

    public List<Doctor> getDoctorsByName(String name) throws Exception {
        return service.doctor().searchByName(name);
    }

    public void update(Doctor p) throws Exception {
        Doctor updatedDoctor = service.doctor().update(p);
        loadDoctors();
        model.setCurrent(updatedDoctor);
    }

    public void delete(String id) throws Exception {
        Doctor doc = new Doctor();
        doc.setId(id);
        service.doctor().delete(doc);
        loadDoctors();
        model.setCurrent(new Doctor());
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
        List<Doctor> allDoctors = service.doctor().getDoctors();

        if (allDoctors.isEmpty()) {
            throw new Exception("No doctors on list.. Add some first");
        }

        model.setDoctors(allDoctors);
        return allDoctors;
    }
}
