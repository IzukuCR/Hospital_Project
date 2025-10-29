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
import front.presentation.patients.ControllerPatient;
import front.presentation.patients.ModelPatient;
import front.presentation.patients.ViewPatient;
import front.presentation.pharmacists.ModelPharmacist;
import front.presentation.pharmacists.ControllerPharmacist;
import front.presentation.pharmacists.ViewPharmacist;
import front.presentation.prescriptions.ControllerPrescription;
import front.presentation.prescriptions.ModelPrescription;
import front.presentation.prescriptions.ViewPrescription;

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

    public static final String USER_TYPE_ADMIN = "ADMIN";
    public static final String USER_TYPE_DOCTOR = "DOCTOR";
    public static final String USER_TYPE_PHARMACIST = "PHARMACIST";

    // Fixed credentials for administrator
    public static final String ADMIN_USERNAME = "admin";
    public static String ADMIN_PASSWORD = "1";

    public static void main(String[] args) {
        System.out.println("Starting application...");

        // Set look and feel
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

        // Ensure all Swing creation runs on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Initializing components...");
                initializeComponents();
                System.out.println("Components initialized");

                System.out.println("Creating login window...");
                JFrame frame = new JFrame("Hospital Login");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                ViewLog loginView = new ViewLog();
                ModelLog loginModel = new ModelLog();
                ControllerLog loginController = new ControllerLog(loginView, loginModel);

                loginView.setControllerLog(loginController);
                loginView.setModelLog(loginModel);

                frame.add(loginView.getPanel());
                frame.setSize(400, 300);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                System.out.println("Login window should be visible now");

            } catch (Exception ex) {
                System.out.println("Error in EDT: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
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

        // If ViewLog is a Window (JFrame, JDialog) show it; otherwise assume it provides a panel via getPanel()
        if (loginView instanceof java.awt.Window) {
            java.awt.Window w = (java.awt.Window) loginView;
            w.pack();
            w.setLocationRelativeTo(null);
            w.setVisible(true);
        } else {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            // Add the view's component to the frame (works for Component-returning getPanel())
            frame.add(loginView.getPanel(), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

    public static void showUserWindow(String userType, String userId) {
        // Close current window if exists
        if (currentWindow != null) {
            currentWindow.dispose();
        }


        currentWindow = new JFrame();
        currentWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        currentWindow.setSize(1000, 700);
        currentWindow.setLocationRelativeTo(null);

        switch (userType) {
            case USER_TYPE_ADMIN:
                currentWindow.setTitle("Administrator - " + userId);
                // Crear barra de herramientas VERTICAL
                JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
                toolBar.setFloatable(false);

                JButton adminChangeUserBtn = new JButton();
                adminChangeUserBtn.setIcon(new ImageIcon(Application.class.getResource("/icons/log.png")));
                adminChangeUserBtn.setBackground(Color.YELLOW);
                adminChangeUserBtn.setOpaque(true);
                adminChangeUserBtn.setContentAreaFilled(true);
                adminChangeUserBtn.setBorderPainted(false);
                adminChangeUserBtn.setFocusPainted(false);
                adminChangeUserBtn.setMargin(new Insets(0, 0, 0, 0));
                adminChangeUserBtn.setBorder(null);
                adminChangeUserBtn.addActionListener(e -> returnToLogin());


                // Hacer el botón más ancho para la barra vertical
                adminChangeUserBtn.setPreferredSize(new Dimension(30, 30));
                adminChangeUserBtn.setMaximumSize(new Dimension(30, 30));

                toolBar.add(adminChangeUserBtn);

                // Añadir espacio flexible en la barra vertical
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

                // Crear panel principal con barra lateral izquierda
                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.add(toolBar, BorderLayout.WEST); // Barra en el lado izquierdo
                mainPanel.add(tabbedPane, BorderLayout.CENTER);

                currentWindow.setContentPane(mainPanel);
                break;

            case USER_TYPE_DOCTOR:
                currentWindow.setTitle("Doctor(Prescriptions) - " + userId);
                currentWindow.setSize(1024, 700);
                JTabbedPane tabbedPane1 = new JTabbedPane();
                Icon prescriptionTabIcon = new ImageIcon(Application.class.getResource("/icons/prescrip1.png"));
                tabbedPane1.addTab("Prescription", prescriptionTabIcon, prescriptionView.getPanel());
                prescriptionController.setDoctorSearch(userId);

                Icon historyTabIcon1 = new ImageIcon(Application.class.getResource("/icons/history.png"));
                tabbedPane1.addTab("History", historyTabIcon1, historyView.getPanel());

                Icon dash2 = new ImageIcon(Application.class.getResource("/icons/data.png"));
                tabbedPane1.addTab(" Dashboard", dash2, dashboardView.getPanel());

                Icon infoTabIcon1 = new ImageIcon(Application.class.getResource("/icons/hospital.png"));
                tabbedPane1.addTab("Info", infoTabIcon1, infoWindow);



                currentWindow.setContentPane(tabbedPane1);


                break;

            case USER_TYPE_PHARMACIST:

                JTabbedPane tabbedPane2 = new JTabbedPane();

                currentWindow.setTitle("Pharmacist - " + userId);
                dispensingController.setPharmacists(userId);

                Icon dispe1 = new ImageIcon(Application.class.getResource("/icons/dispend.png"));
                tabbedPane2.addTab(" Dispatch", dispe1, dispensingView.getPanel());

                Icon historyTabIcon2 = new ImageIcon(Application.class.getResource("/icons/history.png"));
                tabbedPane2.addTab("History", historyTabIcon2, historyView.getPanel());

                Icon dash3= new ImageIcon(Application.class.getResource("/icons/data.png"));
                tabbedPane2.addTab(" Dashboard", dash3, dashboardView.getPanel());

                Icon infoTabIcon2 = new ImageIcon(Application.class.getResource("/icons/hospital.png"));
                tabbedPane2.addTab("Info", infoTabIcon2, infoWindow);

                currentWindow.setContentPane(tabbedPane2);
                break;

            default:
                JOptionPane.showMessageDialog(null, "Invalid user");
                showLoginWindow();
                return;
        }

        if (currentWindow != null) {
            // Ensure proper sizing and centering before showing
            currentWindow.pack();
            currentWindow.setLocationRelativeTo(null);
            currentWindow.setVisible(true);
        }
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