package com.petclinic.util;

import javafx.scene.layout.BorderPane;

public class NavigationUtil {
    
    private static BorderPane mainBorderPane;
    
    public static void setMainBorderPane(BorderPane borderPane) {
        mainBorderPane = borderPane;
    }
    
    public static void loadDashboard() {
        try {
            if (mainBorderPane != null) {
                mainBorderPane.fireEvent(new NavigationEvent(NavigationEvent.DASHBOARD));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadPets() {
        try {
            if (mainBorderPane != null) {
                mainBorderPane.fireEvent(new NavigationEvent(NavigationEvent.PETS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadOwners() {
        try {
            if (mainBorderPane != null) {
                mainBorderPane.fireEvent(new NavigationEvent(NavigationEvent.OWNERS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadAppointments() {
        try {
            if (mainBorderPane != null) {
                mainBorderPane.fireEvent(new NavigationEvent(NavigationEvent.APPOINTMENTS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadMedicalRecords() {
        try {
            if (mainBorderPane != null) {
                mainBorderPane.fireEvent(new NavigationEvent(NavigationEvent.MEDICAL_RECORDS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadReports() {
        try {
            if (mainBorderPane != null) {
                mainBorderPane.fireEvent(new NavigationEvent(NavigationEvent.REPORTS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadNewAppointment() {
        try {
            if (mainBorderPane != null) {
                mainBorderPane.fireEvent(new NavigationEvent(NavigationEvent.NEW_APPOINTMENT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadAppointmentDetails(int appointmentId) {
        try {
            if (mainBorderPane != null) {
                mainBorderPane.fireEvent(new NavigationEvent(NavigationEvent.APPOINTMENT_DETAILS, appointmentId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static class NavigationEvent extends javafx.event.Event {
        public static final javafx.event.EventType<NavigationEvent> DASHBOARD = 
                new javafx.event.EventType<>(javafx.event.Event.ANY, "DASHBOARD");
        public static final javafx.event.EventType<NavigationEvent> PETS = 
                new javafx.event.EventType<>(javafx.event.Event.ANY, "PETS");
        public static final javafx.event.EventType<NavigationEvent> OWNERS = 
                new javafx.event.EventType<>(javafx.event.Event.ANY, "OWNERS");
        public static final javafx.event.EventType<NavigationEvent> APPOINTMENTS = 
                new javafx.event.EventType<>(javafx.event.Event.ANY, "APPOINTMENTS");
        public static final javafx.event.EventType<NavigationEvent> MEDICAL_RECORDS = 
                new javafx.event.EventType<>(javafx.event.Event.ANY, "MEDICAL_RECORDS");
        public static final javafx.event.EventType<NavigationEvent> REPORTS = 
                new javafx.event.EventType<>(javafx.event.Event.ANY, "REPORTS");
        public static final javafx.event.EventType<NavigationEvent> NEW_APPOINTMENT = 
                new javafx.event.EventType<>(javafx.event.Event.ANY, "NEW_APPOINTMENT");
        public static final javafx.event.EventType<NavigationEvent> APPOINTMENT_DETAILS = 
                new javafx.event.EventType<>(javafx.event.Event.ANY, "APPOINTMENT_DETAILS");
        
        private int entityId = -1;
        
        public NavigationEvent(javafx.event.EventType<NavigationEvent> eventType) {
            super(eventType);
        }
        
        public NavigationEvent(javafx.event.EventType<NavigationEvent> eventType, int entityId) {
            super(eventType);
            this.entityId = entityId;
        }
        
        public int getEntityId() {
            return entityId;
        }
    }
}