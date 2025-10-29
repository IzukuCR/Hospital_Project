package front.presentation.dashboard;

import com.github.lgooddatepicker.components.DatePicker;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import logic.Medicine;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewDashboard extends JPanel implements PropertyChangeListener {
    private JPanel mainPanel;
    private JPanel chartPanel;
    private JPanel pieChartPanel;
    private JPanel controlPanel;
    private JComboBox<String> medicinesComboBox;
    private JButton addMedicineButton;
    private JButton generateButton;
    private JList<String> selectedMedicinesList;
    private DefaultListModel<String> medicinesListModel;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private JButton removeButton;
    private String currentUserId;

    private AbstractModelDashboard model;
    private ControllerDashboard controller;

    public ViewDashboard() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Verificar que todos los componentes existan
        if (selectedMedicinesList == null) {
            System.err.println("ERROR: selectedMedicinesList is null");
            selectedMedicinesList = new JList<>();
        }

        medicinesListModel = new DefaultListModel<>();
        selectedMedicinesList.setModel(medicinesListModel);

        // Configurar fechas predeterminadas con validación
        try {
            Calendar cal = Calendar.getInstance();
            if (endDatePicker != null) {
                endDatePicker.setDate(cal.getTime().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            } else {
                System.err.println("ERROR: endDatePicker is null");
            }

            cal.add(Calendar.MONTH, -6);
            if (startDatePicker != null) {
                startDatePicker.setDate(cal.getTime().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            } else {
                System.err.println("ERROR: startDatePicker is null");
            }
        } catch (Exception e) {
            System.err.println("Error configuring dates: " + e.getMessage());
            e.printStackTrace();
        }

        // Validar que los botones existan antes de añadir listeners
        if (addMedicineButton != null) {
            addMedicineButton.addActionListener(e -> {
                String selectedMedicine = (String) medicinesComboBox.getSelectedItem();
                if (selectedMedicine != null && !selectedMedicine.isEmpty()
                        && !medicinesListModel.contains(selectedMedicine)) {
                    medicinesListModel.addElement(selectedMedicine);
                }
            });
        } else {
            System.err.println("ERROR: addMedicineButton is null");
        }

        if (removeButton != null) {
            removeButton.addActionListener(e -> {
                int selectedIndex = selectedMedicinesList.getSelectedIndex();
                if (selectedIndex != -1) {
                    medicinesListModel.remove(selectedIndex);
                }
            });
        } else {
            System.err.println("ERROR: removeButton is null");
        }

        if (generateButton != null) {
            generateButton.addActionListener(e -> generateCharts());
        } else {
            System.err.println("ERROR: generateButton is null");
        }
    }

    public void generateCharts() {
        try {
            if (medicinesListModel.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please select at least one medicine.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (startDatePicker.getDate() == null || endDatePicker.getDate() == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select valid dates.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date startDate = Date.from(startDatePicker.getDate().atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endDatePicker.getDate().atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault()).toInstant());

            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(this,
                        "Start date must be before end date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generar datos en el modelo (esto dispara los PropertyChangeEvents)
            model.getMedicinesPrescribedByMonth(startDate, endDate);
            model.getPrescriptionsByStatus();

        } catch (Exception e) {
            System.err.println("Error generating charts: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error generating charts: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] getSelectedMedicines() {
        String[] selectedMedicines = new String[medicinesListModel.size()];
        for (int i = 0; i < medicinesListModel.size(); i++) {
            selectedMedicines[i] = medicinesListModel.get(i);
        }
        return selectedMedicines;
    }

    public void loadMedicinesList() {
        medicinesComboBox.removeAllItems();

        List<Medicine> medicines = controller.getAllMedicines();
        if (medicines != null) {
            for (Medicine medicine : medicines) {
                medicinesComboBox.addItem(medicine.getName());
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (AbstractModelDashboard.MEDICINE_DATA.equals(evt.getPropertyName())) {
            // Get selected medicines
            String[] selectedMeds = getSelectedMedicines();

            // Convert LocalDate to Date for the controller
            Date startDate = Date.from(startDatePicker.getDate().atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endDatePicker.getDate().atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault()).toInstant());

            // Create medicine line chart
            JFreeChart lineChart = controller.createMedicineLineChart(startDate, endDate, selectedMeds);

            // Update the chart panel
            chartPanel.removeAll();
            ChartPanel medicineChartPanel = new ChartPanel(lineChart);
            medicineChartPanel.setPreferredSize(new Dimension(600, 400));
            chartPanel.add(medicineChartPanel, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
        }
        else if (AbstractModelDashboard.PRESCRIPTION_STATUS.equals(evt.getPropertyName())) {

            JFreeChart pieChart = controller.createStatusPieChart();

            pieChartPanel.removeAll();
            ChartPanel statusChartPanel = new ChartPanel(pieChart);
            statusChartPanel.setPreferredSize(new Dimension(400, 400));
            pieChartPanel.add(statusChartPanel, BorderLayout.CENTER);
            pieChartPanel.revalidate();
            pieChartPanel.repaint();
        }
    }

    public void setControllerDashboard(ControllerDashboard controller) {
        this.controller = controller;
    }

    public void setModelDashboard(AbstractModelDashboard model) {
        this.model = model;
        this.model.addPropertyChangeListener(this);
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}