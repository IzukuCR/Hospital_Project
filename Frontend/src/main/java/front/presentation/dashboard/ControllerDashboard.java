package front.presentation.dashboard;

import front.presentation.ThreadListener;
import logic.Prescription;
import logic.PrescriptionItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import logic.Medicine;
import front.logic.Service;
import front.presentation.Refresher;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ControllerDashboard implements ThreadListener {

    private ViewDashboard view;
    private ModelDashboard model;
    private Service service;

    public ControllerDashboard(ViewDashboard view, ModelDashboard model){
        this.view = view;
        this.model = model;
        this.service = Service.instance();
        view.setControllerDashboard(this);
        view.setModelDashboard(model);
    }


    public JFreeChart createMedicineLineChart(Date startDate, Date endDate, String[] selectedMedicines) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Map<String, Integer>> data = model.getMedicinesData();

        // 🔹 Construcción del dataset ordenado
        SimpleDateFormat fmt = new SimpleDateFormat("M-yyyy");
        TreeSet<String> allMonths = new TreeSet<>((a, b) -> {
            try {
                return fmt.parse(a).compareTo(fmt.parse(b));
            } catch (Exception e) {
                return a.compareTo(b);
            }
        });

        for (Map<String, Integer> map : data.values()) {
            allMonths.addAll(map.keySet());
        }

        // 🔹 Generar al menos un rango visible (si hay solo un mes)
        if (allMonths.size() == 1) {
            Calendar cal = Calendar.getInstance();
            Date uniqueDate = null;
            try { uniqueDate = fmt.parse(allMonths.first()); } catch (Exception ignored) {}
            if (uniqueDate != null) {
                cal.setTime(uniqueDate);
                cal.add(Calendar.MONTH, -1);
                allMonths.add(fmt.format(cal.getTime()));
                cal.add(Calendar.MONTH, 2);
                allMonths.add(fmt.format(cal.getTime()));
            }
        }

        for (String medicine : selectedMedicines) {
            Map<String, Integer> monthData = data.get(medicine);
            if (monthData != null) {
                for (String month : allMonths) {
                    int value = monthData.getOrDefault(month, 0);
                    dataset.addValue(value, medicine, month);
                }
            }
        }

        // 🔹 Crear gráfico con mejor apariencia
        JFreeChart chart = ChartFactory.createLineChart(
                "Medicines Prescribed by Month",
                "Month-Year",
                "Quantity",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);

        // 🔹 Eje X: rotar etiquetas
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6)
        );

        // 🔹 Eje Y: valores enteros, inicio en 0
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLowerBound(0);

        // 🔹 Dibujar puntos y líneas
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));

        return chart;
    }

    public JFreeChart createStatusPieChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Integer> statusData = model.getStatusData();

        for (Map.Entry<String, Integer> entry : statusData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        return ChartFactory.createPieChart(
                "Prescription Status Distribution",
                dataset,
                true,
                true,
                false
        );
    }

    public List<Medicine>getAllMedicines(){
        try {
            return service.medicine().getMedicines();
        }catch (Exception e){
            System.err.println("Error fetching medicines: " + e.getMessage());
            return new ArrayList<>();
        }

    }

    private Map<String, Integer> calculateStatusData(List<Prescription> prescriptions) {
        Map<String, Integer> result = new HashMap<>();
        for (Prescription p : prescriptions) {
            if (p.getStatus() != null)
                result.put(p.getStatus(), result.getOrDefault(p.getStatus(), 0) + 1);
        }
        return result;
    }

    private Map<String, Map<String, Integer>> calculateMedicinesByMonth(List<Prescription> prescriptions) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        SimpleDateFormat fmt = new SimpleDateFormat("M-yyyy");

        for (Prescription p : prescriptions) {
            if (p.getItems() == null || p.getCreationDate() == null) continue;
            String monthYear = fmt.format(p.getCreationDate());

            for (PrescriptionItem item : p.getItems()) {
                String medName = getMedicineNameByCode(item.getMedicineCode());
                result.putIfAbsent(medName, new TreeMap<>());
                Map<String, Integer> monthData = result.get(medName);
                monthData.put(monthYear, monthData.getOrDefault(monthYear, 0) + item.getQuantity());
            }
        }
        return result;
    }

    private String getMedicineNameByCode(String code) {
        try {
            Medicine m = service.medicine().readByCode(code);
            return (m != null && m.getName() != null) ? m.getName() : code;
        } catch (Exception e) {
            return code;
        }
    }

    public void updateDashboardData(Date startDate, Date endDate) {
        try {
            List<Prescription> prescriptions = service.prescription().getPrescriptions();

            // Filtrar por rango de fechas
            prescriptions.removeIf(p -> p.getCreationDate() == null ||
                    p.getCreationDate().before(startDate) ||
                    p.getCreationDate().after(endDate));

            Map<String, Map<String, Integer>> medData = calculateMedicinesByMonth(prescriptions);
            Map<String, Integer> statusData = calculateStatusData(prescriptions.stream()
                    .filter(p -> p.getCreationDate() != null &&
                            !p.getCreationDate().before(startDate) &&
                            !p.getCreationDate().after(endDate))
                    .toList());

            model.setMedicinesData(medData);
            model.setStatusData(statusData);

            System.out.println("[ControllerDashboard] Dashboard data updated for selected dates.");
        } catch (Exception e) {
            System.err.println("[ControllerDashboard] Error updating data: " + e.getMessage());
        }
    }

    @Override
    public void refresh() {
        try {
            // 1️⃣ Traer datos desde la BD (a través del Service)
            List<Medicine> medicines = service.medicine().getMedicines();
            List<Prescription> prescriptions = service.prescription().getPrescriptions();

            // 2️⃣ Calcular los mapas y transformaciones necesarias
            Map<String, Map<String, Integer>> medData = calculateMedicinesByMonth(prescriptions);
            Map<String, Integer> statusData = calculateStatusData(prescriptions);

            // 3️⃣ Actualizar el modelo con firePropertyChange()
            model.setMedicinesList(medicines);
            model.setMedicinesData(medData);
            model.setStatusData(statusData);

            System.out.println("[ControllerDashboard] Dashboard refreshed successfully");
        } catch (Exception e) {
            System.err.println("[ControllerDashboard] Refresh error: " + e.getMessage());
        }
    }


}
