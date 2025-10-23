package main.java.presentation.table_models;


import prescription_dispatch.logic.Prescription;

import java.util.List;

public class TableModelPrescriptions extends AbstractTableModel<Prescription> implements javax.swing.table.TableModel {

    public TableModelPrescriptions(int[] cols, List<Prescription> rows) {
        super(cols, rows);
    }

    public static final int PatientID = 0;
    public static final int State = 1;
    public static final int CreationDate = 2;
    public static final int PickupDate =3;

    @Override
    protected void initColNames() {
        colNames = new String[4];
        colNames[PatientID] = "Patient ID";
        colNames[State] = "Sate";
        colNames[CreationDate] = "Creation Date";
        colNames[PickupDate] = "Pickup Date";
    }
    protected Object getPropetyAt(Prescription prescription, int col) {
        switch (cols[col]) {
            case PatientID:
                return prescription.getPatientId();
            case State:
                return prescription.getStatus();
            case CreationDate:
                return prescription.getCreationDate();
            case PickupDate:
                return prescription.getWithdrawalDate();
            default:
                return "";
        }
    }


}
