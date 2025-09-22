package com.blockchain.healthcare;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class Transaction {
    // Fields that will be stored in Firestore
    private String hospitalId;
    private String doctorId;
    private String patientId;
    private String insuranceId;
    private String recordId;
    private String recordType;
    private String operation;
    private String prescription;
    private double amount;
    private long timestamp;

    /**
     * No-argument constructor REQUIRED for Firestore deserialization.
     */
    public Transaction() {}

    // --- Getters and Setters (Crucial for Firestore) ---

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getInsuranceId() { return insuranceId; }
    public void setInsuranceId(String insuranceId) { this.insuranceId = insuranceId; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // --- Core Logic ---

    /**
     * Calculates a unique SHA-256 hash for the transaction's content.
     * This is used as a leaf in the Merkle Tree.
     */
    public String getHashValue() {
        String dataToHash = hospitalId + doctorId + patientId + insuranceId + recordId +
                recordType + operation + prescription + amount + timestamp;
        return Hashing.sha256()
                .hashString(dataToHash, StandardCharsets.UTF_8)
                .toString();
    }
}

