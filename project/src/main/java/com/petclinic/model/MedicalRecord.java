package com.petclinic.model;

import java.time.LocalDateTime;

public class MedicalRecord {
    private int id;
    private int petId;
    private String petName;
    private int appointmentId;
    private LocalDateTime dateTime;
    private String type;
    private String description;
    private String treatment;
    private String notes;

    public MedicalRecord(int id, int petId, String petName, int appointmentId, LocalDateTime dateTime,
                         String type, String description, String treatment, String notes) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.appointmentId = appointmentId;
        this.dateTime = dateTime;
        this.type = type;
        this.description = description;
        this.treatment = treatment;
        this.notes = notes;
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

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return type + " - " + dateTime.toString();
    }
}