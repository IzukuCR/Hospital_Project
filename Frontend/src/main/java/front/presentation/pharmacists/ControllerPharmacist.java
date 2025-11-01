package front.presentation.pharmacists;

import front.presentation.ThreadListener;
import logic.Pharmacist;
import front.logic.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerPharmacist implements ThreadListener {
    ViewPharmacist view;
    ModelPharmacist model;
    Service service;

    public ControllerPharmacist(ViewPharmacist view, ModelPharmacist model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();

        view.setControllerPharm(this);
        view.setModelPharm(model);

        //new Thread(this::loadPharmacistsSafe, "Pharmacist-Init-Thread").start();
    }

    private void loadPharmacistsSafe() {
        try {
            var pharmacists = service.pharmacist().getPharmacists();
            SwingUtilities.invokeLater(() -> model.setPharmacists(pharmacists));
        } catch (Exception e) {
            System.err.println("[ControllerPharmacist] Error loading pharmacists: " + e.getMessage());
        }
    }

    private void loadPharmacists() {
        try {
            model.setPharmacists(service.pharmacist().getPharmacists());
        } catch (Exception e) {
            System.err.println("Error loading pharmacists: " + e.getMessage());
        }
    }

    /*public void create(Pharmacist p) throws Exception {
        Pharmacist createdPharmacist = service.pharmacist().create(p);
        loadPharmacists();
        model.setCurrent(createdPharmacist);
    }*/

    public void create(Pharmacist p) {
        new Thread(() -> {
            try {
                var created = service.pharmacist().create(p);
                var pharmacists = service.pharmacist().getPharmacists();
                SwingUtilities.invokeLater(() -> {
                    model.setPharmacists(pharmacists);
                    model.setCurrent(created);
                });
            } catch (Exception e) {
                System.err.println("[ControllerPharmacist] Error creating pharmacist: " + e.getMessage());
            }
        }, "Pharmacist-Create").start();
    }

    /*public void search(String searchValue, String searchType) throws Exception {
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
    }*/

    public void search(String searchValue, String searchType) {
        new Thread(() -> {
            try {
                if ("id".equalsIgnoreCase(searchType)) {
                    var found = service.pharmacist().readById(searchValue);
                    SwingUtilities.invokeLater(() -> model.setCurrent(found));
                } else if ("name".equalsIgnoreCase(searchType)) {
                    var foundPharmacists = service.pharmacist().searchByName(searchValue);
                    SwingUtilities.invokeLater(() -> {
                        if (foundPharmacists.isEmpty()) {
                            model.setPharmacists(new ArrayList<>());
                        } else if (foundPharmacists.size() == 1) {
                            model.setCurrent(foundPharmacists.get(0));
                            model.setPharmacists(foundPharmacists);
                        } else {
                            model.setPharmacists(foundPharmacists);

                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("[ControllerPharmacist] Error searching: " + e.getMessage());
            }
        }, "Pharmacist-Search").start();
    }

    /*public List<Pharmacist> getPharmacistsByName(String name) throws Exception {
        return service.pharmacist().searchByName(name);
    }*/

    public List<Pharmacist> getPharmacistsByName(String name) {
        try {
            return service.pharmacist().searchByName(name);
        } catch (Exception e) {
            System.err.println("[ControllerPharmacist] Error searching by name: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /*public void update(Pharmacist p) throws Exception {
        Pharmacist updatedPharmacist = service.pharmacist().update(p);
        loadPharmacists();
        model.setCurrent(updatedPharmacist);
    }*/

    public void update(Pharmacist p) {
        new Thread(() -> {
            try {
                var updated = service.pharmacist().update(p);
                var pharmacists = service.pharmacist().getPharmacists();
                SwingUtilities.invokeLater(() -> {
                    model.setPharmacists(pharmacists);
                    model.setCurrent(updated);
                });
            } catch (Exception e) {
                System.err.println("[ControllerPharmacist] Error updating pharmacist: " + e.getMessage());
            }
        }, "Pharmacist-Update").start();
    }

    /*public void delete(String id) throws Exception {
        Pharmacist pharm = new Pharmacist();
        pharm.setId(id);
        service.pharmacist().delete(pharm);
        loadPharmacists();
        model.setCurrent(new Pharmacist());
    }*/

    public void delete(String id) {
        new Thread(() -> {
            try {
                var pharm = new Pharmacist();
                pharm.setId(id);
                service.pharmacist().delete(pharm);
                var pharmacists = service.pharmacist().getPharmacists();
                SwingUtilities.invokeLater(() -> {
                    model.setPharmacists(pharmacists);
                    model.setCurrent(new Pharmacist());
                });
            } catch (Exception e) {
                System.err.println("[ControllerPharmacist] Error deleting pharmacist: " + e.getMessage());
            }
        }, "Pharmacist-Delete").start();
    }

    /*public List<Pharmacist> getAllPharmacists() throws Exception {
        List<Pharmacist> allPharmacists = service.pharmacist().getPharmacists();

        if (allPharmacists.isEmpty()) {
            throw new Exception("No pharmacists on list.. Add some first");
        }

        model.setPharmacists(allPharmacists);
        return allPharmacists;
    }*/

    public List<Pharmacist> getAllPharmacists() {
        new Thread(() -> {
            try {
                var allPharmacists = service.pharmacist().getPharmacists();
                SwingUtilities.invokeLater(() -> model.setPharmacists(allPharmacists));
            } catch (Exception e) {
                System.err.println("[ControllerPharmacist] Error loading all pharmacists: " + e.getMessage());
            }
        }, "Pharmacist-GetAll").start();

        return model.getPharmacists();
    }

    @Override
    public void refresh() {
        try {
            var pharmacists = service.pharmacist().getPharmacists();
            model.setPharmacists(pharmacists);
        } catch (Exception e) {
            System.err.println("[ControllerPharmacist] Refresh error: " + e.getMessage());
        }
    }
}
