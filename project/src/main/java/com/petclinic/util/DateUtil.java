package com.petclinic.util;

import javafx.scene.control.TableCell;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
    
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMATTER.format(date);
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return DATETIME_FORMATTER.format(dateTime);
    }
    
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return TIME_FORMATTER.format(dateTime);
    }
    
    public static class DateTableCell<T> extends TableCell<T, LocalDate> {
        @Override
        protected void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            
            if (empty || date == null) {
                setText(null);
            } else {
                setText(formatDate(date));
            }
        }
    }
    
    public static class DateTimeTableCell<T> extends TableCell<T, LocalDateTime> {
        @Override
        protected void updateItem(LocalDateTime dateTime, boolean empty) {
            super.updateItem(dateTime, empty);
            
            if (empty || dateTime == null) {
                setText(null);
            } else {
                setText(formatDateTime(dateTime));
            }
        }
    }
}