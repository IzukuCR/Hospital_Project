package main.java.presentation.table_models;


import prescription_dispatch.logic.Doctor;

import java.util.List;

public class TableModelDoctor extends AbstractTableModel<Doctor> implements javax.swing.table.TableModel {
    public TableModelDoctor(int[] cols, List<Doctor> rows) {
        super(cols, rows);
    }

    public static final int ID = 0;
    public static final int NAME = 1;
    public static final int SPECIALTY = 2;
    public static final int PASS = 3;

    @Override
    protected void initColNames() {
        colNames = new String[4];
        colNames[ID] = "ID";
        colNames[NAME] = "Name";
        colNames[SPECIALTY] = "Specialty";
        colNames[PASS] = "Password";
    }

    @Override
    protected Object getPropetyAt(Doctor doctor, int col) {
        switch (cols[col]) {
            case ID:
                return doctor.getId();
            case NAME:
                return doctor.getName();
            case SPECIALTY:
                return doctor.getSpecialty();
            case PASS:
                return "•••"; //Ocultar
            default:
                return "";
        }
    }

    public Doctor getRowAt(int row) {
        if (row >= 0 && row < rows.size()) {
            return rows.get(row);
        }
        return null;
    }
}
