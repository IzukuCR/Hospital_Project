package main.java.presentation.table_models;

import prescription_dispatch.logic.Patient;

import java.util.Collections;
import java.util.List;

public class TableModelPatient extends AbstractTableModel<Patient> implements javax.swing.table.TableModel {
    public TableModelPatient(int[] cols, List<Patient> rows) {
        super(cols, rows);
    }

    public TableModelPatient(List<Patient> rows) {
        this(new int[]{ ID, NAME, BIRTHDATE, PHONE }, rows);
    }

    public static final int ID = 0;
    public static final int NAME = 1;
    public static final int BIRTHDATE = 2;
    public static final int PHONE = 3;

    @Override
    protected void initColNames() {
        colNames = new String[4];
        colNames[ID] = "ID";
        colNames[NAME] = "Name";
        colNames[BIRTHDATE] = "Birth Date";
        colNames[PHONE] = "Phone Number";
    }

    @Override
    protected Object getPropetyAt(Patient patient, int col) {
        switch (cols[col]) {
            case ID:
                return patient.getId();
            case NAME:
                return patient.getName();
            case BIRTHDATE:
                return patient.getBirthDate();
            case PHONE:
                return patient.getPhoneNumber();
            default:
                return "";
        }
    }

    public Patient getRowAt(int row) {
        if (row >= 0 && row < rows.size()) {
            return rows.get(row);
        }
        return null;
    }

    public void setData(List<Patient> newRows) {
        this.rows = (newRows != null) ? newRows : Collections.emptyList();
        fireTableDataChanged();
    }

}