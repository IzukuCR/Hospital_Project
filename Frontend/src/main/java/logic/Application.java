package logic;

import com.formdev.flatlaf.FlatDarculaLaf;
import presentation.InfoWindow;
import presentation.dashboard.ControllerDashboard;
import presentation.dashboard.ModelDashboard;
import presentation.dashboard.ViewDashboard;
import presentation.dispensing.ControllerDispensing;
import presentation.dispensing.ModelDispensing;
import presentation.dispensing.ViewDispensing;
import presentation.doctors.ControllerDoctor;
import presentation.doctors.ModelDoctor;
import presentation.doctors.ViewDoctor;
import presentation.history.ControllerHistory;
import presentation.history.ModelHistory;
import presentation.history.ViewHistory;
import presentation.login.ControllerLog;
import presentation.login.ModelLog;
import presentation.login.ViewLog;
import presentation.medicines.ControllerMedicine;
import presentation.medicines.ModelMedicine;
import presentation.medicines.ViewMedicine;
import presentation.patients.ControllerPatient;
import presentation.patients.ModelPatient;
import presentation.patients.ViewPatient;
import presentation.pharmacists.*;
import presentation.pharmacists.ControllerPharmacist;
import presentation.pharmacists.ViewPharmacist;
import presentation.prescriptions.ControllerPrescription;
import presentation.prescriptions.ModelPrescription;
import presentation.prescriptions.ViewPrescription;

import javax.swing.*;
import java.awt.*;

public class Application {
    private static JFrame currentWindow;

    // Instances for views and controllers
    private static ViewDoctor doctorView;
    private static ViewPharmacist pharmacistView;
    private static ViewPatient patientView;
    private static ViewMedicine medicineView;
    private static ViewPrescription prescriptionView;
    private static ViewDispensing dispensingView;
    private static ViewHistory historyView;
    private static ViewDashboard dashboardView;
    private static InfoWindow infoWindow;

    private static ControllerDoctor doctorController;
    private static ControllerPharmacist pharmacistController;
    private static ControllerPatient patientController;
    private static ControllerMedicine medicineController;
    private static ControllerPrescription prescriptionController;
    private static ControllerDispensing dispensingController;
    private static ControllerHistory historyController;
    private static ControllerDashboard dashboardController;
    private static ControllerMessaging messagingController;

    public static final String USER_TYPE_ADMIN = "ADMIN";
    public static final String USER_TYPE_DOCTOR = "DOCTOR";
    public static final String USER_TYPE_PHARMACIST = "PHARMACIST";

    // Fixed credentials for administrator
    public static final String ADMIN_USERNAME = "admin";
    public static String ADMIN_PASSWORD = "1";

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(/*"javax.swing.plaf.nimbus.NimbusLookAndFeel"*/ new FlatDarculaLaf());
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Initialize components
        initializeComponents();

        showLoginWindow();
    }

    private static void initializeComponents() {
        // Initialize models
        ModelDoctor doctorModel = new ModelDoctor();
        ModelPharmacist pharmacistModel = new ModelPharmacist();
        ModelPatient patientModel = new ModelPatient();
        ModelMedicine medicineModel = new ModelMedicine();
        ModelPrescription prescriptionModel = new ModelPrescription();
        ModelDispensing dispensingModel = new ModelDispensing();
        ModelHistory historyModel = new ModelHistory();
        ModelDashboard dashboardModel = new ModelDashboard();


        // Initialize views
        doctorView = new ViewDoctor();
        pharmacistView = new ViewPharmacist();
        patientView = new ViewPatient();
        medicineView = new ViewMedicine();
        prescriptionView = new ViewPrescription();
        dispensingView =new ViewDispensing();
        historyView = new ViewHistory();
        dashboardView = new ViewDashboard();
        infoWindow = new InfoWindow();

        // Initialize controllers
        doctorController = new ControllerDoctor(doctorView, doctorModel);
        pharmacistController = new ControllerPharmacist(pharmacistView, pharmacistModel);
        patientController = new ControllerPatient(patientView, patientModel);
        medicineController = new ControllerMedicine(medicineView, medicineModel);
        prescriptionController = new ControllerPrescription(prescriptionView,prescriptionModel);
        dispensingController =new ControllerDispensing(dispensingView,dispensingModel);
        historyController = new ControllerHistory(historyView, historyModel);
        dashboardController = new ControllerDashboard(dashboardView, dashboardModel);
        messagingController = new ControllerMessaging();


        // Configure views with their controllers and models
        doctorView.setControllerDoc(doctorController);
        doctorView.setModelDoc(doctorModel);

        pharmacistView.setControllerPharm(pharmacistController);
        pharmacistView.setModelPharm(pharmacistModel);

        patientView.setControllerPat(patientController);
        patientView.setModelPat(patientModel);

        medicineView.setControllerMed(medicineController);
        medicineView.setModelMed(medicineModel);

        prescriptionView.setController(prescriptionController);
        prescriptionView.setModel(prescriptionModel);

        historyView.setControllerHistory(historyController);
        historyView.setModelHistory(historyModel);
    }

    private static void showLoginWindow() {
        ViewLog loginView = new ViewLog();
        ModelLog loginModel = new ModelLog();

        ControllerLog loginController = new ControllerLog(loginView, loginModel);

        loginView.setControllerLog(loginController);
        loginView.setModelLog(loginModel);

        loginView.setVisible(true);
    }

    public static void showUserWindow(String userType, String userId) {
        if (currentWindow != null) {
            currentWindow.dispose();
        }

        currentWindow = new JFrame();
        currentWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        currentWindow.setSize(1200, 700);
        currentWindow.setLocationRelativeTo(null);

        // REGISTRAR USUARIO EN MENSAJERÍA
        messagingController.loginUser(userId);

        switch (userType) {
            case USER_TYPE_ADMIN:
                setupAdminWindow(userId);
                break;
            case USER_TYPE_DOCTOR:
                setupDoctorWindow(userId);
                break;
            case USER_TYPE_PHARMACIST:
                setupPharmacistWindow(userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown user type: " + userType);
        }

        currentWindow.setVisible(true);
    }

    private static void setupAdminWindow(String userId) {
        currentWindow.setTitle("Administrator - " + userId);

        // INICIALIZAR MENSAJERÍA PARA VISTAS
        doctorView.initializeMessaging(userId);
        pharmacistView.initializeMessaging(userId);
        patientView.initializeMessaging(userId);
        medicineView.initializeMessaging(userId);
        historyView.initializeMessaging(userId);
        dashboardView.initializeMessaging(userId);

        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);

        JButton adminChangeUserBtn = new JButton();
        adminChangeUserBtn.setIcon(new ImageIcon(Application.class.getResource("/icons/log.png")));
        adminChangeUserBtn.setBackground(Color.YELLOW);
        adminChangeUserBtn.setOpaque(true);
        adminChangeUserBtn.setContentAreaFilled(true);
        adminChangeUserBtn.setBorderPainted(false);
        adminChangeUserBtn.setFocusPainted(false);
        adminChangeUserBtn.addActionListener(e -> returnToLogin());
        adminChangeUserBtn.setPreferredSize(new Dimension(30, 30));
        adminChangeUserBtn.setMaximumSize(new Dimension(30, 30));

        toolBar.add(adminChangeUserBtn);
        toolBar.add(Box.createVerticalGlue());

        JTabbedPane tabbedPane = new JTabbedPane();
        Icon doctorTabIcon = new ImageIcon(Application.class.getResource("/icons/doctor.png"));
        tabbedPane.addTab("Doctors", doctorTabIcon, doctorView.getPanel());

        Icon pharmTabIcon = new ImageIcon(Application.class.getResource("/icons/pharmacist.png"));
        tabbedPane.addTab("Pharmacist", pharmTabIcon, pharmacistView.getPanel());

        Icon patientTabIcon = new ImageIcon(Application.class.getResource("/icons/patient.png"));
        tabbedPane.addTab("Patient", patientTabIcon, patientView.getPanel());

        Icon medicineTabIcon = new ImageIcon(Application.class.getResource("/icons/meds1.png"));
        tabbedPane.addTab("Medicines", medicineTabIcon, medicineView.getPanel());

        Icon historyTabIcon = new ImageIcon(Application.class.getResource("/icons/history.png"));
        tabbedPane.addTab("History", historyTabIcon, historyView.getPanel());

        Icon dash1 = new ImageIcon(Application.class.getResource("/icons/data.png"));
        tabbedPane.addTab(" Dashboard", dash1, dashboardView.getPanel());

        Icon infoTabIcon = new ImageIcon(Application.class.getResource("/icons/hospital.png"));
        tabbedPane.addTab("Info", infoTabIcon, infoWindow);

        // PANEL PRINCIPAL CON MENSAJERÍA
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(toolBar, BorderLayout.WEST);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // AGREGAR PANEL DE USUARIOS ACTIVOS
       presentation.messaging.ActiveUsersPanel messagingPanel =
                new presentation.messaging.ActiveUsersPanel(userId);
        mainPanel.add(messagingPanel, BorderLayout.EAST);

        currentWindow.setContentPane(mainPanel);
    }

    private static void setupDoctorWindow(String userId) {
        currentWindow.setTitle("Doctor(Prescriptions) - " + userId);

        // INICIALIZAR MENSAJERÍA
        doctorView.initializeMessaging(userId);

        JTabbedPane tabbedPane = new JTabbedPane();
        Icon prescriptionTabIcon = new ImageIcon(Application.class.getResource("/icons/prescrip1.png"));
        tabbedPane.addTab("Prescription", prescriptionTabIcon, prescriptionView.getPanel());

        //Doctor currentDoctor = getData().getCurrentDoctor();
        prescriptionController.setDoctorSearch(userId);


        Icon historyTabIcon = new ImageIcon(Application.class.getResource("/icons/history.png"));
        tabbedPane.addTab("History", historyTabIcon, historyView.getPanel());

        Icon dash2 = new ImageIcon(Application.class.getResource("/icons/data.png"));
        tabbedPane.addTab(" Dashboard", dash2, dashboardView.getPanel());

        Icon infoTabIcon = new ImageIcon(Application.class.getResource("/icons/hospital.png"));
        tabbedPane.addTab("Info", infoTabIcon, infoWindow);

        // PANEL PRINCIPAL CON MENSAJERÍA
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // AGREGAR PANEL DE USUARIOS ACTIVOS (como en ViewDoctor)
        presentation.messaging.ActiveUsersPanel messagingPanel =
                new presentation.messaging.ActiveUsersPanel(userId);
        mainPanel.add(messagingPanel, BorderLayout.EAST);

        currentWindow.setContentPane(mainPanel);
    }

    private static void setupPharmacistWindow(String userId) {
        currentWindow.setTitle("Pharmacist - " + userId);

        dispensingController.setPharmacists(userId);

        // INICIALIZAR MENSAJERÍA
        dispensingView.initializeMessaging(userId);

        JTabbedPane tabbedPane = new JTabbedPane();
        Icon dispe1 = new ImageIcon(Application.class.getResource("/icons/dispend.png"));
        tabbedPane.addTab(" Dispatch", dispe1, dispensingView.getPanel());

        Icon historyTabIcon = new ImageIcon(Application.class.getResource("/icons/history.png"));
        tabbedPane.addTab("History", historyTabIcon, historyView.getPanel());

        Icon dash3 = new ImageIcon(Application.class.getResource("/icons/data.png"));
        tabbedPane.addTab(" Dashboard", dash3, dashboardView.getPanel());

        Icon infoTabIcon = new ImageIcon(Application.class.getResource("/icons/hospital.png"));
        tabbedPane.addTab("Info", infoTabIcon, infoWindow);

        // PANEL PRINCIPAL CON MENSAJERÍA
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // AGREGAR PANEL DE USUARIOS ACTIVOS
        presentation.messaging.ActiveUsersPanel messagingPanel =
                new presentation.messaging.ActiveUsersPanel(userId);
        mainPanel.add(messagingPanel, BorderLayout.EAST);

        currentWindow.setContentPane(mainPanel);
    }

    public static void returnToLogin() {
        if (currentWindow != null) {
            // CLEANUP: Limpiar todas las vistas que puedan tener mensajería activa
            doctorView.cleanup();
            pharmacistView.cleanup();
            patientView.cleanup();
            medicineView.cleanup();
            prescriptionView.cleanup();
            dispensingView.cleanup();
            historyView.cleanup();
            dashboardView.cleanup();

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