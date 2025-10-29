package front.presentation.pharmacists;

import logic.Pharmacist;
import front.logic.Service;

import java.util.List;

public class ControllerPharmacist {
    ViewPharmacist view;
    ModelPharmacist model;
    Service service;

    public ControllerPharmacist(ViewPharmacist view, ModelPharmacist model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();
        loadPharmacists();
        view.setControllerPharm(this);
        view.setModelPharm(model);
    }

    private void loadPharmacists() {
        try {
            model.setPharmacists(service.pharmacist().getPharmacists());
        } catch (Exception e) {
            System.err.println("Error loading pharmacists: " + e.getMessage());
        }
    }

    public void create(Pharmacist p) throws Exception {
        Pharmacist createdPharmacist = service.pharmacist().create(p);
        loadPharmacists();
        model.setCurrent(createdPharmacist);
    }

    public void search(String searchValue, String searchType) throws Exception {
        if ("id".equalsIgnoreCase(searchType)) {
            Pharmacist foundPharmacist = service.pharmacist().readById(searchValue);
            model.setCurrent(foundPharmacist);
        } else if ("name".equalsIgnoreCase(searchType)) {
            List<Pharmacist> foundPharmacists = service.pharmacist().searchByName(searchValue);

            if (foundPharmacists.isEmpty()) {
                throw new Exception("No pharmacists found with name: " + searchValue);
            } else if (foundPharmacists.size() == 1) {
                model.setCurrent(foundPharmacists.get(0));
            } else {
                model.setPharmacists(foundPharmacists);
            }
        }
    }

    public List<Pharmacist> getPharmacistsByName(String name) throws Exception {
        return service.pharmacist().searchByName(name);
    }

    public void update(Pharmacist p) throws Exception {
        Pharmacist updatedPharmacist = service.pharmacist().update(p);
        loadPharmacists();
        model.setCurrent(updatedPharmacist);
    }

    public void delete(String id) throws Exception {
        Pharmacist pharm = new Pharmacist();
        pharm.setId(id);
        service.pharmacist().delete(pharm);
        loadPharmacists();
        model.setCurrent(new Pharmacist());
    }
    //-------------------- Login Validation -------------------//
    // DIDN'T USE AUTH SERVICE FOR THIS ONE BECAUSE IT WAS CAUSING CIRCULAR DEPENDENCY ISSUES
    //--------------------------------------------------------//
    public void validateLogin(String id, String password) throws Exception {
        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(id);
        pharmacist.setPassword(password);

        Pharmacist validatedPharmacist = service.pharmacist().validateLogin(pharmacist);
        if (validatedPharmacist != null) {
            model.setCurrent(validatedPharmacist);
        } else {
            throw new Exception("Invalid pharmacist credentials");
        }
    }

    public String getUserType(String username, String password) throws Exception {
        return service.log().validateUserType(username, password);
    }

    public List<Pharmacist> getAllPharmacists() throws Exception {
        List<Pharmacist> allPharmacists = service.pharmacist().getPharmacists();

        if (allPharmacists.isEmpty()) {
            throw new Exception("No pharmacists on list.. Add some first");
        }

        model.setPharmacists(allPharmacists);
        return allPharmacists;
    }
}
