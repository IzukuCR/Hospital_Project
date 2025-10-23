package main.java.presentation.medicines;

import prescription_dispatch.Application;
import prescription_dispatch.data.Data;
import prescription_dispatch.logic.Medicine;
import prescription_dispatch.logic.Service;

import java.util.ArrayList;
import java.util.List;

public class ControllerMedicine {
    ViewMedicine view;
    ModelMedicine model;
    Service service;
    Data data;

    public ControllerMedicine(ViewMedicine view, ModelMedicine model) {
        this.view = view;
        this.model = model;
        this.service = Service.instance();
        this.data = Application.getData();
        this.model.setMedicines(data.getMedicines());
        view.setControllerMed(this);
        view.setModelMed(model);
    }

    public void create(Medicine m) throws Exception {
        Medicine createdMedicine = service.medicine().create(m);
        model.setMedicines(data.getMedicines());
        model.setCurrent(createdMedicine);
    }


    public void search(String searchValue, String searchType) throws Exception {
        if ("code".equalsIgnoreCase(searchType)) {
            Medicine foundMedicine = null;
            for (Medicine med : data.getMedicines()) {
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

            for (Medicine med : data.getMedicines()) {
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
    }

    public List<Medicine> getMedicinesByName(String name) throws Exception {
        List<Medicine> result = new ArrayList<>();
        String searchLower = name.toLowerCase();

        for (Medicine medicine : data.getMedicines()) {
            if (medicine.getName().toLowerCase().contains(searchLower)) {
                result.add(medicine);
            }
        }

        return result;
    }

    public void update(Medicine m) throws Exception {
        Medicine updatedMedicine = service.medicine().update(m);
        model.setMedicines(data.getMedicines());
        model.setCurrent(updatedMedicine);
    }

    public void delete(String code) throws Exception {
        service.medicine().delete(code);

        model.setMedicines(data.getMedicines());
        model.setCurrent(new Medicine());
    }

    public List<Medicine> getAllMedicines() throws Exception {
        List<Medicine> allMedicines = data.getMedicines();

        if (allMedicines.isEmpty()) {
            throw new Exception("No medicines on list.. Add some first");
        }

        model.setMedicines(allMedicines);
        return allMedicines;
    }
}