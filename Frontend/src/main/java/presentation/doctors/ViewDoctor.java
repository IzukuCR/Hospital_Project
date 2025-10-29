package presentation.doctors;

import logic.Doctor;
import presentation.Highlighter;
import presentation.messaging.ActiveUsersPanel;
import presentation.table_models.TableModelDoctor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ViewDoctor implements PropertyChangeListener {
    private JTextField searchFld;
    private JTextField idFld;
    private JPanel panel;
    private JTable table1;
    private JTextField nameFld;
    private JPasswordField passwordFld;
    private JTextField specFld;
    private JComboBox<String> searchComboBox;
    private JButton saveButton;
    private JButton searchButton;
    private JButton clearDataButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton listButton;
    private JButton clearTableButton;
    private ActiveUsersPanel activeUsersPanel;
    private String currentDoctorId;


    private TableModelDoctor tableModel;

    private static final String SEARCH_BY_ID = "Search by ID";
    private static final String SEARCH_BY_NAME = "Search by Name";

    public ViewDoctor() {

        initializeSearchComboBox();

        initializeTable();

        idFld.setEditable(true);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Doctor doctor = new Doctor();
                    doctor.setId(idFld.getText());
                    doctor.setName(nameFld.getText());

                    // Asignar password: si está vacío, usar el ID
                    String password = new String(passwordFld.getPassword());
                    if (password.isEmpty()) {
                        password = idFld.getText(); // Usar ID como password por defecto
                    }
                    doctor.setPassword(password);

                    doctor.setSpecialty(specFld.getText());

                    controllerDoctor.create(doctor);
                    JOptionPane.showMessageDialog(panel, "Doctor registered successfully", "SAVE", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadAllDoctors(); // Refresh table
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Doctor doctor = new Doctor();
                    doctor.setId(idFld.getText());
                    doctor.setName(nameFld.getText());

                    String password = new String(passwordFld.getPassword());
                    if (password.isEmpty()) {
                        password = idFld.getText();
                    }
                    doctor.setPassword(password);

                    doctor.setSpecialty(specFld.getText());

                    controllerDoctor.update(doctor);
                    JOptionPane.showMessageDialog(panel, "Doctor updated successfully", "UPDATE", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadAllDoctors(); // Refresh table
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
                        JOptionPane.showMessageDialog(panel, "Please enter a doctor ID to delete", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(panel,
                            "Are you sure you want to delete doctor with ID: " + id + "?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        controllerDoctor.delete(id);
                        JOptionPane.showMessageDialog(panel, "Doctor deleted successfully", "DELETE", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                        loadAllDoctors(); // Refresh table
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
                    loadAllDoctors();
                    //JOptionPane.showMessageDialog(panel, "Doctors loaded successfully", "LIST", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error loading doctors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                Doctor selectedDoctor = tableModel.getRowAt(selectedRow);
                idFld.setText(selectedDoctor.getId());
                nameFld.setText(selectedDoctor.getName());
                passwordFld.setText(selectedDoctor.getPassword());
                specFld.setText(selectedDoctor.getSpecialty());

                idFld.setEditable(false);
            }
        });


        clearDataButton.addActionListener(e -> {
            idFld.setEditable(true);
        });

        // Add highlighter to fields
        Highlighter highlighter = new Highlighter(Color.GREEN);
        searchFld.addMouseListener(highlighter);
        idFld.addMouseListener(highlighter);
        nameFld.addMouseListener(highlighter);
        passwordFld.addMouseListener(highlighter);
        specFld.addMouseListener(highlighter);
    }

    public void initializeMessaging(String doctorId) {
        this.currentDoctorId = doctorId;
        activeUsersPanel = new ActiveUsersPanel(currentDoctorId);
        logic.Service.MessagingService.getInstance().userLoggedIn(currentDoctorId);

        rebuildPanel();
    }
    private void rebuildPanel(){
        Container parent = panel.getParent();
        if (parent != null) {
            JPanel mainContainer = new JPanel(new BorderLayout());
            mainContainer.add(panel, BorderLayout.CENTER);
            mainContainer.add(activeUsersPanel, BorderLayout.EAST);

            parent.removeAll();
            parent.add(mainContainer);
            parent.revalidate();
            parent.repaint();
        }
    }
    public void cleanup(){

        if (activeUsersPanel != null) {
            activeUsersPanel.cleanup();
        }
        if (currentDoctorId != null) {
            logic.Service.MessagingService.getInstance().userLoggedOut(currentDoctorId);
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

            if (SEARCH_BY_ID.equals(searchType)) {

                controllerDoctor.search(searchText, "id");

                Doctor current = modelDoctor.getCurrent();
                if (current != null) {
                    tableModel.setRows(List.of(current));
                }

            } else {

                List<Doctor> foundDoctors = controllerDoctor.getDoctorsByName(searchText);

                if (foundDoctors.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "No doctors found", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRows(new ArrayList<>());
                } else if (foundDoctors.size() == 1) {

                    Doctor doctor = foundDoctors.get(0);
                    idFld.setText(doctor.getId());
                    nameFld.setText(doctor.getName());
                    passwordFld.setText(doctor.getPassword());
                    specFld.setText(doctor.getSpecialty());
                    idFld.setEditable(false);
                    tableModel.setRows(foundDoctors);
                } else {

                    tableModel.setRows(foundDoctors);
                    JOptionPane.showMessageDialog(panel, "Found " + foundDoctors.size() + " doctors, please select one from the list", "INFO", JOptionPane.INFORMATION_MESSAGE);
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
                TableModelDoctor.ID,
                TableModelDoctor.NAME,
                TableModelDoctor.SPECIALTY,
                TableModelDoctor.PASS
        };


        tableModel = new TableModelDoctor(cols, new ArrayList<>());
        table1.setModel(tableModel);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableModel.setRows(new ArrayList<>());
    }

    public void refresh() {
        loadAllDoctors();
    }

    private void loadAllDoctors() {
        try {
            List<Doctor> doctors = controllerDoctor.getAllDoctors();
            tableModel.setRows(doctors);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error loading doctors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        searchFld.setText("");
        idFld.setText("");
        nameFld.setText("");
        passwordFld.setText("");
        specFld.setText("");
        idFld.setEditable(true);
    }

    private void clearTable() {
        tableModel.setRows(new ArrayList<>());
    }

    public JPanel getPanel() {
        return panel;
    }

    ControllerDoctor controllerDoctor;
    ModelDoctor modelDoctor;

    public void setControllerDoc(ControllerDoctor controllerDoctor) {
        this.controllerDoctor = controllerDoctor;
    }

    public void setModelDoc(ModelDoctor modelDoctor) {
        this.modelDoctor = modelDoctor;
        modelDoctor.addPropertyChangeListener(this);

    }



    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ModelDoctor.CURRENT.equals(evt.getPropertyName())) {
            Doctor current = (Doctor) evt.getNewValue();
            SwingUtilities.invokeLater(() -> {
                idFld.setText(current.getId());
                nameFld.setText(current.getName());
                passwordFld.setText(current.getPassword());
                specFld.setText(current.getSpecialty());
                idFld.setEditable(false);
            });
        }
    }
}