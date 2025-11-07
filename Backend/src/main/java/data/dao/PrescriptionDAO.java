package data.dao;

import logic.Prescription;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrescriptionDAO {
    private final Database db = Database.instance();

    public PrescriptionDAO() {}

    private Prescription fromRS(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setId(rs.getString("id"));
        p.setPatientId(rs.getString("patientId"));   // alias
        p.setDoctorId(rs.getString("doctorId"));

        Timestamp cd = rs.getTimestamp("creationDate");   // DATETIME -> Timestamp
        if (cd != null) p.setCreationDate(new Date(cd.getTime()));

        Timestamp wd = rs.getTimestamp("withdrawalDate");
        if (wd != null) p.setWithdrawalDate(new Date(wd.getTime()));

        p.setStatus(rs.getString("status"));
        return p;
    }

    public Prescription findById(String id) throws SQLException {
        String sql = "SELECT id, patient_id AS patientId, doctor_id AS doctorId, " +
                "creation_date AS creationDate, withdrawal_date AS withdrawalDate, status " +
                "FROM prescriptions WHERE id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, id);
            try (ResultSet rs = db.executeQuery(st)) {
                return (rs != null && rs.next()) ? fromRS(rs) : null;
            }
        }
    }

    public List<Prescription> findAll() throws SQLException {
        String sql = "SELECT id, patient_id AS patientId, doctor_id AS doctorId, " +
                "creation_date AS creationDate, withdrawal_date AS withdrawalDate, status " +
                "FROM prescriptions";
        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql);
             ResultSet rs = db.executeQuery(st)) {
            while (rs != null && rs.next()) out.add(fromRS(rs));
        }
        return out;
    }

    public List<Prescription> findByPatient(String patientId) throws SQLException {
        String sql = "SELECT id, patient_id AS patientId, doctor_id AS doctorId, " +
                "creation_date AS creationDate, withdrawal_date AS withdrawalDate, status " +
                "FROM prescriptions WHERE patient_id = ?";
        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, patientId);
            try (ResultSet rs = db.executeQuery(st)) {
                while (rs != null && rs.next()) out.add(fromRS(rs));
            }
        }
        return out;
    }

    public void create(Prescription p) throws SQLException {
        String sql = "INSERT INTO prescriptions " +
                "(id, patient_id, doctor_id, creation_date, withdrawal_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            int i = 1;
            st.setString(i++, p.getId());
            st.setString(i++, p.getPatientId());
            st.setString(i++, p.getDoctorId());

            if (p.getCreationDate() != null) {
                st.setTimestamp(i++, new Timestamp(p.getCreationDate().getTime()));
            } else {
                st.setNull(i++, Types.TIMESTAMP);
            }

            if (p.getWithdrawalDate() != null) {
                st.setTimestamp(i++, new Timestamp(p.getWithdrawalDate().getTime()));
            } else {
                st.setNull(i++, Types.TIMESTAMP);
            }

            st.setString(i++, p.getStatus());
            db.executeUpdate(st);
        }
    }

    public int update(Prescription p) throws SQLException {
        String sql = "UPDATE prescriptions SET " +
                "patient_id = ?, doctor_id = ?, creation_date = ?, withdrawal_date = ?, status = ? " +
                "WHERE id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            int i = 1;
            st.setString(i++, p.getPatientId());
            st.setString(i++, p.getDoctorId());

            if (p.getCreationDate() != null) {
                st.setTimestamp(i++, new Timestamp(p.getCreationDate().getTime()));
            } else {
                st.setNull(i++, Types.TIMESTAMP);
            }

            if (p.getWithdrawalDate() != null) {
                st.setTimestamp(i++, new Timestamp(p.getWithdrawalDate().getTime()));
            } else {
                st.setNull(i++, Types.TIMESTAMP);
            }

            st.setString(i++, p.getStatus());
            st.setString(i++, p.getId());
            return db.executeUpdate(st);
        }
    }

    public int deleteById(String id) throws SQLException {
        String sql = "DELETE FROM prescriptions WHERE id = ?";
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, id);
            return db.executeUpdate(st);
        }
    }

    public List<Prescription> findByPatientId(String patientId) throws SQLException {
        String sql = "SELECT id, patient_id AS patientId, doctor_id AS doctorId, " +
                "       creation_date AS creationDate, withdrawal_date AS withdrawalDate, status " +
                "FROM prescriptions WHERE patient_id = ?";
        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, patientId);
            try (ResultSet rs = db.executeQuery(st)) {
                while (rs != null && rs.next()) out.add(fromRS(rs));
            }
        }
        return out;
    }

    public List<Prescription> findByPatientName(String patientName) throws SQLException {
        String sql =
                "SELECT p.id, " +
                        "       p.patient_id     AS patientId, " +
                        "       p.doctor_id      AS doctorId, " +
                        "       p.creation_date  AS creationDate, " +
                        "       p.withdrawal_date AS withdrawalDate, " +
                        "       p.status " +
                        "FROM prescriptions p " +
                        "JOIN patients pa ON pa.id = p.patient_id " +
                        "WHERE pa.name LIKE CONCAT('%', ?, '%')";

        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, patientName);
            try (ResultSet rs = db.executeQuery(st)) {
                while (rs != null && rs.next()) {
                    out.add(fromRS(rs));
                }
            }
        }
        return out;
    }

    public List<Prescription> findByPatientIdAndWithdrawalRange(
            String patientId, Timestamp fromInclusive, Timestamp toExclusive) throws SQLException {

        String sql = "SELECT id, patient_id AS patientId, doctor_id AS doctorId, " +
                "       creation_date AS creationDate, withdrawal_date AS withdrawalDate, status " +
                "FROM prescriptions " +
                "WHERE patient_id = ? " +
                "  AND withdrawal_date >= ? " +
                "  AND withdrawal_date < ?";

        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, patientId);
            st.setTimestamp(2, fromInclusive);
            st.setTimestamp(3, toExclusive);
            try (ResultSet rs = db.executeQuery(st)) {
                while (rs != null && rs.next()) out.add(fromRS(rs));
            }
        }
        return out;
    }

    public List<Prescription> findByDoctorId(String doctorId) throws SQLException {
        String sql = "SELECT id, " +
                "       patient_id     AS patientId, " +
                "       doctor_id      AS doctorId, " +
                "       creation_date  AS creationDate, " +
                "       withdrawal_date AS withdrawalDate, " +
                "       status " +
                "FROM prescriptions " +
                "WHERE doctor_id = ?";

        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(sql)) {
            st.setString(1, doctorId);
            try (ResultSet rs = db.executeQuery(st)) {
                while (rs != null && rs.next()) {
                    out.add(fromRS(rs)); // usa tu helper que mapea Timestamp -> Date
                }
            }
        }
        return out;
    }
}