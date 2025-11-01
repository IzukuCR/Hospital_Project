package front.presentation.dashboard;

import front.presentation.ThreadListener;
import logic.Prescription;
import logic.PrescriptionItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
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

        for (String medicine : selectedMedicines) {
            if (data.containsKey(medicine)) {
                Map<String, Integer> monthData = new TreeMap<>(data.get(medicine));
                for (Map.Entry<String, Integer> entry : monthData.entrySet()) {
                    dataset.addValue(entry.getValue(), medicine, entry.getKey());
                }
            }
        }

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
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLowerBound(0);
        plot.setBackgroundPaint(Color.WHITE);

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
                String medName = item.getMedicineCode();
                result.putIfAbsent(medName, new TreeMap<>());
                Map<String, Integer> monthData = result.get(medName);
                monthData.put(monthYear, monthData.getOrDefault(monthYear, 0) + item.getQuantity());
            }
        }
        return result;
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
