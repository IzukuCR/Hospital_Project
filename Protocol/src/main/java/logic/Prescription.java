package main.java.logic;

import java.util.Date;
import java.util.List;


public class Prescription {
    private String id;
    private String patientId;
    private Date creationDate;
    private Date withdrawalDate;
    private String status;
    private List<PrescriptionItem> items;
    private String doctorId;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public Date getWithdrawalDate() { return withdrawalDate; }
    public void setWithdrawalDate(Date withdrawalDate) { this.withdrawalDate = withdrawalDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<PrescriptionItem> getItems() { return items; }
    public void setItems(List<PrescriptionItem> items) { this.items = items; }
}
