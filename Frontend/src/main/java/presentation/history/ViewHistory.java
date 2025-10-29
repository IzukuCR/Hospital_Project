package presentation.history;

import logic.*;
import logic.Service;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ViewHistory implements PropertyChangeListener {
    private JPanel panel;
    private JTextField searchFld;
    private JComboBox<String> searchComboBox;
    private JButton searchButton;
    private JTable historyTable;
    private JButton listAllButton;
    private JTextArea detailsArea;
    private JButton clearListButton;
    private JButton clearTextAreaButton;
    private JButton clearSearchButton;
    private String currentUserId;

    private TableModelHistory historyTableModel;

    private static final String SEARCH_BY_PATIENT_ID = "Search by Patient ID";
    private static final String SEARCH_BY_DOCTOR_ID = "Search by Doctor ID"; // CAMBIADO

    private Service service = Service.instance();

    ControllerHistory controllerHistory;
    ModelHistory modelHistory;

    public ViewHistory() {

        //this.panel = new JPanel();
        initializeComponents();
        setupListeners();
    }

    private void initializeComponents() {

        searchComboBox.addItem(SEARCH_BY_PATIENT_ID);
        searchComboBox.addItem(SEARCH_BY_DOCTOR_ID);
        searchComboBox.setSelectedItem(SEARCH_BY_PATIENT_ID);

        int[] cols = {
                TableModelHistory.ID,
                TableModelHistory.PATIENT_ID,
                TableModelHistory.PATIENT_NAME,
                TableModelHistory.CREATION_DATE,
                TableModelHistory.WITHDRAWAL_DATE,
                TableModelHistory.STATUS,
                TableModelHistory.DOCTOR_ID,
                TableModelHistory.DOCTOR_NAME,
                TableModelHistory.ITEMS_COUNT,
                TableModelHistory.MEDICINES_DETAILS,
                TableModelHistory.INSTRUCTIONS
        };

        historyTableModel = new TableModelHistory(cols, new ArrayList<>());
        historyTable.setModel(historyTableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hacer que el área de detalles sea de solo lectura
        detailsArea.setEditable(false);
    }

    private void setupListeners() {
        searchButton.addActionListener(e -> handleSearch());
        listAllButton.addActionListener(e -> handleListAll());
        clearListButton.addActionListener(e -> handleClearList());
        clearTextAreaButton.addActionListener(e -> handleClearTextArea());
        clearSearchButton.addActionListener(e -> handleClearSearch());

        // Listener para selección de tabla
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && historyTable.getSelectedRow() != -1) {
                int selectedRow = historyTable.getSelectedRow();
                Prescription selectedPrescription = historyTableModel.getRowAt(selectedRow);
                displayPrescriptionDetails(selectedPrescription);
            }
        });
    }

    private void handleClearList() {

        historyTableModel.setRows(new ArrayList<>());
        JOptionPane.showMessageDialog(panel, "List cleared successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleClearTextArea() {

        detailsArea.setText("");
        JOptionPane.showMessageDialog(panel, "Text area cleared", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleClearSearch() {

        searchFld.setText("");
        searchComboBox.setSelectedItem(SEARCH_BY_PATIENT_ID);
        JOptionPane.showMessageDialog(panel, "Search cleared", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleSearch() {
        try {
            String searchText = searchFld.getText().trim();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter search text", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String searchType = (String) searchComboBox.getSelectedItem();
            List<Prescription> results;

            if (SEARCH_BY_PATIENT_ID.equals(searchType)) {
                results = controllerHistory.searchByPatient(searchText);
            } else if (SEARCH_BY_DOCTOR_ID.equals(searchType)) {
                results = controllerHistory.searchByDoctorId(searchText);
            } else {
                results = new ArrayList<>();
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "No prescriptions found", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

            historyTableModel.setRows(results);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleListAll() {
        List<Prescription> allPrescriptions = controllerHistory.getAllPrescriptions();
        historyTableModel.setRows(allPrescriptions);
    }

    private void displayPrescriptionDetails(Prescription prescription) {
        StringBuilder details = new StringBuilder();
        details.append("=== PRESCRIPTION DETAILS ===\n\n");
        details.append("ID: ").append(prescription.getId()).append("\n");
        details.append("Patient ID: ").append(prescription.getPatientId()).append("\n");
        try{
            Patient patient = service.patient().readById(prescription.getPatientId());
            if (patient != null) {
                details.append("Patient Name: ").append(patient.getName()).append("\n");
            }

            details.append("Creation Date: ").append(prescription.getCreationDate()).append("\n");
            details.append("Withdrawal Date: ").append(prescription.getWithdrawalDate()).append("\n");
            details.append("Status: ").append(prescription.getStatus()).append("\n");
            details.append("Doctor ID: ").append(prescription.getDoctorId()).append("\n");

            Doctor doctor = service.doctor().searchByID(prescription.getDoctorId());
            if (doctor != null) {
                details.append("Doctor Name: ").append(doctor.getName()).append("\n");
            }

            details.append("\n=== MEDICINES ===\n");
            if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
                for (PrescriptionItem item : prescription.getItems()) {
                    Medicine medicine = service.medicine().readByCode(item.getMedicineCode());
                    details.append("\nMedicine: ").append(medicine != null ? medicine.getName() : item.getMedicineCode()).append("\n");
                    details.append("Code: ").append(item.getMedicineCode()).append("\n");
                    details.append("Quantity: ").append(item.getQuantity()).append("\n");
                    details.append("Duration: ").append(item.getDurationDays()).append(" days\n");
                    details.append("Instructions: ").append(item.getInstructions()).append("\n");
                }
            } else {
                details.append("No medicines in this prescription\n");
            }

            detailsArea.setText(details.toString());

        }catch(Exception e){
            details.append("Patient Name: N/A\n");
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setControllerHistory(ControllerHistory controllerHistory) {
        this.controllerHistory = controllerHistory;
    }

    public void setModelHistory(ModelHistory modelHistory) {
        this.modelHistory = modelHistory;
        modelHistory.addPropertyChangeListener(this);
    }
    public void initializeMessaging(String userId) {
        this.currentUserId = userId;
        logic.Service.MessagingService.getInstance().userLoggedIn(currentUserId);
    }

    public void cleanup() {
        if (currentUserId != null) {
            logic.Service.MessagingService.getInstance().userLoggedOut(currentUserId);
            currentUserId = null;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

}
