package presentation.dispensing;

import com.github.lgooddatepicker.components.DatePicker;
import logic.Patient;
import logic.Prescription;
import logic.PrescriptionItem;
import presentation.table_models.TableModelItems;
import presentation.table_models.TableModelPrescriptions;
import logic.Application;

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

public class ViewDispensing extends JPanel implements PropertyChangeListener, ListSelectionListener  {
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
    private String currentUserId;

    private ControllerDispensing controller;
    private ModelDispensing model;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        Object nv = evt.getNewValue();

        if (ModelDispensing.LIST.equals(prop)) {
            // El model usa la misma key para varias listas; distingue por tipo
            if (nv instanceof List<?> lst) {
                if (lst.isEmpty() || lst.get(0) instanceof Prescription) {
                    @SuppressWarnings("unchecked")
                    List<Prescription> pres = (List<Prescription>) lst;
                    updatePrescriptionsTable(pres);
                }
                // Si en futuro muestras pacientes aquí, puedes manejar instanceof Patient
            }
        } else if (ModelDispensing.CURRENT.equals(prop)) {
            if (nv instanceof Patient p) {
                setPatient(p);
            } else if (nv instanceof Prescription pr) {
                // refrescar tabla de items y campos de la receta seleccionada
                List<PrescriptionItem> items =
                        (pr.getItems() != null) ? pr.getItems() : List.of();
                medicinesTable.setModel(new TableModelItems(items));

                String st = (pr.getStatus() != null) ? pr.getStatus() : "";
                textFieldStatePrescriptionSelected.setText(st);

                String doc = (pr.getDoctorId() != null) ? pr.getDoctorId() : "";
                textFieldDoctorPrescription.setText(doc);
            }
        }
    }

    public ViewDispensing()  {

        prescriptionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prescriptionsTable.getSelectionModel().addListSelectionListener(this);


        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.onSearchPatientRequested();
                }
            }
        });

        dateFilter.addDateChangeListener(event -> {
            LocalDate selectedDate = event.getNewDate();
            if (selectedDate != null) {
                List<Prescription> filteredPrescriptions = controller.getPrescriptionsByDateRange(selectedDate);
                model.setPrescriptionsList(filteredPrescriptions);
                //updatePrescriptionsTable(filteredPrescriptions);
            }
        });

        dateFilter.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearEvent();
            }
        });
        preparingRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (preparingRadioButton.isSelected()) {
                    prescriptionsTable.setEnabled(false);
                    controller.setCurrentPrescriptionStatus("Preparing");

                }
            }
        });
        readyRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (readyRadioButton.isSelected()) {
                    prescriptionsTable.setEnabled(false);
                    controller.setCurrentPrescriptionStatus("Ready");

                }
            }
        });
        pickedRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pickedRadioButton.isSelected()) {
                    prescriptionsTable.setEnabled(false);
                    controller.setCurrentPrescriptionStatus("Picked");

                }
            }
        });
        medicinesTable.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (controller != null && controller.getCurrentPrescription() != null) {
                    List<PrescriptionItem> medicines = controller.getCurrentPrescription().getItems();
                    medicinesTable.setModel(new TableModelItems(medicines));
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null && controller.getCurrentPrescription() != null) {
                    prescriptionsTable.setEnabled(true);
                    //Prescription current = controller.getCurrentPrescription();
                    //current.setStatus(textFieldStatePrescriptionSelected.getText());
                    controller.savePrescription();
                    JOptionPane.showMessageDialog(panel1, "Prescription saved successfully.");
                    clearEvent();
                }
            }
        });
        changeUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.returnToLogin();
            }
        });
    }
    public void clearEvent(){
        textFieldPatientName.setText("");
        textFieldPatientName.setEditable(true);
        textFieldPatientName.setBackground(Color.WHITE);

        textFieldStatePrescriptionSelected.setText("");
        textFieldStatePrescriptionSelected.setEditable(true);
        textFieldStatePrescriptionSelected.setBackground(Color.WHITE);

        textFieldDoctorPrescription.setText("");
        prescriptionsTable.clearSelection();
        medicinesTable.clearSelection();

        preparingRadioButton.setSelected(false);
        readyRadioButton.setSelected(false);
        pickedRadioButton.setSelected(false);

        readyRadioButton.setEnabled(true);
        pickedRadioButton.setEnabled(true);
        preparingRadioButton.setEnabled(true);

        prescriptionsTable.setEnabled(true);

        updatePrescriptionsTable(List.of());
        medicinesTable.setModel(new TableModelItems(List.of())); // Limpiar tabla de Items

        ButtonGroup group = new ButtonGroup();
        group.add(preparingRadioButton);
        group.add(readyRadioButton);
        group.add(pickedRadioButton);
        group.clearSelection(); // Quitar selección de los radio buttons si están agrupados

        if (controller != null) {
            controller.setCurrentPrescription(null);
        }
    }
    public void setPatient(Patient p) {
         textFieldPatientName.setText(p != null ? p.getName() : "");
         if(p!=null){
             textFieldPatientName.setEditable(false);
             textFieldPatientName.setBackground(Color.LIGHT_GRAY);
         }

    }
     public void setCurrentPrescription(Prescription p){
        textFieldStatePrescriptionSelected.setText(p != null ? p.getStatus() : "None");
         if(p!=null){
             textFieldStatePrescriptionSelected.setEditable(false);
             textFieldStatePrescriptionSelected.setBackground(Color.LIGHT_GRAY);
             textFieldDoctorPrescription.setText(String.valueOf(p.getDoctorId()));
             textFieldDoctorPrescription.setEditable(false);
             switch (p.getStatus()) {
                 case "Issued" -> {
                     preparingRadioButton.setEnabled(true);
                     readyRadioButton.setEnabled(false);
                     pickedRadioButton.setEnabled(false);
                 }
                 case "Preparing" -> {
                     preparingRadioButton.setEnabled(false);
                     readyRadioButton.setEnabled(true);
                     pickedRadioButton.setEnabled(false);
                 }
                 case "Ready" -> {
                     preparingRadioButton.setEnabled(false);
                     readyRadioButton.setEnabled(false);
                     pickedRadioButton.setEnabled(true);
                 }
             }

         }
     }

    public void setController(ControllerDispensing controller) {
        this.controller = controller;
    }

    public JPanel getPanel() {
        return panel1;
    }

    public void updatePrescriptionsTable(List<Prescription> prescriptions) {
        TableModelPrescriptions tableModel = new TableModelPrescriptions(
                new int[]{TableModelPrescriptions.PatientID, TableModelPrescriptions.State, TableModelPrescriptions.CreationDate, TableModelPrescriptions.PickupDate},
                prescriptions
        );
        prescriptionsTable.setModel(tableModel);
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
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {  // This ensures we only handle the final selection event
            int selectedRow = prescriptionsTable.getSelectedRow();
            if (selectedRow >= 0) {  // Check if a row is actually selected
                TableModelPrescriptions model = (TableModelPrescriptions) prescriptionsTable.getModel();
                Prescription selectedPrescription = model.getRowAt(selectedRow);
                controller.setCurrentPrescription(selectedPrescription);
            }
            List<PrescriptionItem> items = controller.getCurrentPrescription().getItems();
            medicinesTable.setModel(new TableModelItems(items));
        }
    }

    public void setModel(ModelDispensing model) {
        this.model = model;
        this.model.addPropertyChangeListener(this);

        // pintar estado inicial
        updatePrescriptionsTable(model.getPrescriptionsList());
        setPatient(model.getCurrentPatient());
        Prescription cp = model.getCurrentPrescription();
        List<logic.PrescriptionItem> items =
                (cp != null && cp.getItems() != null) ? cp.getItems() : List.of();
        medicinesTable.setModel(new TableModelItems(items));
    }

}
