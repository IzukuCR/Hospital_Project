package front.presentation.medicines;

import front.presentation.ThreadListener;
import logic.Medicine;
import front.logic.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerMedicine implements ThreadListener {
    ViewMedicine view;
    ModelMedicine model;
    Service service;


    public ControllerMedicine(ViewMedicine view, ModelMedicine model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();


        view.setControllerMed(this);
        view.setModelMed(model);

        //new Thread(this::loadMedicinesSafe, "Medicine-Init").start();
    }

    private void loadMedicinesSafe() {
        try {
            var medicines = service.medicine().getMedicines();
            SwingUtilities.invokeLater(() -> model.setMedicines(medicines));
        } catch (Exception e) {
            System.err.println("[ControllerMedicine] Error loading medicines: " + e.getMessage());
        }
    }

    /*public void create(Medicine m) throws Exception {
        Medicine createdMedicine = service.medicine().create(m);
        loadMedicines();
        model.setCurrent(createdMedicine);
    }*/

    public void create(Medicine m)  {
        new Thread(() -> {
            try {
                var created = service.medicine().create(m);
                var medicines = service.medicine().getMedicines();
                SwingUtilities.invokeLater(() -> {
                    model.setMedicines(medicines);
                    model.setCurrent(created);
                });
            } catch (Exception e) {
                System.err.println("[ControllerMedicine] Error creating medicine: " + e.getMessage());
            }
        }, "Medicine-Create").start();
    }


    /*public void search(String searchValue, String searchType) throws Exception {
        try{
            if ("code".equalsIgnoreCase(searchType)) {
                Medicine foundMedicine = null;
                for (Medicine med : service.medicine().getMedicines()) {
                    if (med.getCode().equals(searchValue)) {
                        foundMedicine = med;
                        break;
                    }
                }

                if (foundMedicine == null) {
                    throw new Exception("No medicine found with code: " + searchValue);
                }

                model.setCurrent(foundMedicine);

            } else if ("name".equalsIgnoreCase(searchType)) {
                List<Medicine> foundMedicines = new ArrayList<>();
                String searchLower = searchValue.toLowerCase();

                for (Medicine med : service.medicine().getMedicines()) {
                    if (med.getName().toLowerCase().contains(searchLower)) {
                        foundMedicines.add(med);
                    }
                }

                if (foundMedicines.isEmpty()) {
                    throw new Exception("No medicines found with name: " + searchValue);
                } else if (foundMedicines.size() == 1) {
                    model.setCurrent(foundMedicines.get(0));
                    model.setMedicines(foundMedicines);
                } else {
                    Medicine exactMatch = null;
                    for (Medicine med : foundMedicines) {
                        if (med.getName().equalsIgnoreCase(searchValue)) {
                            exactMatch = med;
                            break;
                        }
                    }

                    if (exactMatch != null) {
                        model.setCurrent(exactMatch);
                        model.setMedicines(List.of(exactMatch));
                    } else {
                        model.setMedicines(foundMedicines);
                        throw new Exception("MULTIPLE_RESULTS:" + foundMedicines.size());
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }*/

    public void search(String searchValue, String searchType) {
        new Thread(() -> {
            try {
                var medicines = service.medicine().getMedicines();
                List<Medicine> found = new ArrayList<>();

                if ("code".equalsIgnoreCase(searchType)) {
                    for (var med : medicines) {
                        if (med.getCode().equalsIgnoreCase(searchValue)) {
                            found.add(med);
                            break;
                        }
                    }
                } else if ("name".equalsIgnoreCase(searchType)) {
                    String searchLower = searchValue.toLowerCase();
                    for (var med : medicines) {
                        if (med.getName().toLowerCase().contains(searchLower)) {
                            found.add(med);
                        }
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    if (found.isEmpty()) {

                        model.setMedicines(new ArrayList<>());
                    } else if (found.size() == 1) {
                        model.setCurrent(found.get(0));
                        model.setMedicines(found);
                    } else {
                        model.setMedicines(found);
                    }
                });
            } catch (Exception e) {
                System.err.println("[ControllerMedicine] Error searching: " + e.getMessage());
            }
        }, "Medicine-Search").start();
    }

    public List<Medicine> getMedicinesByName(String name) throws Exception {
        List<Medicine> result = new ArrayList<>();
        try{
            String searchLower = name.toLowerCase();

            for (Medicine medicine : service.medicine().getMedicines()) {
                if (medicine.getName().toLowerCase().contains(searchLower)) {
                    result.add(medicine);
                }
            }

            return result;
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /*public void update(Medicine m) throws Exception {
        Medicine updatedMedicine = service.medicine().update(m);
        loadMedicines();
        model.setCurrent(updatedMedicine);
    }*/

    public void update(Medicine m) {
        new Thread(() -> {
            try {
                var updated = service.medicine().update(m);
                var medicines = service.medicine().getMedicines();
                SwingUtilities.invokeLater(() -> {
                    model.setMedicines(medicines);
                    model.setCurrent(updated);
                });
            } catch (Exception e) {
                System.err.println("[ControllerMedicine] Error updating medicine: " + e.getMessage());
            }
        }, "Medicine-Update").start();
    }

    /*public void delete(String code) throws Exception {
        service.medicine().delete(code);

        loadMedicines();
        model.setCurrent(new Medicine());
    }*/

    public void delete(String code) {
        new Thread(() -> {
            try {
                service.medicine().delete(code);
                var medicines = service.medicine().getMedicines();
                SwingUtilities.invokeLater(() -> {
                    model.setMedicines(medicines);
                    model.setCurrent(new Medicine());
                });
            } catch (Exception e) {
                System.err.println("[ControllerMedicine] Error deleting medicine: " + e.getMessage());
            }
        }, "Medicine-Delete").start();
    }

    /*public List<Medicine> getAllMedicines() throws Exception {
        List<Medicine> allMedicines = null;
        try {
            allMedicines = service.medicine().getMedicines();
            model.setMedicines(allMedicines);
            return allMedicines;
        } catch (Exception e) {
            throw new Exception("No medicines on list.. Add some first");
        }
    }*/

    public List<Medicine> getAllMedicines() {
        new Thread(() -> {
            try {
                var medicines = service.medicine().getMedicines();
                SwingUtilities.invokeLater(() -> model.setMedicines(medicines));
            } catch (Exception e) {
                System.err.println("[ControllerMedicine] Error getting all medicines: " + e.getMessage());
            }
        }, "Medicine-GetAll").start();
        return model.getMedicines();
    }

    @Override
    public void refresh() {
        try {
            var medicines = service.medicine().getMedicines();
            model.setMedicines(medicines);
        } catch (Exception e) {
            System.err.println("[ControllerMedicine] Refresh error: " + e.getMessage());
        }
    }
}