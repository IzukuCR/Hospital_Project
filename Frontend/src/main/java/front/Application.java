package front;

import com.formdev.flatlaf.FlatDarculaLaf;
import front.presentation.InfoWindow;
import front.presentation.dashboard.ControllerDashboard;
import front.presentation.dashboard.ModelDashboard;
import front.presentation.dashboard.ViewDashboard;
import front.presentation.dispensing.ControllerDispensing;
import front.presentation.dispensing.ModelDispensing;
import front.presentation.dispensing.ViewDispensing;
import front.presentation.doctors.ControllerDoctor;
import front.presentation.doctors.ModelDoctor;
import front.presentation.doctors.ViewDoctor;
import front.presentation.history.ControllerHistory;
import front.presentation.history.ModelHistory;
import front.presentation.history.ViewHistory;
import front.presentation.login.ControllerLog;
import front.presentation.login.ModelLog;
import front.presentation.login.ViewLog;
import front.presentation.medicines.ControllerMedicine;
import front.presentation.medicines.ModelMedicine;
import front.presentation.medicines.ViewMedicine;
import front.presentation.messages.ControllerMessages;
import front.presentation.messages.ModelMessages;
import front.presentation.messages.ViewMessages;
import front.presentation.patients.ControllerPatient;
import front.presentation.patients.ModelPatient;
import front.presentation.patients.ViewPatient;
import front.presentation.pharmacists.ModelPharmacist;
import front.presentation.pharmacists.ControllerPharmacist;
import front.presentation.pharmacists.ViewPharmacist;
import front.presentation.prescriptions.ControllerPrescription;
import front.presentation.prescriptions.ModelPrescription;
import front.presentation.prescriptions.ViewPrescription;

import front.logic.Service;

import javax.swing.*;
import java.awt.*;

public class Application {
    private static JFrame currentWindow;

    // Views
    private static ViewDoctor doctorView;
    private static ViewPharmacist pharmacistView;
    private static ViewPatient patientView;
    private static ViewMedicine medicineView;
    private static ViewPrescription prescriptionView;
    private static ViewDispensing dispensingView;
    private static ViewHistory historyView;
    private static ViewDashboard dashboardView;
    private static InfoWindow infoWindow;
    private static ViewMessages messagesView;

    // Controllers
    private static ControllerDoctor doctorController;
    private static ControllerPharmacist pharmacistController;
    private static ControllerPatient patientController;
    private static ControllerMedicine medicineController;
    private static ControllerPrescription prescriptionController;
    private static ControllerDispensing dispensingController;
    private static ControllerHistory historyController;
    private static ControllerDashboard dashboardController;
    private static ControllerMessages messagesController;

    public static final String USER_TYPE_ADMIN = "ADMIN";
    public static final String USER_TYPE_DOCTOR = "DOCTOR";
    public static final String USER_TYPE_PHARMACIST = "PHARMACIST";

    public static final String ADMIN_USERNAME = "admin";
    public static String ADMIN_PASSWORD = "1";

    public static void main(String[] args) {
        System.out.println("Starting application...");

        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
            System.out.println("Look and feel set successfully");
        } catch (Exception ex) {
            System.out.println("Error setting look and feel: " + ex.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("🔌 Connecting to server...");
            Service.instance().connect();
            System.out.println("Service connected successfully");
        } catch (Exception e) {
            System.err.println(" Failed to connect to server: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to server.\nPlease ensure the server is running.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Inicializar componentes
                initializeComponents();
                System.out.println("Components initialized successfully");

                // Crear ventana de login
                showLoginWindow();

            } catch (Exception ex) {
                System.err.println("Error in EDT: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error initializing application: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void initializeComponents() {
        // Crear modelos
        ModelDoctor doctorModel = new ModelDoctor();
        ModelPharmacist pharmacistModel = new ModelPharmacist();
        ModelPatient patientModel = new ModelPatient();
        ModelMedicine medicineModel = new ModelMedicine();
        ModelPrescription prescriptionModel = new ModelPrescription();
        ModelDispensing dispensingModel = new ModelDispensing();
        ModelHistory historyModel = new ModelHistory();
        ModelDashboard dashboardModel = new ModelDashboard();
        ModelMessages modelMessagesModel = new ModelMessages();

        // Crear vistas
        doctorView = new ViewDoctor();
        pharmacistView = new ViewPharmacist();
        patientView = new ViewPatient();
        medicineView = new ViewMedicine();
        prescriptionView = new ViewPrescription();
        dispensingView = new ViewDispensing();
        historyView = new ViewHistory();
        dashboardView = new ViewDashboard();
        infoWindow = new InfoWindow();
        messagesView = new ViewMessages();

        // Crear controladores y vincularlos
        doctorController = new ControllerDoctor(doctorView, doctorModel);
        doctorView.setControllerDoc(doctorController);
        doctorView.setModelDoc(doctorModel);

        pharmacistController = new ControllerPharmacist(pharmacistView, pharmacistModel);
        pharmacistView.setControllerPharm(pharmacistController);
        pharmacistView.setModelPharm(pharmacistModel);

        patientController = new ControllerPatient(patientView, patientModel);
        patientView.setControllerPat(patientController);
        patientView.setModelPat(patientModel);

        medicineController = new ControllerMedicine(medicineView, medicineModel);
        medicineView.setControllerMed(medicineController);
        medicineView.setModelMed(medicineModel);

        prescriptionController = new ControllerPrescription(prescriptionView, prescriptionModel);
        prescriptionView.setController(prescriptionController);
        prescriptionView.setModel(prescriptionModel);

        dispensingController = new ControllerDispensing(dispensingView, dispensingModel);
        dispensingView.setController(dispensingController);
        dispensingView.setModel(dispensingModel);

        historyController = new ControllerHistory(historyView, historyModel);
        historyView.setControllerHistory(historyController);
        historyView.setModelHistory(historyModel);

        dashboardController = new ControllerDashboard(dashboardView, dashboardModel);
        dashboardView.setControllerDashboard(dashboardController);
        dashboardView.setModelDashboard(dashboardModel);

        messagesController = new ControllerMessages(modelMessagesModel, messagesView);
        messagesView.setController(messagesController);
        messagesView.setModel(modelMessagesModel);

        System.out.println("All components initialized successfully");
    }

    private static void showLoginWindow() {
        ViewLog loginView = new ViewLog();
        ModelLog loginModel = new ModelLog();

        ControllerLog loginController = new ControllerLog(loginView, loginModel);
        loginView.setControllerLog(loginController);
        loginView.setModelLog(loginModel);



        JFrame frame = new JFrame("Hospital Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(loginView.getPanel());
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void showUserWindow(String userType, String userId) {
        if (currentWindow != null) currentWindow.dispose();

        currentWindow = new JFrame();
        currentWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        currentWindow.setSize(1000, 700);
        currentWindow.setLocationRelativeTo(null);

        switch (userType) {
            case USER_TYPE_ADMIN -> {
                currentWindow.setTitle("Administrator - " + userId);
                JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
                toolBar.setFloatable(false);

                JButton adminChangeUserBtn = new JButton(new ImageIcon(Application.class.getResource("/icons/log.png")));
                adminChangeUserBtn.setBackground(Color.YELLOW);
                adminChangeUserBtn.setFocusPainted(false);
                adminChangeUserBtn.addActionListener(e -> returnToLogin());
                adminChangeUserBtn.setPreferredSize(new Dimension(30, 30));

                toolBar.add(adminChangeUserBtn);
                toolBar.add(Box.createVerticalGlue());

                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.addTab("Doctors", new ImageIcon(Application.class.getResource("/icons/doctor.png")), doctorView.getPanel());
                tabbedPane.addTab("Pharmacist", new ImageIcon(Application.class.getResource("/icons/pharmacist.png")), pharmacistView.getPanel());
                tabbedPane.addTab("Patient", new ImageIcon(Application.class.getResource("/icons/patient.png")), patientView.getPanel());
                tabbedPane.addTab("Medicines", new ImageIcon(Application.class.getResource("/icons/meds1.png")), medicineView.getPanel());
                tabbedPane.addTab("History", new ImageIcon(Application.class.getResource("/icons/history.png")), historyView.getPanel());
                tabbedPane.addTab("Dashboard", new ImageIcon(Application.class.getResource("/icons/data.png")), dashboardView.getPanel());
                tabbedPane.addTab("Info", new ImageIcon(Application.class.getResource("/icons/hospital.png")), infoWindow);

                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.add(toolBar, BorderLayout.WEST);
                mainPanel.add(tabbedPane, BorderLayout.CENTER);

                currentWindow.setContentPane(mainPanel);
            }

            case USER_TYPE_DOCTOR -> {
                currentWindow.setTitle("Doctor - " + userId);
                JTabbedPane tabbedPane1 = new JTabbedPane();
                tabbedPane1.addTab("Prescription", new ImageIcon(Application.class.getResource("/icons/prescrip1.png")), prescriptionView.getPanel());
                prescriptionController.setDoctorSearch(userId);
                tabbedPane1.addTab("History", new ImageIcon(Application.class.getResource("/icons/history.png")), historyView.getPanel());
                tabbedPane1.addTab("Dashboard", new ImageIcon(Application.class.getResource("/icons/data.png")), dashboardView.getPanel());
                tabbedPane1.addTab("Info", new ImageIcon(Application.class.getResource("/icons/hospital.png")), infoWindow);
                currentWindow.setContentPane(tabbedPane1);
            }

            case USER_TYPE_PHARMACIST -> {
                currentWindow.setTitle("Pharmacist - " + userId);
                JTabbedPane tabbedPane2 = new JTabbedPane();
                tabbedPane2.addTab("Dispatch", new ImageIcon(Application.class.getResource("/icons/dispend.png")), dispensingView.getPanel());
                dispensingController.setPharmacists(userId);
                tabbedPane2.addTab("History", new ImageIcon(Application.class.getResource("/icons/history.png")), historyView.getPanel());
                tabbedPane2.addTab("Dashboard", new ImageIcon(Application.class.getResource("/icons/data.png")), dashboardView.getPanel());
                tabbedPane2.addTab("Info", new ImageIcon(Application.class.getResource("/icons/hospital.png")), infoWindow);
                currentWindow.setContentPane(tabbedPane2);
            }

            default -> {
                JOptionPane.showMessageDialog(null, "Invalid user");
                showLoginWindow();
                return;
            }
        }

        currentWindow.pack();
        currentWindow.setLocationRelativeTo(null);
        currentWindow.setVisible(true);
    }

    public static void returnToLogin() {
        if (currentWindow != null) {
            currentWindow.dispose();
            currentWindow = null;
        }
        showLoginWindow();
    }

    public static boolean isAdminCredentials(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    public static boolean changeAdminPassword(String oldPassword, String newPassword) {
        if (ADMIN_PASSWORD.equals(oldPassword)) {
            ADMIN_PASSWORD = newPassword;
            return true;
        }
        return false;
    }
}
