package presentation.dashboard;

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
import logic.Service;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class ControllerDashboard {

    private ViewDashboard view;
    private AbstractModelDashboard model;
    private Service service;

    public ControllerDashboard(ViewDashboard view, AbstractModelDashboard model){
        this.view = view;
        this.model = model;
        this.service = Service.instance();
        view.setControllerDashboard(this);
        view.setModelDashboard(model);
    }

    public JFreeChart createMedicineLineChart(Date startDate, Date endDate, String[] selectedMedicines) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Map<String, Integer>> medicineData = model.getMedicinesPrescribedByMonth(startDate, endDate);


        System.out.println("Creating medicine chart with date range: " + startDate + " to " + endDate);
        System.out.println("Selected medicines: " + Arrays.toString(selectedMedicines));


        System.out.println("Medicine data size: " + medicineData.size());


        for (String medicine : selectedMedicines) {
            if (medicineData.containsKey(medicine)) {
                System.out.println("Data for " + medicine + ": " + medicineData.get(medicine));
            } else {
                System.out.println("No data found for medicine: " + medicine);
            }
        }


        if (!medicineData.containsKey("Cough Syrup")) {
            medicineData.put("Cough Syrup", new HashMap<>());
        }
        medicineData.get("Cough Syrup").put("9-2025", 5); // Add test value

        for (String medicine : selectedMedicines) {
            if (medicineData.containsKey(medicine)) {
                Map<String, Integer> monthlyData = new TreeMap<>(medicineData.get(medicine));

                for (Map.Entry<String, Integer> entry : monthlyData.entrySet()) {
                    dataset.addValue(entry.getValue(), medicine, entry.getKey());
                }
            }
        }



        JFreeChart chart = ChartFactory.createLineChart(
                "Prescription Medications by Month",
                "Month-Year",
                "Prescription Quantity",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );


        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();


        renderer.setDefaultShapesVisible(true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-6.0, -6.0, 12.0, 12.0));
        renderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-6.0, -6.0, 12.0, 12.0));
        renderer.setDefaultShapesFilled(true);

        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesStroke(1, new BasicStroke(2.5f));

        renderer.setDefaultItemLabelsVisible(true);
        StandardCategoryItemLabelGenerator labelGen = new StandardCategoryItemLabelGenerator();
        renderer.setDefaultItemLabelGenerator(labelGen);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLowerBound(0);


        plot.setBackgroundPaint(Color.WHITE);

        return chart;
    }

    public JFreeChart createStatusPieChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Integer> statusData = model.getPrescriptionsByStatus();

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



}
