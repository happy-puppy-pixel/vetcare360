package com.petclinic.util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    public static void addRequiredValidator(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                textField.setStyle("-fx-border-color: red;");
            } else {
                textField.setStyle("");
            }
        });
    }
    
    public static void addRequiredValidator(ComboBox<?> comboBox) {
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                comboBox.setStyle("-fx-border-color: red;");
            } else {
                comboBox.setStyle("");
            }
        });
    }
    
    public static void addRequiredValidator(DatePicker datePicker) {
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                datePicker.setStyle("-fx-border-color: red;");
            } else {
                datePicker.setStyle("");
            }
        });
    }
    
    public static void addEmailValidator(TextField emailField) {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty() && !isValidEmail(newValue)) {
                emailField.setStyle("-fx-border-color: red;");
            } else {
                emailField.setStyle("");
            }
        });
        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (emailField.getText() != null && !emailField.getText().isEmpty() && !isValidEmail(emailField.getText())) {
                    emailField.setStyle("-fx-border-color: red;");
                }
            }
        });
    }
    
    public static boolean isValidEmail(String email) {
        return email == null || email.isEmpty() || EMAIL_PATTERN.matcher(email).matches();
    }
}