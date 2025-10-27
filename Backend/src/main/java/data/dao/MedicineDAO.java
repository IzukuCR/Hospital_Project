package data.dao;

import logic.Medicine;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {
    private final Database db = Database.instance();

    public MedicineDAO() {}

    private Medicine fromRS(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setCode(rs.getString("code"));
        m.setName(rs.getString("name"));
        m.setPresentation(rs.getString("presentation"));
        return m;
    }

    public Medicine findByCode(String code) throws SQLException {
        String sql = "SELECT code, name, presentation FROM medicines WHERE code = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, code);
            try (ResultSet rs = db.executeQuery(st)) {
                return (rs != null && rs.next()) ? fromRS(rs) : null;
            }
        }
    }

    public List<Medicine> findAll() throws SQLException {
        String sql = "SELECT code, name, presentation FROM medicines";
        List<Medicine> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql);
             ResultSet rs = db.executeQuery(st)) {
            while (rs != null && rs.next()) out.add(fromRS(rs));
        }
        return out;
    }

    public List<Medicine> searchByName(String name) throws SQLException {
        String sql = "SELECT code, name, presentation FROM medicines " +
                "WHERE name LIKE CONCAT('%', ?, '%')";
        List<Medicine> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, name);
            try (ResultSet rs = db.executeQuery(st)) {
                while (rs != null && rs.next()) out.add(fromRS(rs));
            }
        }
        return out;
    }

    public void create(Medicine m) throws SQLException {
        String sql = "INSERT INTO medicines (code, name, presentation) VALUES (?, ?, ?)";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, m.getCode());
            st.setString(2, m.getName());
            st.setString(3, m.getPresentation());
            db.executeUpdate(st);
        }
    }

    public int update(Medicine m) throws SQLException {
        String sql = "UPDATE medicines SET name = ?, presentation = ? WHERE code = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, m.getName());
            st.setString(2, m.getPresentation());
            st.setString(3, m.getCode());
            return db.executeUpdate(st);
        }
    }

    public int deleteByCode(String code) throws SQLException {
        String sql = "DELETE FROM medicines WHERE code = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, code);
            return db.executeUpdate(st);
        }
    }
}