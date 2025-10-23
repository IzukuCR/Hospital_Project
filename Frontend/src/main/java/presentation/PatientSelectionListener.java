package main.java.presentation;

import prescription_dispatch.logic.Patient;

@FunctionalInterface
public interface PatientSelectionListener {
    void onPatientSelected(Patient patient);
}