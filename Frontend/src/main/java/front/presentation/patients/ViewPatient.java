package front.presentation.patients;

import logic.Patient;
import front.presentation.Highlighter;
import front.presentation.table_models.TableModelPatient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ViewPatient implements PropertyChangeListener {
    private JTextField searchFld;
    private JTextField idFld;
    private JPanel panel;
    private JTable table1;
    private JTextField nameFld;
    private JTextField birthDateFld;
    private JTextField phoneFld;
    private JComboBox<String> searchComboBox;
    private JButton saveButton;
    private JButton searchButton;
    private JButton clearDataButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton listButton;
    private JButton clearTableButton;
    private String currentUserId;


    private TableModelPatient tableModel;

    private static final String SEARCH_BY_ID = "Search by ID";
    private static final String SEARCH_BY_NAME = "Search by Name";

    public ViewPatient() {

        initializeSearchComboBox();

        initializeTable();

        idFld.setEditable(true);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Patient patient = new Patient();
                    patient.setId(idFld.getText());
                    patient.setName(nameFld.getText());
                    patient.setBirthDate(birthDateFld.getText());
                    patient.setPhoneNumber(phoneFld.getText());

                    controllerPatient.create(patient);
                    JOptionPane.showMessageDialog(panel, "Patient registered successfully", "SAVE", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadAllPatients(); // Refresh table
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Patient patient = new Patient();
                    patient.setId(idFld.getText());
                    patient.setName(nameFld.getText());
                    patient.setBirthDate(birthDateFld.getText());
                    patient.setPhoneNumber(phoneFld.getText());

                    controllerPatient.update(patient);
                    JOptionPane.showMessageDialog(panel, "Patient updated successfully", "UPDATE", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadAllPatients(); // Refresh table
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String id = idFld.getText();
                    if (id.isEmpty()) {
                        JOptionPane.showMessageDialog(panel, "Please enter a patient ID to delete", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(panel,
                            "Are you sure you want to delete patient with ID: " + id + "?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        controllerPatient.delete(id);
                        JOptionPane.showMessageDialog(panel, "Patient deleted successfully", "DELETE", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                        loadAllPatients(); // Refresh table
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });

        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    loadAllPatients();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error loading patients: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
                JOptionPane.showMessageDialog(panel, "Information cleared successfully", "CLEAR", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        clearTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTable();
                JOptionPane.showMessageDialog(panel, "Table cleared successfully", "CLEAR TABLE", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table1.getSelectedRow() != -1) {
                int selectedRow = table1.getSelectedRow();
                Patient selectedPatient = tableModel.getRowAt(selectedRow);
                idFld.setText(selectedPatient.getId());
                nameFld.setText(selectedPatient.getName());
                birthDateFld.setText(selectedPatient.getBirthDate());
                phoneFld.setText(selectedPatient.getPhoneNumber());


                idFld.setEditable(false);
            }
        });


        clearDataButton.addActionListener(e -> {
            idFld.setEditable(true);
        });


        Highlighter highlighter = new Highlighter(Color.GREEN);
        searchFld.addMouseListener(highlighter);
        idFld.addMouseListener(highlighter);
        nameFld.addMouseListener(highlighter);
        birthDateFld.addMouseListener(highlighter);
        phoneFld.addMouseListener(highlighter);
    }

    private void handleSearch() {
        try {
            String searchText = searchFld.getText().trim();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter search text", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String searchType = (String) searchComboBox.getSelectedItem();

            if (SEARCH_BY_ID.equals(searchType)) {

                controllerPatient.search(searchText, "id");

                Patient current = modelPatient.getCurrent();
                if (current != null) {

                    idFld.setText(current.getId());
                    nameFld.setText(current.getName());
                    birthDateFld.setText(current.getBirthDate());
                    phoneFld.setText(current.getPhoneNumber());
                    idFld.setEditable(false);

                    tableModel.setRows(List.of(current));
                } else {
                    JOptionPane.showMessageDialog(panel, "Patient not found", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRows(new ArrayList<>());
                }

            } else {

                List<Patient> foundPatients = controllerPatient.getPatientsByName(searchText);

                if (foundPatients.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "No patients found", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRows(new ArrayList<>());
                } else if (foundPatients.size() == 1) {
                    Patient patient = foundPatients.get(0);
                    idFld.setText(patient.getId());
                    nameFld.setText(patient.getName());
                    birthDateFld.setText(patient.getBirthDate());
                    phoneFld.setText(patient.getPhoneNumber());
                    idFld.setEditable(false);
                    tableModel.setRows(foundPatients);
                } else {

                    tableModel.setRows(foundPatients);
                    JOptionPane.showMessageDialog(panel, "Found " + foundPatients.size() + " patients, please select one from the list", "INFO", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, ex.getMessage(), "INFO", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initializeSearchComboBox() {
        searchComboBox.addItem(SEARCH_BY_ID);
        searchComboBox.addItem(SEARCH_BY_NAME);
        searchComboBox.setSelectedItem(SEARCH_BY_ID);
    }

    private void initializeTable() {

        int[] cols = {
                TableModelPatient.ID,
                TableModelPatient.NAME,
                TableModelPatient.BIRTHDATE,
                TableModelPatient.PHONE
        };

        tableModel = new TableModelPatient(cols, new ArrayList<>());
        table1.setModel(tableModel);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableModel.setRows(new ArrayList<>());
    }

    public void refresh() {
        loadAllPatients();
    }

    private void loadAllPatients() {
        try {
            List<Patient> patients = controllerPatient.getAllPatients();
            tableModel.setRows(patients);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error loading patients: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        searchFld.setText("");
        idFld.setText("");
        nameFld.setText("");
        birthDateFld.setText("");
        phoneFld.setText("");
        idFld.setEditable(true);
    }

    private void clearTable() {
        tableModel.setRows(new ArrayList<>());
    }

    public JPanel getPanel() {
        return panel;
    }

    ControllerPatient controllerPatient;
    ModelPatient modelPatient;

    public void setControllerPat(ControllerPatient controllerPatient) {
        this.controllerPatient = controllerPatient;
    }

    public void setModelPat(ModelPatient modelPatient) {
        this.modelPatient = modelPatient;
        modelPatient.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ModelPatient.CURRENT.equals(evt.getPropertyName())) {
            Patient current = (Patient) evt.getNewValue();
            SwingUtilities.invokeLater(() -> {
                idFld.setText(current.getId());
                nameFld.setText(current.getName());
                birthDateFld.setText(current.getBirthDate());
                phoneFld.setText(current.getPhoneNumber());
                idFld.setEditable(false);
            });
        }
    }


}