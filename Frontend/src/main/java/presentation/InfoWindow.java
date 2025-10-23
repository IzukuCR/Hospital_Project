package main.java.presentation;

import javax.swing.*;
import java.awt.*;

public class InfoWindow extends JPanel {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JPanel imagePanel;
    private JLabel hospitalImageLabel;
    private JLabel developersLabel;
    private JLabel courseInfoLabel;

    public InfoWindow() {
        initComponents();
        setupHospitalContent();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.DARK_GRAY);

        titleLabel = new JLabel("Prescription and dispensing of medicines");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(138, 138, 138));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 20, 5, 20));

        developersLabel = new JLabel("Developed by: [Isaac Rodriguez Aguero], [Josué Vargas Gutiérrez]");
        developersLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        developersLabel.setForeground(new Color(82, 172, 65));
        developersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        developersLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        courseInfoLabel = new JLabel("Course: EIF-206 | University: Universidad Nacional de Costa Rica | Semester: ll-2025");
        courseInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        courseInfoLabel.setForeground(new Color(255, 254, 137));
        courseInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        courseInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(developersLabel, BorderLayout.CENTER);
        headerPanel.add(courseInfoLabel, BorderLayout.SOUTH);

        imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.setBackground(new Color(97, 97, 97));

        hospitalImageLabel = new JLabel();
        hospitalImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hospitalImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        hospitalImageLabel.setPreferredSize(new Dimension(600, 400));
        hospitalImageLabel.setOpaque(true);
        hospitalImageLabel.setBackground(new Color(97, 97, 97));

        imagePanel.add(hospitalImageLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(imagePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupHospitalContent() {

        try {
            java.net.URL imageURL = getClass().getResource("/icons/hospital2.png");

            if (imageURL != null) {
                ImageIcon hospitalIcon = new ImageIcon(imageURL);

                Image img = hospitalIcon.getImage();
                Image scaledImg = img.getScaledInstance(600, 400, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImg);

                hospitalImageLabel.setIcon(scaledIcon);
                hospitalImageLabel.setText("");
                System.out.println("Imagen hospital2.png loaded successfully");

            } else {
                System.out.println("Cannot find image hospital2.png");
                createAlternativeContent();
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            createAlternativeContent();
        }

        revalidate();
        repaint();
    }

    private void createAlternativeContent() {
        hospitalImageLabel.setText("<html><div style='text-align: center;'>" +
                "<span style='font-size: 100px; color: #0066cc;'>🏥</span><br><br>" +
                "<span style='font-size: 24px; color: #0066cc;'><b>Hospital Management System</b></span><br>" +
                "<span style='font-size: 18px; color: gray;'>Prescription and Medication Management</span><br><br>" +
                "<span style='font-size: 16px; color: #666;'>Image hospital2.png not found</span>" +
                "</div></html>");
        hospitalImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hospitalImageLabel.setBackground(new Color(97, 97, 97)); // Mantener fondo gris
    }

    public JPanel getPanel() {
        return this;
    }

}