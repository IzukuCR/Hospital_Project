package presentation.search_patients;

import presentation.table_models.TableModelPatient;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View {
    private JPanel panel1;
    private JTextField fieldSearch;
    private JComboBox<String> comboBoxCondition;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton clearDataButton;
    private JTable table1;
    private JScrollPane patientsTable;

    private SearchPatientsController controller;

    public View() {
        if (comboBoxCondition.getItemCount() == 0) {
            comboBoxCondition.setModel(new DefaultComboBoxModel<>(new String[] {"Name", "ID"}));
            comboBoxCondition.setSelectedItem("Name");
        }
        comboBoxCondition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) controller.onConditionChanged(getSearchCondition());
            }
        });
        fieldSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { if (controller != null) controller.onQueryChanged(fieldSearch.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { if (controller != null) controller.onQueryChanged(fieldSearch.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { if (controller != null) controller.onQueryChanged(fieldSearch.getText()); }
            private void onChange() {
                if (controller != null) controller.onQueryChanged(fieldSearch.getText());
            }
        });


        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowSelectionAllowed(true);
        table1.setColumnSelectionAllowed(false);
        table1.setDefaultEditor(Object.class, null);
        table1.setAutoCreateRowSorter(true);

        table1.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting() && controller != null) {
                int vrow = table1.getSelectedRow();
                if (vrow >= 0) {
                    int mrow = table1.convertRowIndexToModel(vrow);
                    controller.onRowSelected(mrow); // pasa índice del modelo
                }
            }
        });

        clearDataButton.addActionListener(e -> {
            // limpiar texto y des-seleccionar tabla
            if (!fieldSearch.getText().isEmpty()) fieldSearch.setText("");
            if (table1.getSelectedRow() >= 0) table1.clearSelection();
            if (controller != null) controller.onClearRequested(); // resetea filtro/lista en controller
            fieldSearch.requestFocusInWindow();
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) controller.onCancelRequested();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) controller.onSaveRequested();
            }
        });
    }

    public void setTableModel(TableModelPatient tm) {
        table1.setModel(tm);
    }

    public JPanel getRoot() {
        return panel1;
    }
    public void setController(SearchPatientsController controller) { this.controller = controller; }

    public JTable getTable() { return table1; }

    public String getSearchCondition() {
        Object sel = comboBoxCondition.getSelectedItem();
        return sel == null ? "Name" : sel.toString();
    }
}