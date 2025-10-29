package front.presentation.search_patients;

import logic.Patient;

@FunctionalInterface
public interface PatientSelectionListener {
    void onPatientSelected(Patient patient);
}