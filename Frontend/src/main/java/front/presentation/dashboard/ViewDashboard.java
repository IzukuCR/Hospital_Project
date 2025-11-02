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
import java.time.ZoneId;

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
    private JButton clearGraphs;
    private String currentUserId;

    private AbstractModelDashboard model;
    private ControllerDashboard controller;

    public ViewDashboard() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // === Validación de componentes ===
        if (selectedMedicinesList == null) {
            System.err.println("ERROR: selectedMedicinesList is null");
            selectedMedicinesList = new JList<>();
        }

        medicinesListModel = new DefaultListModel<>();
        selectedMedicinesList.setModel(medicinesListModel);

        // === Fechas predeterminadas con validación coherente ===
        try {
            Calendar cal = Calendar.getInstance();

            // Fecha final → hoy
            if (endDatePicker != null) {
                endDatePicker.setDate(cal.getTime().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate());
            } else {
                System.err.println("ERROR: endDatePicker is null");
            }

            // Fecha inicial → 6 meses atrás
            cal.add(Calendar.MONTH, -6);
            if (startDatePicker != null) {
                startDatePicker.setDate(cal.getTime().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate());
            } else {
                System.err.println("ERROR: startDatePicker is null");
            }

            // Validar coherencia automática de rango (si se manipula manualmente)
            if (startDatePicker != null && endDatePicker != null) {
                startDatePicker.addDateChangeListener(e -> {
                    if (startDatePicker.getDate().isAfter(endDatePicker.getDate())) {
                        JOptionPane.showMessageDialog(this,
                                "La fecha de inicio no puede ser posterior a la fecha final.",
                                "Fecha inválida",
                                JOptionPane.WARNING_MESSAGE);
                        startDatePicker.setDate(endDatePicker.getDate().minusMonths(1));
                    }
                });

                endDatePicker.addDateChangeListener(e -> {
                    if (endDatePicker.getDate().isBefore(startDatePicker.getDate())) {
                        JOptionPane.showMessageDialog(this,
                                "La fecha final no puede ser anterior a la fecha de inicio.",
                                "Fecha inválida",
                                JOptionPane.WARNING_MESSAGE);
                        endDatePicker.setDate(startDatePicker.getDate().plusMonths(1));
                    }
                });
            }

        } catch (Exception e) {
            System.err.println("Error configuring dates: " + e.getMessage());
            e.printStackTrace();
        }

        // === Listeners de botones ===
        if (addMedicineButton != null) {
            addMedicineButton.addActionListener(e -> {
                String selectedMedicine = (String) medicinesComboBox.getSelectedItem();
                if (selectedMedicine != null && !selectedMedicine.isEmpty()
                        && !medicinesListModel.contains(selectedMedicine)
                        && !"No medicines available".equals(selectedMedicine)) {
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

        if (clearGraphs != null) {
            clearGraphs.addActionListener(e -> {
                // Limpiar gráficos
                chartPanel.removeAll();
                pieChartPanel.removeAll();
                chartPanel.revalidate();
                chartPanel.repaint();
                pieChartPanel.revalidate();
                pieChartPanel.repaint();

                // Limpiar lista de medicamentos seleccionados
                medicinesListModel.clear();

                JOptionPane.showMessageDialog(this, "Charts and medicine list cleared.", "Info", JOptionPane.INFORMATION_MESSAGE);
            });
        } else {
            System.err.println("ERROR: clearGraphs button is null");
        }
    }

    public void generateCharts() {
        try {
            if (medicinesListModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select at least one medicine.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date startDate = Date.from(startDatePicker.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endDatePicker.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            String[] selectedMeds = getSelectedMedicines();

            // 🔹 Actualizar datos del modelo según fechas
            controller.updateDashboardData(startDate, endDate);

            // 🔹 Crear gráficos con datos actualizados
            JFreeChart lineChart = controller.createMedicineLineChart(startDate, endDate, selectedMeds);
            JFreeChart pieChart = controller.createStatusPieChart();

            // 🔹 Mostrar los gráficos
            chartPanel.removeAll();
            chartPanel.add(new ChartPanel(lineChart), BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();

            pieChartPanel.removeAll();
            pieChartPanel.add(new ChartPanel(pieChart), BorderLayout.CENTER);
            pieChartPanel.revalidate();
            pieChartPanel.repaint();

        } catch (Exception e) {
            System.err.println("Error generating charts: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error generating charts: " + e.getMessage());
        }
    }

    private String[] getSelectedMedicines() {
        String[] selected = new String[medicinesListModel.size()];
        for (int i = 0; i < medicinesListModel.size(); i++) {
            selected[i] = medicinesListModel.get(i);
        }
        return selected;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelDashboard.MEDICINES_LIST -> {
                List<Medicine> updatedList = (List<Medicine>) evt.getNewValue();
                medicinesComboBox.removeAllItems();
                if (updatedList == null || updatedList.isEmpty()) {
                    medicinesComboBox.addItem("No medicines available");
                } else {
                    updatedList.forEach(m -> medicinesComboBox.addItem(m.getName()));
                }
                System.out.println("[ViewDashboard] Medicines combo updated.");
            }
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