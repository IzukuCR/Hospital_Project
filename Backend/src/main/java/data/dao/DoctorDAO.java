package data.dao;

import logic.Doctor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DoctorDAO {
    private final Database db;

    public DoctorDAO() {
        db = Database.instance();
    }

    private Doctor fromRS(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setId(rs.getString("id"));
        d.setName(rs.getString("name"));
        d.setPassword(rs.getString("password"));
        d.setSpecialty(rs.getString("specialty"));
        return d;
    }

    public Doctor findById(String id) throws SQLException {
        String sql = "SELECT id, name, password, specialty FROM doctors WHERE id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, id);
            try (ResultSet rs = db.executeQuery(st)) {
                return (rs != null && rs.next()) ? fromRS(rs) : null;
            }
        }
    }
    public List<Doctor> findAll() throws SQLException {
        String sql = "SELECT id, name, password, specialty FROM doctors";
        List<Doctor> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql);
             ResultSet rs = db.executeQuery(st)) {
            while (rs != null && rs.next()) out.add(fromRS(rs));
        }
        return out;
    }
    public void create(Doctor doctor) throws SQLException{
        PreparedStatement stmt = db.prepareStatement(
                "INSERT INTO doctors (id, name, password, specialty) VALUES (?, ?, ?, ?)"
        );
        stmt.setString(1, doctor.getId());
        stmt.setString(2, doctor.getName());
        stmt.setString(3, doctor.getPassword());
        stmt.setString(4, doctor.getSpecialty());
        db.executeUpdate(stmt);

    }

    public int deleteById(String id) throws SQLException {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, id);
            return db.executeUpdate(st);
        }
    }

    public List<Doctor> searchByName(String name) throws SQLException {
        String sql = "SELECT id, name, password, specialty FROM doctors " +
                "WHERE name LIKE CONCAT('%', ?, '%')";
        List<Doctor> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, name);
            try (ResultSet rs = db.executeQuery(st)) {
                while (rs != null && rs.next()) out.add(fromRS(rs));
            }
        }
        return out;
    }

    public int update(Doctor d) throws SQLException {
        String sql = "UPDATE doctors SET name = ?, password = ?, specialty = ? WHERE id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, d.getName());
            st.setString(2, d.getPassword());
            st.setString(3, d.getSpecialty());
            st.setString(4, d.getId());
            return db.executeUpdate(st);
        }
    }
}