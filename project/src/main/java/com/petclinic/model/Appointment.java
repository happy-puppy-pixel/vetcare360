package com.petclinic.model;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private int petId;
    private String petName;
    private int ownerId;
    private String ownerName;
    private LocalDateTime dateTime;
    private String reason;
    private String diagnosis;
    private String treatment;
    private String notes;
    private String status;

    public Appointment(int id, int petId, String petName, int ownerId, String ownerName,
                       LocalDateTime dateTime, String reason, String diagnosis,
                       String treatment, String notes, String status) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.dateTime = dateTime;
        this.reason = reason;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.notes = notes;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return petName + " - " + dateTime.toString();
    }
}