package data.dao;

import logic.Pharmacist;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.List;


public class PharmacistDAO {
    private Database db;

    public PharmacistDAO() {
        db = Database.instance();
    }

    private Pharmacist fromResultSet(ResultSet rs) throws SQLException {
        Pharmacist ph = new Pharmacist();
        ph.setId(rs.getString("id"));
        ph.setName(rs.getString("name"));
        ph.setPassword(rs.getString("password"));
        ph.setShift(rs.getString("shift"));
        return ph;
    }

    public Pharmacist findById(String id) throws SQLException {
        String sql = "SELECT id, name, password, shift FROM pharmacists WHERE id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = db.executeQuery(stmt)) {
                if (rs != null && rs.next()) return fromResultSet(rs);
                return null;
            }
        }
    }

    public List<Pharmacist> searchByName(String name) throws SQLException {
        String sql = "SELECT id, name, password, shift FROM pharmacists " +
                "WHERE name LIKE CONCAT('%', ?, '%')";
        List<Pharmacist> list = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = db.executeQuery(stmt)) {
                while (rs != null && rs.next()) list.add(fromResultSet(rs));
            }
        }
        return list;
    }

    public List<Pharmacist> findAll() throws SQLException {
        List<Pharmacist> list = new ArrayList<>();
        PreparedStatement stmt = db.prepareStatement("SELECT id, name, password, shift FROM pharmacists");
        ResultSet rs = db.executeQuery(stmt);

        while (rs != null && rs.next()) {
            list.add(fromResultSet(rs));
        }
        return list;
    }

    public void create(Pharmacist ph) throws SQLException {
        PreparedStatement stmt = db.prepareStatement(
                "INSERT INTO pharmacists (id, name, password, shift) VALUES (?, ?, ?, ?)"
        );
        stmt.setString(1, ph.getId());
        stmt.setString(2, ph.getName());
        stmt.setString(3, ph.getPassword());
        stmt.setString(4, ph.getShift());
        db.executeUpdate(stmt);
    }
    public int update(Pharmacist ph) throws SQLException {
        PreparedStatement stmt = db.prepareStatement(
                "UPDATE pharmacists SET name = ?, password = ?, shift = ? WHERE id = ?"
        );
        stmt.setString(1, ph.getName());
        stmt.setString(2, ph.getPassword());
        stmt.setString(3, ph.getShift());
        stmt.setString(4, ph.getId());
        return db.executeUpdate(stmt);
    }

    public int deleteById(String id) throws SQLException {
        PreparedStatement stmt = db.prepareStatement(
                "DELETE FROM pharmacists WHERE id = ?"
        );
        stmt.setString(1, id);
        return db.executeUpdate(stmt);
    }
}