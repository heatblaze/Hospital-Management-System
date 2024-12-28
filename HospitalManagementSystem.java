import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HospitalManagementSystem extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel patientPanel, doctorPanel, appointmentPanel;

    // Database connection details
    private final String DB_URL = "jdbc:sqlite:hospital.db";

    public HospitalManagementSystem() {
        // Set up the main frame
        setTitle("Hospital Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Patient management tab
        patientPanel = createPatientPanel();
        tabbedPane.addTab("Manage Patients", patientPanel);

        // Doctor management tab
        doctorPanel = createDoctorPanel();
        tabbedPane.addTab("Manage Doctors", doctorPanel);

        // Appointment management tab
        appointmentPanel = createAppointmentPanel();
        tabbedPane.addTab("Manage Appointments", appointmentPanel);

        add(tabbedPane, BorderLayout.CENTER);

        initializeDatabase();

        setVisible(true);
    }

    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table to display patients
        JTable table = new JTable(new DefaultTableModel(new String[]{"ID", "Name", "Age", "Gender"}, 0));
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Form to add a new patient
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField();
        JLabel genderLabel = new JLabel("Gender:");
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        JButton addButton = new JButton("Add Patient");
        JButton refreshButton = new JButton("Refresh");

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(ageLabel);
        formPanel.add(ageField);
        formPanel.add(genderLabel);
        formPanel.add(genderBox);
        formPanel.add(addButton);
        formPanel.add(refreshButton);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        // Add patient action
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            int age;
            try {
                age = Integer.parseInt(ageField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Age must be a valid number!");
                return;
            }
            String gender = genderBox.getSelectedItem().toString();

            addPatient(name, age, gender);
            JOptionPane.showMessageDialog(this, "Patient added successfully!");
        });

        // Refresh action
        refreshButton.addActionListener(e -> {
            populateTable(table, "SELECT * FROM patients");
        });

        return panel;
    }

    private JPanel createDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table to display doctors
        JTable table = new JTable(new DefaultTableModel(new String[]{"ID", "Name", "Specialization"}, 0));
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Form to add a new doctor
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel specializationLabel = new JLabel("Specialization:");
        JTextField specializationField = new JTextField();

        JButton addButton = new JButton("Add Doctor");
        JButton refreshButton = new JButton("Refresh");

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(specializationLabel);
        formPanel.add(specializationField);
        formPanel.add(addButton);
        formPanel.add(refreshButton);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        // Add doctor action
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String specialization = specializationField.getText();

            addDoctor(name, specialization);
            JOptionPane.showMessageDialog(this, "Doctor added successfully!");
        });

        // Refresh action
        refreshButton.addActionListener(e -> {
            populateTable(table, "SELECT * FROM doctors");
        });

        return panel;
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table to display appointments
        JTable table = new JTable(new DefaultTableModel(new String[]{"ID", "Patient Name", "Doctor Name", "Date"}, 0));
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Form to add a new appointment
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel patientLabel = new JLabel("Patient ID:");
        JTextField patientField = new JTextField();
        JLabel doctorLabel = new JLabel("Doctor ID:");
        JTextField doctorField = new JTextField();
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField();

        JButton addButton = new JButton("Add Appointment");
        JButton refreshButton = new JButton("Refresh");

        formPanel.add(patientLabel);
        formPanel.add(patientField);
        formPanel.add(doctorLabel);
        formPanel.add(doctorField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(addButton);
        formPanel.add(refreshButton);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        // Add appointment action
        addButton.addActionListener(e -> {
            int patientId = Integer.parseInt(patientField.getText());
            int doctorId = Integer.parseInt(doctorField.getText());
            String date = dateField.getText();

            addAppointment(patientId, doctorId, date);
            JOptionPane.showMessageDialog(this, "Appointment added successfully!");
        });

        // Refresh action
        refreshButton.addActionListener(e -> {
            populateTable(table, "SELECT * FROM appointments");
        });

        return panel;
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/daw n/hos:hospital.db")) {
            if (conn != null) {
                System.out.println("Connected to the database successfully!");
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS patients (id INTEGER PRIMARY KEY, name TEXT, age INTEGER, gender TEXT)");
                stmt.execute("CREATE TABLE IF NOT EXISTS doctors (id INTEGER PRIMARY KEY, name TEXT, specialization TEXT)");
                stmt.execute("CREATE TABLE IF NOT EXISTS appointments (id INTEGER PRIMARY KEY, patient_id INTEGER, doctor_id INTEGER, date TEXT)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    

    private void addPatient(String name, int age, String gender) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO patients (name, age, gender) VALUES (?, ?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


    private void addDoctor(String name, String specialization) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO doctors (name, specialization) VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, specialization);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addAppointment(int patientId, int doctorId, String date) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO appointments (patient_id, doctor_id, date) VALUES (?, ?, ?)")) {
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, doctorId);
            pstmt.setString(3, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateTable(JTable table, String query) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");  // Explicitly load the SQLite JDBC driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        new HospitalManagementSystem();
    }
}
