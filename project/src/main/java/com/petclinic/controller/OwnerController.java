package com.petclinic.controller;

import com.petclinic.model.Owner;
import com.petclinic.service.OwnerService;
import com.petclinic.util.AlertUtil;
import com.petclinic.util.ValidationUtil;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;

public class OwnerController {
    @FXML
    private TableColumn<Owner, String> nameColumn;

    @FXML
    private TableView<Owner> ownersTable;
    
    @FXML
    private TableColumn<Owner, String> firstNameColumn;
    
    @FXML
    private TableColumn<Owner, String> lastNameColumn;
    
    @FXML
    private TableColumn<Owner, String> phoneColumn;
    
    @FXML
    private TableColumn<Owner, String> emailColumn;
    
    @FXML
    private TableColumn<Owner, String> addressColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private VBox addOwnerForm;
    
    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextArea addressArea;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private final OwnerService ownerService = new OwnerService();
    
    private Owner currentOwner;
    private boolean isEditing = false;

    @FXML
    public void initialize() {
        setupOwnersTable();
        

        loadOwners();
        

        addOwnerForm.setVisible(false);
        addOwnerForm.setManaged(false);
        

        setupSearch();
        

        setupValidation();
    }
    
    private void setupOwnersTable() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        

        ownersTable.setRowFactory(tv -> {
            TableRow<Owner> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editOwner(row.getItem());
                }
            });
            return row;
        });
    }
    
    private void setupValidation() {
        ValidationUtil.addRequiredValidator(firstNameField);
        ValidationUtil.addRequiredValidator(lastNameField);
        ValidationUtil.addRequiredValidator(phoneField);
        ValidationUtil.addEmailValidator(emailField);
    }
    
    private void loadOwners() {
        List<Owner> owners = ownerService.getAllOwners();
        ownersTable.setItems(FXCollections.observableArrayList(owners));
    }
    
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadOwners();
            } else {
                List<Owner> filteredOwners = ownerService.searchOwners(newValue);
                ownersTable.setItems(FXCollections.observableArrayList(filteredOwners));
            }
        });
    }
    
    @FXML
    private void showAddOwnerForm() {

        clearForm();
        

        showForm(false);
    }
    
    @FXML
    private void editOwner(Owner owner) {
        this.currentOwner = owner;
        this.isEditing = true;
        

        firstNameField.setText(owner.getFirstName());
        lastNameField.setText(owner.getLastName());
        phoneField.setText(owner.getPhone());
        emailField.setText(owner.getEmail());
        addressArea.setText(owner.getAddress());
        

        showForm(true);
    }
    
    private void showForm(boolean isEdit) {

        saveButton.setText(isEdit ? "Update Owner" : "Add Owner");
        

        addOwnerForm.setVisible(true);
        addOwnerForm.setManaged(true);
        

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), addOwnerForm);
        slideIn.setFromY(50);
        slideIn.setToY(0);
        slideIn.play();
    }
    
    @FXML
    private void hideForm() {

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), addOwnerForm);
        slideOut.setFromY(0);
        slideOut.setToY(50);
        slideOut.setOnFinished(e -> {
            addOwnerForm.setVisible(false);
            addOwnerForm.setManaged(false);
        });
        slideOut.play();
    }

    @FXML
    private void saveOwner() {
        if (!validateForm()) {
            return;
        }

        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String address = addressArea.getText();

            if (isEditing && currentOwner != null) {

                currentOwner.setFirstName(firstName);
                currentOwner.setLastName(lastName);
                currentOwner.setPhone(phone);
                currentOwner.setEmail(email);
                currentOwner.setAddress(address);

                ownerService.updateOwner(currentOwner);
                AlertUtil.showInformation("Success", "Owner updated successfully");
            } else {
                Owner newOwner = new Owner(
                    0,
                    firstName,
                    lastName,
                    phone,
                    email,
                    address
                );

                ownerService.addOwner(newOwner);
                AlertUtil.showInformation("Success", "Owner added successfully");
            }


            loadOwners();
            hideForm();

        } catch (Exception e) {
            AlertUtil.showError("Error", "Error saving owner: " + e.getMessage());
        }
    }

    @FXML
    private void deleteOwner() {
        Owner selectedOwner = ownersTable.getSelectionModel().getSelectedItem();
        if (selectedOwner == null) {
            AlertUtil.showWarning("No Selection", "Please select an owner to delete");
            return;
        }
        
        boolean confirmed = AlertUtil.showConfirmation(
            "Confirm Delete", 
            "Are you sure you want to delete " + selectedOwner.getFirstName() + " " + selectedOwner.getLastName() + "?",
            "This action cannot be undone and will also delete all associated pets."
        );
        
        if (confirmed) {
            try {
                ownerService.deleteOwner(selectedOwner.getId());
                loadOwners();
                AlertUtil.showInformation("Success", "Owner deleted successfully");
            } catch (Exception e) {
                AlertUtil.showError("Error", "Error deleting owner: " + e.getMessage());
            }
        }
    }
    
    private boolean validateForm() {
        if (firstNameField.getText().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "First name is required");
            return false;
        }
        
        if (lastNameField.getText().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Last name is required");
            return false;
        }
        
        if (phoneField.getText().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Phone number is required");
            return false;
        }
        
        if (!emailField.getText().isEmpty() && !ValidationUtil.isValidEmail(emailField.getText())) {
            AlertUtil.showWarning("Validation Error", "Please enter a valid email address");
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
        addressArea.clear();
        
        currentOwner = null;
        isEditing = false;
    }
    
    @FXML
    private void cancelEdit() {
        hideForm();
    }
}