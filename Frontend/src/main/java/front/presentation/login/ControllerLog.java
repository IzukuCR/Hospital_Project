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
        new Thread(() -> {
            try {
                if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    SwingUtilities.invokeLater(() ->
                            viewLog.showMessage("Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE));
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    SwingUtilities.invokeLater(() ->
                            viewLog.showMessage("New passwords don't match", "Error", JOptionPane.ERROR_MESSAGE));
                    return;
                }

                if (newPassword.length() < 3) {
                    SwingUtilities.invokeLater(() ->
                            viewLog.showMessage("Password must be at least 3 characters", "Error", JOptionPane.ERROR_MESSAGE));
                    return;
                }

                String userType = services.log().validateUserType(username, oldPassword);

                if (userType == null && !Application.isAdminCredentials(username, oldPassword)) {
                    SwingUtilities.invokeLater(() ->
                            viewLog.showMessage("Invalid current credentials", "Error", JOptionPane.ERROR_MESSAGE));
                    return;
                }

                boolean success = false;

                if (Application.isAdminCredentials(username, oldPassword)) {
                    success = Application.changeAdminPassword(oldPassword, newPassword);
                    final boolean ok = success;
                    SwingUtilities.invokeLater(() -> {
                        if (ok)
                            viewLog.showMessage("Admin password changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        else
                            viewLog.showMessage("Failed to change admin password", "Error", JOptionPane.ERROR_MESSAGE);
                    });
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
                            SwingUtilities.invokeLater(() ->
                                    viewLog.showMessage("User type not supported for password change", "Error", JOptionPane.ERROR_MESSAGE));
                            return;
                    }
                }

                final boolean ok = success;
                SwingUtilities.invokeLater(() -> {
                    if (ok)
                        viewLog.showMessage("Password changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    else
                        viewLog.showMessage("Failed to change password", "Error", JOptionPane.ERROR_MESSAGE);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        viewLog.showMessage("Error changing password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }, "ChangePassword-Thread").start();
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
        if (username.isEmpty() || password.isEmpty()) {
            viewLog.showError("Complete form");
            return;
        }

        new Thread(() -> {
            try {
                Service service = Service.instance();

                // Paso 1: conectar si no hay sesión activa
                if (service.getSyncSocket() == null || service.getSyncSocket().isClosed()) {
                    try {
                        System.out.println("Connecting before login validation...");
                        service.connect();
                        System.out.println("Connection established before user validation");
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() ->
                                viewLog.showError("Cannot connect to server: " + ex.getMessage()));
                        return;
                    }
                }

                // Paso 2: Validar administrador
                if (Application.isAdminCredentials(username, password)) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(viewLog, "Successful login as Administrator. Accessing System...");
                        try {
                            service.connectAfterLogin(username);
                        } catch (Exception e) {
                            System.err.println("Error linking admin session: " + e.getMessage());
                        }
                        Application.showUserWindow(Application.USER_TYPE_ADMIN, username);
                        viewLog.disposeView();
                    });
                    return;
                }

                // Paso 3: Validar Doctor
                try {
                    Doctor doctor = new Doctor();
                    doctor.setId(username);
                    doctor.setPassword(password);
                    Doctor validatedDoctor = service.doctor().validateLogin(doctor);

                    if (validatedDoctor != null) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(viewLog, "Successful login. Accessing System...");
                            try {
                                service.connectAfterLogin(username);
                            } catch (Exception e) {
                                System.err.println("Error linking doctor session: " + e.getMessage());
                            }
                            Application.showUserWindow(Application.USER_TYPE_DOCTOR, username);
                            viewLog.disposeView();
                        });
                        return;
                    }
                } catch (Exception e) {
                    System.err.println("Doctor validation failed: " + e.getMessage());
                }

                // Paso 4: Validar Pharmacist
                try {
                    Pharmacist pharmacist = new Pharmacist();
                    pharmacist.setId(username);
                    pharmacist.setPassword(password);
                    Pharmacist validatedPharmacist = service.pharmacist().validateLogin(pharmacist);

                    if (validatedPharmacist != null) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(viewLog, "Successful login. Accessing System...");
                            try {
                                service.connectAfterLogin(username);
                            } catch (Exception e) {
                                System.err.println("Error linking pharmacist session: " + e.getMessage());
                            }
                            Application.showUserWindow(Application.USER_TYPE_PHARMACIST, username);
                            viewLog.disposeView();
                        });
                        return;
                    }
                } catch (Exception e) {
                    System.err.println("Pharmacist validation failed: " + e.getMessage());
                }

                // Paso 5: Ninguna validación exitosa
                SwingUtilities.invokeLater(() -> viewLog.showError("Invalid username or password"));

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    viewLog.showError("System error: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }, "Login-Thread").start();
    }

    public void exit() {
        System.exit(0);
    }
}