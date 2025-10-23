package main.java.presentation.prescriptions;

import prescription_dispatch.Application;
import prescription_dispatch.data.Data;
import prescription_dispatch.logic.Doctor;
import prescription_dispatch.logic.Patient;
import prescription_dispatch.logic.Prescription;
import prescription_dispatch.presentation.table_models.AbstractTableModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class TableModelPrescription extends AbstractTableModel<Prescription> implements javax.swing.table.TableModel {
    private Data data;
    private SimpleDateFormat dateFormat;

    public TableModelPrescription(int[] cols, List<Prescription> rows) {
        super(cols, rows);
        this.data = Application.getData();
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    public static final int ID = 0;
    public static final int PATIENT = 1;
    public static final int CREATION_DATE = 2;
    public static final int WITHDRAWAL_DATE = 3;
    public static final int STATUS = 4;
    public static final int ITEMS_COUNT = 5;
    public static final int DOCTOR = 6;

    @Override
    protected void initColNames() {
        colNames = new String[7];
        colNames[ID] = "ID";
        colNames[PATIENT] = "Patient";
        colNames[CREATION_DATE] = "Creation Date";
        colNames[WITHDRAWAL_DATE] = "Withdrawal Date";
        colNames[STATUS] = "Status";
        colNames[ITEMS_COUNT] = "Medicines Count";
        colNames[DOCTOR] = "Doctor";

    }

    @Override
    protected Object getPropetyAt(Prescription prescription, int col) {
        switch (cols[col]) {
            case ID:
                return prescription.getId();
            case PATIENT:
                Patient patient = data.findPatientById(prescription.getPatientId());
                return patient != null ? patient.getName() + " (" + prescription.getPatientId() + ")" : prescription.getPatientId();
            case CREATION_DATE:
                return prescription.getCreationDate() != null ? dateFormat.format(prescription.getCreationDate()) : "N/A";
            case WITHDRAWAL_DATE:
                if (prescription.getWithdrawalDate() != null) {
                    return dateFormat.format(prescription.getWithdrawalDate());
                } else {
                    return "N/A";
                }
            case STATUS:
                return prescription.getStatus();
            case ITEMS_COUNT:
                return prescription.getItems() != null ? prescription.getItems().size() : 0;
            case DOCTOR:
                Doctor doctor = data.findDoctorById(prescription.getDoctorId());
                return doctor != null ? doctor.getName()  + " (" + prescription.getDoctorId() + ")" : prescription.getDoctorId();
            default: return "";
        }
    }

    public Prescription getRowAt(int row) {
        if (row >= 0 && row < rows.size()) {
            return rows.get(row);
        }
        return null;
    }
}