package front.presentation.login;

import front.Application;
import logic.Doctor;
import logic.Pharmacist;
import front.logic.Service;

import javax.swing.*;

public class ControllerLog {
    ViewLog viewLog;
    ModelLog modelLog;
    Service services;

    public ControllerLog(ViewLog viewLog, ModelLog modelLog) {
        this.viewLog = viewLog;
        this.modelLog = modelLog;
        this.services = Service.instance();
        viewLog.setControllerLog(this);
        viewLog.setModelLog(modelLog);
    }

    public void showChangePasswordDialog() {
        viewLog.showChangePasswordDialog();
    }

    public void changePassword(String username, String oldPassword, String newPassword, String confirmPassword) {
        try {
            if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                viewLog.showMessage("Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                viewLog.showMessage("New passwords don't match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPassword.length() < 3) {
                viewLog.showMessage("Password must be at least 3 characters", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String userType = services.log().validateUserType(username, oldPassword);

            if (userType == null && !Application.isAdminCredentials(username, oldPassword)) {
                viewLog.showMessage("Invalid current credentials", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = false;

            if (Application.isAdminCredentials(username, oldPassword)) {
                success = Application.changeAdminPassword(oldPassword, newPassword);
                if (success) {
                    viewLog.showMessage("Admin password changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    viewLog.showMessage("Failed to change admin password", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return;
            } else {
                switch (userType) {
                    case Application.USER_TYPE_DOCTOR:
                        success = changeDoctorPassword(username, oldPassword, newPassword);
                        break;
                    case Application.USER_TYPE_PHARMACIST:
                        success = changePharmacistPassword(username, oldPassword, newPassword);
                        break;
                    default:
                        viewLog.showMessage("User type not supported for password change", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }
            }

            if (success) {
                viewLog.showMessage("Password changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Guardado exitoso en base de datos (ya se hizo en changeDoctorPassword/changePharmacistPassword)
            } else {
                viewLog.showMessage("Failed to change password", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            viewLog.showMessage("Error changing password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean changeDoctorPassword(String username, String oldPassword, String newPassword) {
        Doctor doctor = findDoctorByCredentials(username, oldPassword);
        if (doctor != null) {
            doctor.setPassword(newPassword);
            try {
                services.doctor().update(doctor);
                return true;
            } catch (Exception e) {
                System.err.println("Error updating doctor password: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    private boolean changePharmacistPassword(String username, String oldPassword, String newPassword) {
        Pharmacist pharmacist = findPharmacistByCredentials(username, oldPassword);
        if (pharmacist != null) {
            pharmacist.setPassword(newPassword);
            try {
                services.pharmacist().update(pharmacist);
                return true;
            } catch (Exception e) {
                System.err.println("Error updating pharmacist password: " + e.getMessage());
                return false;
            }
        }
        return false;
    }


    private Doctor findDoctorByCredentials(String username, String password) {
        try{
            for (Doctor doctor : services.doctor().getDoctors()) {
                if (doctor.getId().equals(username) && doctor.getPassword().equals(password)) {
                    return doctor;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Pharmacist findPharmacistByCredentials(String username, String password) {
        try{
            for (Pharmacist pharmacist : services.pharmacist().getPharmacists()) {
                if (pharmacist.getId().equals(username) && pharmacist.getPassword().equals(password)) {
                    return pharmacist;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void login(String username, String password) {
        try {
            if (username.isEmpty() || password.isEmpty()) {
                viewLog.showError("Complete form");
                return;
            }

            if (Application.isAdminCredentials(username, password)) {
                JOptionPane.showMessageDialog(viewLog, "Successful login as Administrator. Accessing System...");
                Application.showUserWindow(Application.USER_TYPE_ADMIN, username);
                viewLog.disposeView();
                return;
            }

            String userType = services.log().validateUserType(username, password);

            if (userType != null) {
                if (Application.USER_TYPE_DOCTOR.equals(userType)) {
                    Doctor doctor = findDoctorByCredentials(username, password);
                    if (doctor != null) {
                        //data.setCurrentDoctor(doctor);
                        System.out.println("Doctor logged in: " + doctor.getName());
                    }
                }

                JOptionPane.showMessageDialog(viewLog, "Successful login. Accessing System...");
                Application.showUserWindow(userType, username);
                viewLog.disposeView();
            } else {
                viewLog.showError("Invalid username or password");
            }

        } catch (Exception e) {
            viewLog.showError("System error: " + e.getMessage());
        }
    }

    public void exit() {
        System.exit(0);
    }
}