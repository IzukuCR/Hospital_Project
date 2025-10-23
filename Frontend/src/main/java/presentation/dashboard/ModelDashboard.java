package main.java.presentation.dashboard;

import prescription_dispatch.logic.Prescription;
import prescription_dispatch.logic.PrescriptionItem;
import prescription_dispatch.logic.Service;

import java.text.SimpleDateFormat;
import java.util.*;

public class ModelDashboard extends AbstractModelDashboard {

    private Service service;
    private Map<String, Map<String, Integer>> medicinesData;
    private Map<String, Integer> statusData;

    public ModelDashboard() {
        super();
        this.service = Service.instance();
        this.medicinesData = new HashMap<>();
        this.statusData = new HashMap<>();
    }

    @Override
    public Map<String, Map<String, Integer>> getMedicinesPrescribedByMonth(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            System.err.println("Dates cannot be null");
            return new HashMap<>();
        }

        Map<String, Map<String, Integer>> result = new HashMap<>();
        try{
            List<Prescription> prescriptions = service.prescription().getPrescriptions();

            if (prescriptions == null || prescriptions.isEmpty()) {
                System.err.println("No prescriptions found");
                return result;
            }

            SimpleDateFormat monthYearFormat = new SimpleDateFormat("M-yyyy");
            Set<String> allMonthYears = generateMonthRange(startDate, endDate);

            for (Prescription prescription : prescriptions) {
                if (prescription == null || prescription.getCreationDate() == null) {
                    continue;
                }

                Date creationDate = prescription.getCreationDate();

                if (creationDate.compareTo(startDate) >= 0 && creationDate.compareTo(endDate) <= 0) {
                    String monthYear = monthYearFormat.format(creationDate);

                    if (prescription.getItems() != null) {
                        for (PrescriptionItem item : prescription.getItems()) {
                            if (item == null || item.getMedicineCode() == null) {
                                continue;
                            }

                            String medicineName = getMedicineName(item.getMedicineCode());

                            result.putIfAbsent(medicineName, new TreeMap<>());
                            Map<String, Integer> monthData = result.get(medicineName);
                            monthData.put(monthYear, monthData.getOrDefault(monthYear, 0) + item.getQuantity());
                        }
                    }
                }
            }

            // Asegurar que todos los medicamentos tengan datos para todos los meses
            for (Map<String, Integer> monthData : result.values()) {
                for (String monthYear : allMonthYears) {
                    monthData.putIfAbsent(monthYear, 0);
                }
            }

            Map<String, Map<String, Integer>> oldData = this.medicinesData;
            this.medicinesData = new HashMap<>(result);
            firePropertyChange(MEDICINE_DATA, oldData, result);
        }catch (Exception e){
            System.err.println("Error fetching prescriptions: " + e.getMessage());

        }
        return result;
    }

    @Override
    public Map<String, Integer> getPrescriptionsByStatus() {
        Map<String, Integer> result = new HashMap<>();

        try{
            List<Prescription> prescriptions = service.prescription().getPrescriptions();
            if (prescriptions == null || prescriptions.isEmpty()) {
                System.err.println("No prescriptions found for status chart");
                return result;
            }

            for (Prescription prescription : prescriptions) {
                if (prescription == null || prescription.getStatus() == null) {
                    continue;
                }

                String status = prescription.getStatus();
                result.put(status, result.getOrDefault(status, 0) + 1);
            }

            Map<String, Integer> oldData = this.statusData;
            this.statusData = new HashMap<>(result);
            firePropertyChange(PRESCRIPTION_STATUS, oldData, result);

            return result;
        } catch (Exception e) {
            System.err.println("Error fetching prescriptions: " + e.getMessage());
        }
        return result;
    }

    private Set<String> generateMonthRange(Date startDate, Date endDate) {
        Set<String> monthYears = new TreeSet<>();
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("M-yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        while (calendar.compareTo(endCalendar) <= 0) {
            monthYears.add(monthYearFormat.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, 1);
        }

        return monthYears;
    }

    private String getMedicineName(String medicineCode) {
        try{
            if (medicineCode == null || service.medicine().getMedicines() == null) {
                return "Unknown";
            }
            return service.medicine().readByCode(medicineCode).getName();
        } catch (Exception e) {
            System.err.println("Error fetching medicines: " + e.getMessage());
            return "Unknown";
        }

    }
}
