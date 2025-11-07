package front.presentation.prescriptions;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import front.Application;
import front.logic.Service;
import logic.*;
import front.presentation.Highlighter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewPrescription implements PropertyChangeListener {
    private JPanel panel;
    private JTextField searchFld;
    private JComboBox<String> searchComboBox;
    private JButton searchButton;
    private JTable prescriptionsTable;
    private JTextField idFld;
    private JTextField patientIdFld;
    private JTextField patientNameFld;
    private JButton searchPatientButton;
    private JTable itemsTable;
    private JButton addItemButton;
    private JButton removeItemButton;
    private JButton saveItemButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton listButton;
    private JTextField medicineCodeFld;
    private JTextField medicineNameFld;
    private JTextField quantityFld;
    private JTextField instructionsFld;
    private JTextField durationFld;
    private JButton searchMedicineButton;
    private DatePicker datePicker;
    private JTextField docNameFld;
    private JTextField docIdFld;
    private JButton changeUserButton;
    private JButton clearTableButton;
    private String currentUserId;

    private TableModelPrescription prescriptionsTableModel;
    private DefaultTableModel itemsTableModel;

    private static final String SEARCH_BY_PATIENT_ID = "Search by Patient ID";
    private static final String SEARCH_BY_PATIENT_NAME = "Search by Patient Name";

    ControllerPrescription controller;
    ModelPrescription model;

    public ViewPrescription() {
        initializeSearchComboBox();

        initializePrescriptionsTable();
        initializeItemsTable();

        initializeDatePicker();

        idFld.setEditable(false);
        patientIdFld.setEditable(false);
        patientNameFld.setEditable(false);
        medicineCodeFld.setEditable(false);
        medicineNameFld.setEditable(false);
        docNameFld.setEditable(false);
        docIdFld.setEditable(false);

        //setDoctor(model.getDoctor());

        changeUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.returnToLogin();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });

        searchPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePatientSearch();
            }
        });

        searchMedicineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleMedicineSearch();
            }
        });

        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddItem();
            }
        });

        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRemoveItem();
            }
        });

        saveItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClear();
            }
        });

        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleListAll();
            }
        });

        clearTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClearTable();
            }
        });

        prescriptionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && prescriptionsTable.getSelectedRow() != -1) {
                int selectedRow = prescriptionsTable.getSelectedRow();
                Prescription selectedPrescription = prescriptionsTableModel.getRowAt(selectedRow);
                displayPrescription(selectedPrescription);
            }
        });

        Highlighter highlighter = new Highlighter(Color.GREEN);
        searchFld.addMouseListener(highlighter);
        patientIdFld.addMouseListener(highlighter);
        medicineCodeFld.addMouseListener(highlighter);
        quantityFld.addMouseListener(highlighter);
        instructionsFld.addMouseListener(highlighter);
        durationFld.addMouseListener(highlighter);
    }

    public void setDoctor(Doctor doctor) {
        if (doctor != null) {
            docIdFld.setText(doctor.getId());
            docNameFld.setText(doctor.getName());
        } else {
            docIdFld.setText("---");
            docNameFld.setText("NOT AUTHENTICATED ---");
        }
    }

    private void initializeDatePicker() {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("yyyy-MM-dd");
        settings.setAllowKeyboardEditing(false);
        settings.setLocale(java.util.Locale.ENGLISH);

        datePicker.setSettings(settings);

        datePicker.addDateChangeListener(dateChangeEvent -> {
            LocalDate selectedDate = datePicker.getDate();
            System.out.println("=== DEBUG DatePicker ===");
            System.out.println("Selected date: " + selectedDate);
            System.out.println("Event date: " + dateChangeEvent.getNewDate());
        });
    }

    private void initializeSearchComboBox() {
        searchComboBox.addItem(SEARCH_BY_PATIENT_ID);
        searchComboBox.addItem(SEARCH_BY_PATIENT_NAME);
        searchComboBox.setSelectedItem(SEARCH_BY_PATIENT_ID);
    }

    private void initializePrescriptionsTable() {
        int[] cols = {
                TableModelPrescription.ID,
                TableModelPrescription.PATIENT,
                TableModelPrescription.CREATION_DATE,
                TableModelPrescription.WITHDRAWAL_DATE,
                TableModelPrescription.STATUS,
                TableModelPrescription.ITEMS_COUNT,
                TableModelPrescription.DOCTOR
        };

        prescriptionsTableModel = new TableModelPrescription(cols, new ArrayList<>());
        prescriptionsTable.setModel(prescriptionsTableModel);
        prescriptionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initializeItemsTable() {
        itemsTableModel = new DefaultTableModel();
        itemsTableModel.addColumn("Medicine Code");
        itemsTableModel.addColumn("Medicine Name");
        itemsTableModel.addColumn("Quantity");
        itemsTableModel.addColumn("Instructions");
        itemsTableModel.addColumn("Duration (days)");

        itemsTable.setModel(itemsTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void handleClearTable() {

        searchFld.setText("");

        prescriptionsTableModel.setRows(new ArrayList<>());

        JOptionPane.showMessageDialog(panel,
                "Prescriptions table cleared",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private Date convertToDate(LocalDate localDate) {
        if (localDate == null) {
            System.out.println("LocalDate is null in convertToDate");
            return null;
        }
        try {
            return Date.from(localDate.atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant());
        } catch (Exception e) {
            System.out.println("Error converting LocalDate to Date: " + e.getMessage());
            return null;
        }
    }

    private LocalDate convertToLocalDate(Date date) {
        if (date == null) return null;
        try {
            return date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        } catch (Exception e) {
            System.out.println("Error converting Date to LocalDate: " + e.getMessage());
            return null;
        }
    }

    private void handleSearch() {
        try {
            String searchText = searchFld.getText().trim();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter search text", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String searchType = (String) searchComboBox.getSelectedItem();

            List<Prescription> results = controller.searchByPatient(searchText);


            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "No prescriptions found", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

            prescriptionsTableModel.setRows(results);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePatientSearch() {
        try {
            List<Patient> allPatients = model.getPatients();

            if (allPatients.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "No patients available. Please add patients first.",
                        "No Patients",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog patientDialog = new JDialog((Frame) null, "Select Patient", true);
            patientDialog.setSize(700, 500);
            patientDialog.setLocationRelativeTo(panel);

            JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));
            dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
            JTextField searchField = new JTextField(20);
            JButton searchButton = new JButton("Search");
            searchButton.setBackground(Color.decode("#476194"));
            searchButton.setForeground(Color.white);
            searchButton.setOpaque(true);

            searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);

            String[] columnNames = {"ID", "Name", "Birth Date", "Phone"};
            DefaultTableModel patientTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            JTable patientTable = new JTable(patientTableModel);
            patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            patientTable.getTableHeader().setReorderingAllowed(false);
            JScrollPane scrollPane = new JScrollPane(patientTable);

            for (Patient patient : allPatients) {
                patientTableModel.addRow(new Object[]{
                        patient.getId(),
                        patient.getName(),
                        patient.getBirthDate(),
                        patient.getPhoneNumber()
                });
            }

            Runnable filterPatients = () -> {
                String searchText = searchField.getText().toLowerCase().trim();
                patientTableModel.setRowCount(0);

                for (Patient patient : allPatients) {
                    if (searchText.isEmpty() ||
                            patient.getId().toLowerCase().contains(searchText) ||
                            patient.getName().toLowerCase().contains(searchText)) {
                        patientTableModel.addRow(new Object[]{
                                patient.getId(),
                                patient.getName(),
                                patient.getBirthDate(),
                                patient.getPhoneNumber()
                        });
                    }
                }
            };

            searchField.addActionListener(e -> filterPatients.run());
            searchButton.addActionListener(e -> filterPatients.run());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton selectButton = new JButton("Select");
            JButton cancelButton = new JButton("Cancel");
            selectButton.setBackground(Color.decode("#52AC41"));
            cancelButton.setBackground(Color.decode("#AF4C4C"));
            selectButton.setForeground(Color.black);
            cancelButton.setForeground(Color.black);
            selectButton.setOpaque(true);
            cancelButton.setOpaque(true);

            selectButton.addActionListener(e -> {
                int selectedRow = patientTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String patientId = (String) patientTable.getValueAt(selectedRow, 0);
                    String patientName = (String) patientTable.getValueAt(selectedRow, 1);
                    patientIdFld.setText(patientId);
                    patientNameFld.setText(patientName);
                    patientDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(patientDialog,
                            "Please select a patient from the list",
                            "Selection Required",
                            JOptionPane.WARNING_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> patientDialog.dispose());
            buttonPanel.add(selectButton);
            buttonPanel.add(cancelButton);

            dialogPanel.add(searchPanel, BorderLayout.NORTH);
            dialogPanel.add(scrollPane, BorderLayout.CENTER);
            dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

            patientTable.setAutoCreateRowSorter(true);
            patientTable.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        selectButton.doClick();
                    }
                }
            });

            patientDialog.setContentPane(dialogPanel);
            patientDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel,
                    "Error accessing patients data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleMedicineSearch() {
        if (model.getMedicines().isEmpty()) {
            JOptionPane.showMessageDialog(panel,
                    "No medicines available. Please add medicines first.",
                    "No Medicines",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog medicineDialog = new JDialog((Frame) null, "Select Medicine", true);
        medicineDialog.setSize(700, 500);
        medicineDialog.setLocationRelativeTo(panel);

        JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(Color.decode("#476194"));

        searchButton.setForeground(Color.white);
        searchButton.setOpaque(true);
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        String[] columnNames = {"Code", "Name", "Presentation"};
        DefaultTableModel medicineTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable medicineTable = new JTable(medicineTableModel);
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineTable.getTableHeader().setReorderingAllowed(false);

        medicineTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        medicineTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        medicineTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(medicineTable);

        List<Medicine> allMedicines = model.getMedicines();
        for (Medicine medicine : allMedicines) {
            medicineTableModel.addRow(new Object[]{
                    medicine.getCode(),
                    medicine.getName(),
                    medicine.getPresentation() != null ? medicine.getPresentation() : ""
            });
        }

        Runnable filterMedicines = () -> {
            String searchText = searchField.getText().toLowerCase().trim();
            medicineTableModel.setRowCount(0);

            for (Medicine medicine : allMedicines) {
                if (searchText.isEmpty() ||
                        medicine.getCode().toLowerCase().contains(searchText) ||
                        medicine.getName().toLowerCase().contains(searchText) ||
                        (medicine.getPresentation() != null && medicine.getPresentation().toLowerCase().contains(searchText))) {

                    medicineTableModel.addRow(new Object[]{
                            medicine.getCode(),
                            medicine.getName(),
                            medicine.getPresentation() != null ? medicine.getPresentation() : ""
                    });
                }
            }
        };

        searchField.addActionListener(e -> filterMedicines.run());
        searchButton.addActionListener(e -> filterMedicines.run());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton selectButton = new JButton("Select");
        JButton cancelButton = new JButton("Cancel");

        selectButton.setBackground(Color.decode("#52AC41"));  // Verde
        cancelButton.setBackground(Color.decode("#AF4C4C"));  // Rojo


        selectButton.setForeground(Color.black);
        cancelButton.setForeground(Color.black);


        selectButton.setOpaque(true);
        cancelButton.setOpaque(true);

        selectButton.addActionListener(e -> {
            int selectedRow = medicineTable.getSelectedRow();
            if (selectedRow >= 0) {
                String medicineCode = (String) medicineTable.getValueAt(selectedRow, 0);
                String medicineName = (String) medicineTable.getValueAt(selectedRow, 1);
                String medicinePresentation = (String) medicineTable.getValueAt(selectedRow, 2);

                medicineCodeFld.setText(medicineCode);
                medicineNameFld.setText(medicineName + " (" + medicinePresentation + ")");

                medicineDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(medicineDialog,
                        "Please select a medicine from the list",
                        "Selection Required",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> medicineDialog.dispose());

        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);

        dialogPanel.add(searchPanel, BorderLayout.NORTH);
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        medicineTable.setAutoCreateRowSorter(true);

        medicineTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    selectButton.doClick();
                }
            }
        });

        medicineDialog.setContentPane(dialogPanel);
        medicineDialog.setVisible(true);
    }

    private void handleAddItem() {
        try {
            String medicineCode = medicineCodeFld.getText().trim();
            String quantityText = quantityFld.getText().trim();
            String instructions = instructionsFld.getText().trim();
            String durationText = durationFld.getText().trim();

            if (medicineCode.isEmpty() || quantityText.isEmpty() || instructions.isEmpty() || durationText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill all medicine fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            int duration = Integer.parseInt(durationText);

            PrescriptionItem item = new PrescriptionItem();
            item.setMedicineCode(medicineCode);
            item.setQuantity(quantity);
            item.setInstructions(instructions);
            item.setDurationDays(duration);

            controller.addItemToCurrent(item);
            refreshItemsTable();

            medicineCodeFld.setText("");
            medicineNameFld.setText("");
            quantityFld.setText("");
            instructionsFld.setText("");
            durationFld.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel, "Quantity and duration must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRemoveItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow >= 0) {
            controller.removeItemFromCurrent(selectedRow);
            refreshItemsTable();
        } else {
            JOptionPane.showMessageDialog(panel, "Please select an item to remove", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleSave() {
        try {

            if (model.getDoctor() == null) {
                JOptionPane.showMessageDialog(panel,
                        "No current doctor available. Please select a doctor first.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si estamos editando una prescripción existente
            Prescription current = model.getCurrent();
            if (current != null && current.getId() != null && !current.getId().isEmpty()) {
                // Validar que la prescripción pertenezca al médico actual
                if (!current.getDoctorId().equals(model.getDoctor().getId())) {
                    JOptionPane.showMessageDialog(panel,
                            "Cannot edit prescription. It does not belong to the current doctor.",
                            "Error of permission", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Lógica para actualizar prescripción existente
                handleUpdate();
                return;
            }

            // Lógica para crear nueva prescripción
            LocalDate selectedDate = datePicker.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(panel,
                        "Please select a withdrawal date",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (patientIdFld.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please select a patient", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (current == null || current.getItems() == null || current.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please add at least one medicine to the prescription", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear nueva prescripción
            Prescription newPrescription = new Prescription();
            newPrescription.setPatientId(patientIdFld.getText().trim());
            newPrescription.setWithdrawalDate(convertToDate(selectedDate));
            newPrescription.setDoctorId(model.getDoctor().getId());
            newPrescription.setItems(new ArrayList<>(current.getItems()));
            newPrescription.setId("P" + System.currentTimeMillis());

            controller.create(newPrescription);
            JOptionPane.showMessageDialog(panel, "Prescription created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            handleClear();
            handleListAll();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        try {
            if (model.getCurrent() == null || model.getCurrent().getId() == null) {
                JOptionPane.showMessageDialog(panel, "No prescription selected for update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate selectedDate = datePicker.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(panel, "Please select a withdrawal date", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Prescription current = model.getCurrent();
            current.setPatientId(patientIdFld.getText().trim());
            current.setWithdrawalDate(convertToDate(selectedDate));

            controller.update(current);
            JOptionPane.showMessageDialog(panel, "Prescription updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            handleClear();
            handleListAll();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        try {
            String id = idFld.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "No prescription selected", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            Doctor currentDoctor = model.getDoctor();
            Prescription selectedPrescription = controller.getPrescription(id);

            if (selectedPrescription != null && currentDoctor != null &&
                    !selectedPrescription.getDoctorId().equals(currentDoctor.getId())) {
                JOptionPane.showMessageDialog(panel,
                        "Cant delete other Doctor prescriptions",
                        "permission denied", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to delete this prescription?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.delete(id);
                JOptionPane.showMessageDialog(panel, "Prescription deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                handleClear();
                handleListAll();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClear() {
        idFld.setText("");
        patientIdFld.setText("");
        patientNameFld.setText("");

        itemsTableModel.setRowCount(0);
        controller.clearCurrent();

        datePicker.setDate(null);

        medicineCodeFld.setText("");
        medicineNameFld.setText("");
        quantityFld.setText("");
        instructionsFld.setText("");
        durationFld.setText("");

        setPrescriptionEditable(true);


        if (model.getDoctor() != null) {
            docIdFld.setText(model.getDoctor() .getId());
            docNameFld.setText(model.getDoctor() .getName());
        }
    }

    private void handleListAll() {
        prescriptionsTableModel.setRows(model.getPrescriptions());
    }

    private void displayPrescription(Prescription prescription) {
        if (prescription == null) return;

        idFld.setText(prescription.getId());
        patientIdFld.setText(prescription.getPatientId());

        // Ya no consultamos al server
        patientNameFld.setText(prescription.getPatientId());

        if (model.getDoctor() != null) {
            docIdFld.setText(model.getDoctor().getId());
            docNameFld.setText(model.getDoctor().getName());
        }

        // Cargar items sin consultar al server
        itemsTableModel.setRowCount(0);
        if (prescription.getItems() != null) {
            for (PrescriptionItem item : prescription.getItems()) {
                itemsTableModel.addRow(new Object[]{
                        item.getMedicineCode(),
                        item.getMedicineCode(), // opcional mostrar código, no buscamos nombre aquí
                        item.getQuantity(),
                        item.getInstructions(),
                        item.getDurationDays()
                });
            }
        }

        if (prescription.getWithdrawalDate() != null) {
            LocalDate withdrawalDate = convertToLocalDate(prescription.getWithdrawalDate());
            datePicker.setDate(withdrawalDate);
        } else {
            datePicker.setDate(null);
        }

        model.setCurrent(prescription);

        boolean isOwnPrescription = controller.canEditPrescription(prescription.getId());
        setPrescriptionEditable(isOwnPrescription);
    }

    private void setPrescriptionEditable(boolean editable) {
        boolean isNewPrescription = idFld.getText().isEmpty();

        if (isNewPrescription) {
            editable = true;
        }

        patientIdFld.setEnabled(editable);
        searchPatientButton.setEnabled(editable);
        datePicker.setEnabled(editable);

        medicineCodeFld.setEnabled(editable);
        searchMedicineButton.setEnabled(editable);
        quantityFld.setEnabled(editable);
        instructionsFld.setEnabled(editable);
        durationFld.setEnabled(editable);
        addItemButton.setEnabled(editable);
        removeItemButton.setEnabled(editable);

        saveItemButton.setEnabled(editable || isNewPrescription);
        deleteButton.setEnabled(editable);

        Color bgColor = editable ? Color.WHITE : new Color(240, 240, 240);
        patientIdFld.setBackground(bgColor);
        if (datePicker.getComponentDateTextField() != null) {
            datePicker.getComponentDateTextField().setBackground(bgColor);
        }
        medicineCodeFld.setBackground(bgColor);
        quantityFld.setBackground(bgColor);
        instructionsFld.setBackground(bgColor);
        durationFld.setBackground(bgColor);

        if (!editable && !isNewPrescription) {
            saveItemButton.setToolTipText("Cant save changes on other doctor prescription");
            deleteButton.setToolTipText("Cant delete other doctor prescription");
        } else {
            saveItemButton.setToolTipText("Save prescription");
            deleteButton.setToolTipText("Delete prescription");
        }
    }

    private void refreshItemsTable() {
        try {
            itemsTableModel.setRowCount(0);
            Prescription current = model.getCurrent();

            if (current != null && current.getItems() != null) {
                for (PrescriptionItem item : current.getItems()) {
                    Medicine medicine = controller.getMedicineByCode(item.getMedicineCode());
                    String medicineName = medicine != null
                            ? medicine.getName() + " (" + medicine.getPresentation() + ")"
                            : item.getMedicineCode();

                    itemsTableModel.addRow(new Object[]{
                            item.getMedicineCode(),
                            medicineName,
                            item.getQuantity(),
                            item.getInstructions(),
                            item.getDurationDays()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setController(ControllerPrescription controller) {
        this.controller = controller;
    }

    public void setModel(ModelPrescription model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelPrescription.CURRENT -> {
                Object newValue = evt.getNewValue();
                if (newValue instanceof Prescription current) {
                    SwingUtilities.invokeLater(() -> displayPrescription(current));
                }
            }
            case ModelPrescription.DOCTOR -> {
                Object newValue = evt.getNewValue();
                if (newValue instanceof Doctor doctor) {
                    SwingUtilities.invokeLater(() -> setDoctor(doctor));
                }
            }
        }
    }
}

