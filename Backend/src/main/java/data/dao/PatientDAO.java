package data.dao;

import logic.Patient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    private final Database db = Database.instance();

    public PatientDAO() {}

    private Patient fromRS(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getString("id"));
        p.setName(rs.getString("name"));
        p.setPassword(rs.getString("password"));
        p.setBirthDate(rs.getString("birthDate"));     // alias
        p.setPhoneNumber(rs.getString("phoneNumber")); // alias
        return p;
    }

    public Patient findById(String id) throws SQLException {
        String sql = "SELECT id, name, password, " +
                "       birth_date AS birthDate, phone_number AS phoneNumber " +
                "FROM patients WHERE id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, id);
            try (ResultSet rs = db.executeQuery(st)) {
                return (rs != null && rs.next()) ? fromRS(rs) : null;
            }
        }
    }

    public List<Patient> findAll() throws SQLException {
        String sql = "SELECT id, name, password, " +
                "       birth_date AS birthDate, phone_number AS phoneNumber " +
                "FROM patients";
        List<Patient> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql);
             ResultSet rs = db.executeQuery(st)) {
            while (rs != null && rs.next()) out.add(fromRS(rs));
        }
        return out;
    }

    public void create(Patient p) throws SQLException {
        String sql = "INSERT INTO patients (id, name, password, birth_date, phone_number) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, p.getId());
            st.setString(2, p.getName());
            st.setString(3, p.getPassword());
            st.setString(4, p.getBirthDate());
            st.setString(5, p.getPhoneNumber());
            db.executeUpdate(st);
        }
    }

    public List<Patient> searchByName(String name) throws SQLException {
        String sql = "SELECT id, name, password, " +
                "       birth_date AS birthDate, phone_number AS phoneNumber " +
                "FROM patients WHERE name LIKE CONCAT('%', ?, '%')";
        List<Patient> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, name);
            try (ResultSet rs = db.executeQuery(st)) {
                while (rs != null && rs.next()) out.add(fromRS(rs));
            }
        }
        return out;
    }

    public int update(Patient p) throws SQLException {
        String sql = "UPDATE patients SET name = ?, password = ?, birth_date = ?, phone_number = ? " +
                "WHERE id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, p.getName());
            st.setString(2, p.getPassword());
            st.setString(3, p.getBirthDate());
            st.setString(4, p.getPhoneNumber());
            st.setString(5, p.getId());
            return db.executeUpdate(st);
        }
    }

    public int deleteById(String id) throws SQLException {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, id);
            return db.executeUpdate(st);
        }
    }
}
