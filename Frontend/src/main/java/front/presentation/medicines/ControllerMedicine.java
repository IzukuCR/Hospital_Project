package front.presentation.medicines;

import front.presentation.ThreadListener;
import logic.Medicine;
import front.logic.Service;

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

        //loadMedicines();
        view.setControllerMed(this);
        view.setModelMed(model);
    }

    private void loadMedicines() {
        try{
            List<Medicine> medicines = service.medicine().getMedicines();
            this.model.setMedicines(medicines);
        }catch(Exception e){
            System.out.println("Error loading Medicines");
        }
    }

    public void create(Medicine m) throws Exception {
        Medicine createdMedicine = service.medicine().create(m);
        loadMedicines();
        model.setCurrent(createdMedicine);
    }


    public void search(String searchValue, String searchType) throws Exception {
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

    public void update(Medicine m) throws Exception {
        Medicine updatedMedicine = service.medicine().update(m);
        loadMedicines();
        model.setCurrent(updatedMedicine);
    }

    public void delete(String code) throws Exception {
        service.medicine().delete(code);

        loadMedicines();
        model.setCurrent(new Medicine());
    }

    public List<Medicine> getAllMedicines() throws Exception {
        List<Medicine> allMedicines = null;
        try {
            allMedicines = service.medicine().getMedicines();
            model.setMedicines(allMedicines);
            return allMedicines;
        } catch (Exception e) {
            throw new Exception("No medicines on list.. Add some first");
        }
    }

    @Override
    public void refresh() {
        try {
            loadMedicines();
        } catch (Exception e) {
            System.err.println("Dashboard refresh error: " + e.getMessage());
        }
    }
}