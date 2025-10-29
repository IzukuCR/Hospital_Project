package front.presentation.medicines;

import logic.Medicine;
import front.presentation.Highlighter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ViewMedicine implements PropertyChangeListener {
    private JTextField searchFld;
    private JTextField codeFld;
    private JPanel panel;
    private JTable table1;
    private JTextField nameFld;
    private JTextField presentationFld;
    private JComboBox<String> searchComboBox;
    private JButton saveButton;
    private JButton searchButton;
    private JButton clearDataButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton listButton;
    private JButton clearTableButton;
    private String currentUserId;

    private TableModelMedicine tableModel;

    private static final String SEARCH_BY_CODE = "Search by Code";
    private static final String SEARCH_BY_NAME = "Search by Name";

    public ViewMedicine() {
        initializeSearchComboBox();
        initializeTable();
        codeFld.setEditable(true);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Medicine medicine = new Medicine();
                    medicine.setCode(codeFld.getText());
                    medicine.setName(nameFld.getText());
                    medicine.setPresentation(presentationFld.getText());

                    controllerMed.create(medicine);
                    JOptionPane.showMessageDialog(panel, "Medicine registered successfully", "SAVE", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadAllMedicines();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Medicine medicine = new Medicine();
                    medicine.setCode(codeFld.getText());
                    medicine.setName(nameFld.getText());
                    medicine.setPresentation(presentationFld.getText());

                    controllerMed.update(medicine);
                    JOptionPane.showMessageDialog(panel, "Medicine updated successfully", "UPDATE", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadAllMedicines();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String code = codeFld.getText();
                    if (code.isEmpty()) {
                        JOptionPane.showMessageDialog(panel, "Please enter a medicine code to delete", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(panel,
                            "Are you sure you want to delete medicine with code: " + code + "?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        controllerMed.delete(code);
                        JOptionPane.showMessageDialog(panel, "Medicine deleted successfully", "DELETE", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                        loadAllMedicines();
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
                    loadAllMedicines();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error loading medicines: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                Medicine selectedMedicine = tableModel.getRowAt(selectedRow);
                codeFld.setText(selectedMedicine.getCode());
                nameFld.setText(selectedMedicine.getName());
                presentationFld.setText(selectedMedicine.getPresentation());
                codeFld.setEditable(false);
            }
        });

        clearDataButton.addActionListener(e -> {
            codeFld.setEditable(true);
        });

        Highlighter highlighter = new Highlighter(Color.GREEN);
        searchFld.addMouseListener(highlighter);
        codeFld.addMouseListener(highlighter);
        nameFld.addMouseListener(highlighter);
        presentationFld.addMouseListener(highlighter);
    }

    private void handleSearch() {
        try {
            String searchText = searchFld.getText().trim();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter search text", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String searchType = (String) searchComboBox.getSelectedItem();

            if (SEARCH_BY_CODE.equals(searchType)) {
                controllerMed.search(searchText, "code");
                Medicine current = modelMed.getCurrent();
                if (current != null) {

                    codeFld.setText(current.getCode());
                    nameFld.setText(current.getName());
                    presentationFld.setText(current.getPresentation());
                    codeFld.setEditable(false);

                    tableModel.setRows(List.of(current));
                } else {
                    JOptionPane.showMessageDialog(panel, "Medicine not found", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRows(new ArrayList<>());
                }
            } else {
                List<Medicine> foundMedicines = controllerMed.getMedicinesByName(searchText);

                if (foundMedicines.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "No medicines found", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRows(new ArrayList<>());
                } else if (foundMedicines.size() == 1) {
                    Medicine medicine = foundMedicines.get(0);
                    codeFld.setText(medicine.getCode());
                    nameFld.setText(medicine.getName());
                    presentationFld.setText(medicine.getPresentation());
                    codeFld.setEditable(false);
                    tableModel.setRows(foundMedicines);
                } else {
                    tableModel.setRows(foundMedicines);
                    JOptionPane.showMessageDialog(panel, "Found " + foundMedicines.size() + " medicines, please select one from the list", "INFO", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, ex.getMessage(), "INFO", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initializeSearchComboBox() {
        searchComboBox.addItem(SEARCH_BY_CODE);
        searchComboBox.addItem(SEARCH_BY_NAME);
        searchComboBox.setSelectedItem(SEARCH_BY_CODE);
    }

    private void initializeTable() {
        int[] cols = {
                TableModelMedicine.CODE,
                TableModelMedicine.NAME,
                TableModelMedicine.PRESENTATION
        };

        tableModel = new TableModelMedicine(cols, new ArrayList<>());
        table1.setModel(tableModel);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel.setRows(new ArrayList<>());
    }

    public void refresh() {
        loadAllMedicines();
    }

    private void loadAllMedicines() {
        try {
            List<Medicine> medicines = controllerMed.getAllMedicines();
            tableModel.setRows(medicines);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error loading medicines: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        searchFld.setText("");
        codeFld.setText("");
        nameFld.setText("");
        presentationFld.setText("");
        codeFld.setEditable(true);
    }

    private void clearTable() {
        tableModel.setRows(new ArrayList<>());
    }

    public JPanel getPanel() {
        return panel;
    }

    ControllerMedicine controllerMed;
    ModelMedicine modelMed;

    public void setControllerMed(ControllerMedicine controllerMed) {
        this.controllerMed = controllerMed;
    }

    public void setModelMed(ModelMedicine modelMed) {
        this.modelMed = modelMed;
        modelMed.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ModelMedicine.CURRENT.equals(evt.getPropertyName())) {
            Medicine current = (Medicine) evt.getNewValue();
            SwingUtilities.invokeLater(() -> {
                codeFld.setText(current.getCode());
                nameFld.setText(current.getName());
                presentationFld.setText(current.getPresentation());
                codeFld.setEditable(false);
            });
        }
    }
}