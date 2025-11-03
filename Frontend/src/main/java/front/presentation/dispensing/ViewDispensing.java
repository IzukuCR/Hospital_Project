package front.presentation.dispensing;

import com.github.lgooddatepicker.components.DatePicker;
import logic.Patient;
import logic.Prescription;
import logic.PrescriptionItem;
import front.presentation.table_models.TableModelItems;
import front.presentation.table_models.TableModelPrescriptions;
import front.Application;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.List;

public class ViewDispensing extends JPanel implements PropertyChangeListener, ListSelectionListener {
    private JPanel panel1;
    private JButton searchButton;
    private JTextField textFieldPatientName;
    private JTable prescriptionsTable;
    private JRadioButton preparingRadioButton;
    private JRadioButton readyRadioButton;
    private JButton saveButton;
    private JButton clearButton;
    private JTextField textFieldStatePrescriptionSelected;
    private JTextField textFieldDoctorPrescription;
    private JTable medicinesTable;
    private DatePicker dateFilter;
    private JRadioButton pickedRadioButton;
    private JButton changeUserButton;

    private ControllerDispensing controller;
    private ModelDispensing model;
    private final ButtonGroup statusGroup = new ButtonGroup();

    public ViewDispensing() {
        prescriptionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prescriptionsTable.getSelectionModel().addListSelectionListener(this);

        statusGroup.add(preparingRadioButton);
        statusGroup.add(readyRadioButton);
        statusGroup.add(pickedRadioButton);

        searchButton.addActionListener(e -> {
            if (controller != null) controller.onSearchPatientRequested();
        });

        dateFilter.addDateChangeListener(event -> {
            LocalDate selectedDate = event.getNewDate();
            if (selectedDate != null && controller != null) {
                List<Prescription> filtered = controller.getPrescriptionsByDateRange(selectedDate);
                model.setPrescriptionsList(filtered);
            }
        });

        clearButton.addActionListener(e -> clearEvent());

        preparingRadioButton.addActionListener(e -> {
            if (preparingRadioButton.isSelected()) {
                prescriptionsTable.setEnabled(false);
                controller.setCurrentPrescriptionStatus("Preparing");
            }
        });

        readyRadioButton.addActionListener(e -> {
            if (readyRadioButton.isSelected()) {
                prescriptionsTable.setEnabled(false);
                controller.setCurrentPrescriptionStatus("Ready");
            }
        });

        pickedRadioButton.addActionListener(e -> {
            if (pickedRadioButton.isSelected()) {
                prescriptionsTable.setEnabled(false);
                controller.setCurrentPrescriptionStatus("Picked");
            }
        });

        saveButton.addActionListener(e -> {
            if (controller != null && controller.getCurrentPrescription() != null) {
                prescriptionsTable.setEnabled(true);
                controller.savePrescription();
                JOptionPane.showMessageDialog(panel1, "Prescription saved successfully.");
                clearEvent();
            }
        });

        changeUserButton.addActionListener(e -> Application.returnToLogin());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        switch (prop) {
            case ModelDispensing.PRESCRIPTION_LIST -> {
                @SuppressWarnings("unchecked")
                List<Prescription> pres = (List<Prescription>) evt.getNewValue();
                updatePrescriptionsTable(pres);
            }
            case ModelDispensing.CURRENT_PRESCRIPTION -> {
                Prescription pr = (Prescription) evt.getNewValue();
                setCurrentPrescription(pr);
            }
            case ModelDispensing.CURRENT_PATIENT -> {
                Patient p = (Patient) evt.getNewValue();
                setPatient(p);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = prescriptionsTable.getSelectedRow();
            if (selectedRow >= 0) {
                TableModelPrescriptions tableModel = (TableModelPrescriptions) prescriptionsTable.getModel();
                Prescription selected = tableModel.getRowAt(selectedRow);
                controller.setCurrentPrescription(selected);
                SwingUtilities.invokeLater(() ->
                        medicinesTable.setModel(new TableModelItems(selected.getItems()))
                );
            }
        }
    }

    public void setPatient(Patient p) {
        textFieldPatientName.setText(p != null ? p.getName() : "");
        if (p != null) {
            textFieldPatientName.setEditable(false);
            textFieldPatientName.setBackground(Color.LIGHT_GRAY);
        }
    }

    public void setCurrentPrescription(Prescription p) {
        textFieldStatePrescriptionSelected.setText(p != null ? p.getStatus() : "None");
        if (p != null) {
            textFieldStatePrescriptionSelected.setEditable(false);
            textFieldStatePrescriptionSelected.setBackground(Color.LIGHT_GRAY);
            textFieldDoctorPrescription.setText(p.getDoctorId());
            textFieldDoctorPrescription.setEditable(false);

            preparingRadioButton.setEnabled("Issued".equals(p.getStatus()));
            readyRadioButton.setEnabled("Preparing".equals(p.getStatus()));
            pickedRadioButton.setEnabled("Ready".equals(p.getStatus()));
        }
    }

    public void clearEvent() {
        textFieldPatientName.setText("");
        textFieldStatePrescriptionSelected.setText("");
        textFieldDoctorPrescription.setText("");
        prescriptionsTable.clearSelection();
        medicinesTable.setModel(new TableModelItems(List.of()));
        statusGroup.clearSelection();
        prescriptionsTable.setEnabled(true);
        if (controller != null) {
            controller.setCurrentPrescription(new Prescription());
        }
    }

    public void setController(ControllerDispensing controller) { this.controller = controller; }
    public void setModel(ModelDispensing model) {
        this.model = model;
        this.model.addPropertyChangeListener(this);
        updatePrescriptionsTable(model.getPrescriptionsList());
    }
    public JPanel getPanel() { return panel1; }

    public void updatePrescriptionsTable(List<Prescription> prescriptions) {
        TableModelPrescriptions tableModel = new TableModelPrescriptions(
                new int[]{TableModelPrescriptions.PatientID, TableModelPrescriptions.State,
                        TableModelPrescriptions.CreationDate, TableModelPrescriptions.PickupDate},
                prescriptions);
        prescriptionsTable.setModel(tableModel);
    }
}
