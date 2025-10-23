package main.java.data.dao;

import prescription_dispatch.logic.PrescriptionItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionItemDAO {
    private final Database db = Database.instance();

    public PrescriptionItemDAO() {
    }

    private PrescriptionItem fromRS(ResultSet rs) throws SQLException {
        PrescriptionItem it = new PrescriptionItem();
        it.setPrescriptionId(rs.getString("prescriptionId")); // alias
        it.setMedicineCode(rs.getString("medicineCode"));     // alias
        it.setQuantity(rs.getInt("quantity"));
        it.setInstructions(rs.getString("instructions"));
        it.setDurationDays(rs.getInt("durationDays"));        // alias
        return it;
    }

    public List<PrescriptionItem> findByPrescriptionId(String prescriptionId) throws SQLException {
        String sql = "SELECT prescription_id AS prescriptionId, " +
                "       medicine_code   AS medicineCode, " +
                "       quantity, instructions, " +
                "       duration_days   AS durationDays " +
                "FROM prescription_items " +
                "WHERE prescription_id = ?";

        List<PrescriptionItem> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, prescriptionId);
            try (ResultSet rs = db.executeQuery(st)) {
                while (rs != null && rs.next()) {
                    out.add(fromRS(rs));
                }
            }
        }
        return out;
    }

    public void create(PrescriptionItem it) throws SQLException {
        String sql = "INSERT INTO prescription_items " +
                "(prescription_id, medicine_code, quantity, instructions, duration_days) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, it.getPrescriptionId());
            st.setString(2, it.getMedicineCode());
            st.setInt(3, it.getQuantity());
            st.setString(4, it.getInstructions());
            st.setInt(5, it.getDurationDays());
            db.executeUpdate(st);
        }
    }

    public int update(PrescriptionItem it) throws SQLException {
        String sql = "UPDATE prescription_items " +
                "SET quantity = ?, instructions = ?, duration_days = ? " +
                "WHERE prescription_id = ? AND medicine_code = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setInt(1, it.getQuantity());
            st.setString(2, it.getInstructions());
            st.setInt(3, it.getDurationDays());
            st.setString(4, it.getPrescriptionId());
            st.setString(5, it.getMedicineCode());
            return db.executeUpdate(st);
        }
    }

    public int deleteById(String prescriptionId) throws SQLException {
        String sql = "DELETE FROM prescription_items WHERE prescription_id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, prescriptionId);
            return db.executeUpdate(st);
        }
    }

    public int deleteAllForPrescription(String prescriptionId) throws SQLException {
        String sql = "DELETE FROM prescription_items WHERE prescription_id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, prescriptionId);
            return db.executeUpdate(st);
        }
    }


}