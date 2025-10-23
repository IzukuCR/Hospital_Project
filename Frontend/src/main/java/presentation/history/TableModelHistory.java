package main.java.presentation.history;

import prescription_dispatch.Application;
import prescription_dispatch.data.Data;
import prescription_dispatch.logic.*;
import prescription_dispatch.presentation.table_models.AbstractTableModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class TableModelHistory extends AbstractTableModel<Prescription> implements javax.swing.table.TableModel {
    private Data data;
    private SimpleDateFormat dateFormat;

    public TableModelHistory(int[] cols, List<Prescription> rows) {
        super(cols, rows);
        this.data = Application.getData();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public static final int ID = 0;
    public static final int PATIENT_ID = 1;
    public static final int PATIENT_NAME = 2;
    public static final int CREATION_DATE = 3;
    public static final int WITHDRAWAL_DATE = 4;
    public static final int STATUS = 5;
    public static final int DOCTOR_ID = 6;
    public static final int DOCTOR_NAME = 7;
    public static final int ITEMS_COUNT = 8;
    public static final int MEDICINES_DETAILS = 9;
    public static final int INSTRUCTIONS = 10;

    @Override
    protected void initColNames() {
        colNames = new String[11];
        colNames[ID] = "Prescription ID";
        colNames[PATIENT_ID] = "Patient ID";
        colNames[PATIENT_NAME] = "Patient Name";
        colNames[CREATION_DATE] = "Creation Date";
        colNames[WITHDRAWAL_DATE] = "Withdrawal Date";
        colNames[STATUS] = "Status";
        colNames[DOCTOR_ID] = "Doctor ID";
        colNames[DOCTOR_NAME] = "Doctor Name";
        colNames[ITEMS_COUNT] = "Items Count";
        colNames[MEDICINES_DETAILS] = "Medicines Details";
        colNames[INSTRUCTIONS] = "General Instructions";
    }

    @Override
    protected Object getPropetyAt(Prescription prescription, int col) {
        switch (cols[col]) {
            case ID:
                return prescription.getId();
            case PATIENT_ID:
                return prescription.getPatientId();
            case PATIENT_NAME:
                Patient patient = data.findPatientById(prescription.getPatientId());
                return patient != null ? patient.getName() : "Unknown";
            case CREATION_DATE:
                return prescription.getCreationDate() != null ? dateFormat.format(prescription.getCreationDate()) : "N/A";
            case WITHDRAWAL_DATE:
                return prescription.getWithdrawalDate() != null ? dateFormat.format(prescription.getWithdrawalDate()) : "N/A";
            case STATUS:
                return prescription.getStatus();
            case DOCTOR_ID:
                return prescription.getDoctorId();
            case DOCTOR_NAME:
                Doctor doctor = data.findDoctorById(prescription.getDoctorId());
                return doctor != null ? doctor.getName() : "Unknown";
            case ITEMS_COUNT:
                return prescription.getItems() != null ? prescription.getItems().size() : 0;
            case MEDICINES_DETAILS:
                return getMedicinesDetails(prescription);
            case INSTRUCTIONS:
                return getGeneralInstructions(prescription);
            default:
                return "";
        }
    }

    private String getMedicinesDetails(Prescription prescription) {
        if (prescription.getItems() == null || prescription.getItems().isEmpty()) {
            return "No medicines";
        }

        StringBuilder details = new StringBuilder();
        for (PrescriptionItem item : prescription.getItems()) {
            Medicine medicine = data.findMedicineByCode(item.getMedicineCode());
            String medicineName = medicine != null ? medicine.getName() : item.getMedicineCode();
            details.append(medicineName)
                    .append(" (Qty: ")
                    .append(item.getQuantity())
                    .append(", Duration: ")
                    .append(item.getDurationDays())
                    .append(" days); ");
        }
        return details.toString();
    }

    private String getGeneralInstructions(Prescription prescription) {
        if (prescription.getItems() == null || prescription.getItems().isEmpty()) {
            return "No instructions";
        }

        StringBuilder instructions = new StringBuilder();
        for (PrescriptionItem item : prescription.getItems()) {
            if (item.getInstructions() != null && !item.getInstructions().trim().isEmpty()) {
                Medicine medicine = data.findMedicineByCode(item.getMedicineCode());
                String medicineName = medicine != null ? medicine.getName() : item.getMedicineCode();
                instructions.append(medicineName)
                        .append(": ")
                        .append(item.getInstructions())
                        .append("; ");
            }
        }
        return instructions.toString();
    }

    public Prescription getRowAt(int row) {
        if (row >= 0 && row < rows.size()) {
            return rows.get(row);
        }
        return null;
    }
}