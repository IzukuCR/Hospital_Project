package presentation;

import logic.Patient;

@FunctionalInterface
public interface PatientSelectionListener {
    void onPatientSelected(Patient patient);
}