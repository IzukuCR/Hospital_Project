package presentation.pharmacists;

import logic.Pharmacist;
import presentation.Highlighter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ViewPharmacist implements PropertyChangeListener {
    private JTextField searchFld;
    private JTextField idFld;
    private JPanel panel;
    private JTable table1;
    private JTextField nameFld;
    private JPasswordField passwordFld;
    private JTextField shiftFld;
    private JComboBox<String> searchComboBox;
    private JButton saveButton;
    private JButton searchButton;
    private JButton clearDataButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton listButton;
    private JButton clearTableButton;
    private String currentUserId;

    private TableModelPharmacist tableModel;

    private static final String SEARCH_BY_ID = "Search by ID";
    private static final String SEARCH_BY_NAME = "Search by Name";

    public ViewPharmacist() {

        initializeSearchComboBox();

        initializeTable();

        idFld.setEditable(true);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Pharmacist pharmacist = new Pharmacist();
                    pharmacist.setId(idFld.getText());
                    pharmacist.setName(nameFld.getText());

                    String password = new String(passwordFld.getPassword());
                    if (password.isEmpty()) {
                        password = idFld.getText();
                    }
                    pharmacist.setPassword(password);

                    pharmacist.setShift(shiftFld.getText());

                    controllerPharmacist.create(pharmacist);
                    JOptionPane.showMessageDialog(panel, "Pharmacist registered successfully", "SAVE", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadAllPharmacists();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Pharmacist pharmacist = new Pharmacist();
                    pharmacist.setId(idFld.getText());
                    pharmacist.setName(nameFld.getText());

                    String password = new String(passwordFld.getPassword());
                    if (password.isEmpty()) {
                        password = idFld.getText();
                    }
                    pharmacist.setPassword(password);

                    pharmacist.setShift(shiftFld.getText());

                    controllerPharmacist.update(pharmacist);
                    JOptionPane.showMessageDialog(panel, "Pharmacist updated successfully", "UPDATE", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadAllPharmacists();
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
                        JOptionPane.showMessageDialog(panel, "Please enter a pharmacist ID to delete", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(panel,
                            "Are you sure you want to delete pharmacist with ID: " + id + "?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        controllerPharmacist.delete(id);
                        JOptionPane.showMessageDialog(panel, "Pharmacist deleted successfully", "DELETE", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                        loadAllPharmacists(); // Refresh table
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
                    loadAllPharmacists();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error loading pharmacists: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                Pharmacist selectedPharmacist = tableModel.getRowAt(selectedRow);
                idFld.setText(selectedPharmacist.getId());
                nameFld.setText(selectedPharmacist.getName());
                passwordFld.setText(selectedPharmacist.getPassword());
                shiftFld.setText(selectedPharmacist.getShift());

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
        passwordFld.addMouseListener(highlighter);
        shiftFld.addMouseListener(highlighter);
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

                controllerPharmacist.search(searchText, "id");

                Pharmacist current = modelPharmacist.getCurrent();
                if (current != null) {
                    idFld.setText(current.getId());
                    nameFld.setText(current.getName());
                    passwordFld.setText(current.getPassword());
                    shiftFld.setText(current.getShift());
                    idFld.setEditable(false);

                    tableModel.setRows(List.of(current));
                } else {
                    JOptionPane.showMessageDialog(panel, "Pharmacist not found", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRows(new ArrayList<>());
                }

            } else {
                List<Pharmacist> foundPharmacists = controllerPharmacist.getPharmacistsByName(searchText);

                if (foundPharmacists.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "No pharmacists found", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRows(new ArrayList<>());
                } else if (foundPharmacists.size() == 1) {
                    Pharmacist pharmacist = foundPharmacists.get(0);
                    idFld.setText(pharmacist.getId());
                    nameFld.setText(pharmacist.getName());
                    passwordFld.setText(pharmacist.getPassword());
                    shiftFld.setText(pharmacist.getShift());
                    idFld.setEditable(false);
                    tableModel.setRows(foundPharmacists);
                } else {
                    tableModel.setRows(foundPharmacists);
                    JOptionPane.showMessageDialog(panel, "Found " + foundPharmacists.size() + " pharmacists, please select one from the list", "INFO", JOptionPane.INFORMATION_MESSAGE);
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
                TableModelPharmacist.ID,
                TableModelPharmacist.NAME,
                TableModelPharmacist.PHARMACY,
                TableModelPharmacist.PASS
        };

        tableModel = new TableModelPharmacist(cols, new ArrayList<>());
        table1.setModel(tableModel);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableModel.setRows(new ArrayList<>());
    }

    public void refresh() {
        loadAllPharmacists();
    }

    private void loadAllPharmacists() {
        try {
            List<Pharmacist> pharmacists = controllerPharmacist.getAllPharmacists();
            tableModel.setRows(pharmacists);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error loading pharmacists: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        searchFld.setText("");
        idFld.setText("");
        nameFld.setText("");
        passwordFld.setText("");
        shiftFld.setText("");
        idFld.setEditable(true);
    }

    private void clearTable() {
        tableModel.setRows(new ArrayList<>());
    }

    public JPanel getPanel() {
        return panel;
    }

    ControllerPharmacist controllerPharmacist;
    ModelPharmacist modelPharmacist;

    public void setControllerPharm(ControllerPharmacist controllerPharmacist) {
        this.controllerPharmacist = controllerPharmacist;
    }

    public void setModelPharm(ModelPharmacist modelPharmacist) {
        this.modelPharmacist = modelPharmacist;
        modelPharmacist.addPropertyChangeListener(this);
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
        if (ModelPharmacist.CURRENT.equals(evt.getPropertyName())) {
            Pharmacist current = (Pharmacist) evt.getNewValue();
            SwingUtilities.invokeLater(() -> {
                idFld.setText(current.getId());
                nameFld.setText(current.getName());
                passwordFld.setText(current.getPassword());
                shiftFld.setText(current.getShift());
                idFld.setEditable(false);
            });
        }
    }
}