package main.java.presentation.login;

import prescription_dispatch.presentation.Highlighter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ViewLog extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel panel;
    private JButton validateButton;
    private JButton changePasswordButton;
    private JButton clearButton; // Nuevo botón Clear

    private ControllerLog controllerLog;
    private ModelLog modelLog;

    public ViewLog() {
        Highlighter highlighter = new Highlighter(Color.GREEN);
        usernameField.addMouseListener(highlighter);
        passwordField.addMouseListener(highlighter);

        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Login Prescription System");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        add(panel);


        validateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controllerLog != null) {
                    controllerLog.login(
                            usernameField.getText(),
                            new String(passwordField.getPassword())
                    );
                }
            }
        });

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controllerLog != null) {
                    controllerLog.showChangePasswordDialog();
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    validateButton.doClick();
                }
            }
        });
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }

    public void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        JTextField usernameField = new JTextField();
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Old Password:"));
        panel.add(oldPasswordField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (controllerLog != null) {
                controllerLog.changePassword(username, oldPassword, newPassword, confirmPassword);
            }
        }
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Authentication error", JOptionPane.ERROR_MESSAGE);
    }

    public void setUsernameField(String username) {
        usernameField.setText(username);
    }

    public void setPasswordField(String password) {
        passwordField.setText(password);
    }

    public void disposeView() {
        dispose();
    }

    public Component getPanel() {
        return panel;
    }

    public void setControllerLog(ControllerLog controllerLog) {
        this.controllerLog = controllerLog;
    }

    public void setModelLog(ModelLog modelLog) {
        if (this.modelLog != null) {
            this.modelLog.removePropertyChangeListener(propertyChangeListener);
        }

        this.modelLog = modelLog;

        if (modelLog != null) {
            modelLog.addPropertyChangeListener(propertyChangeListener);
            usernameField.setText(modelLog.getUsername());
            passwordField.setText(modelLog.getPassword());
        }
    }

    private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (ModelLog.USERNAME.equals(evt.getPropertyName())) {
                usernameField.setText((String) evt.getNewValue());
            }
            if (ModelLog.PASSWORD.equals(evt.getPropertyName())) {
                passwordField.setText((String) evt.getNewValue());
            }
        }
    };
}
