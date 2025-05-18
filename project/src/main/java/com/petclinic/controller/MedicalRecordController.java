package com.petclinic.controller;

import com.petclinic.model.MedicalRecord;
import com.petclinic.model.Pet;
import com.petclinic.service.MedicalRecordService;
import com.petclinic.service.PetService;
import com.petclinic.util.AlertUtil;
import com.petclinic.util.DateUtil;
import com.petclinic.util.ValidationUtil;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MedicalRecordController {

    @FXML
    private TableView<MedicalRecord> recordsTable;

    @FXML
    private TableColumn<MedicalRecord, LocalDateTime> dateTimeColumn;

    @FXML
    private TableColumn<MedicalRecord, String> petNameColumn;

    @FXML
    private TableColumn<MedicalRecord, String> typeColumn;

    @FXML
    private TableColumn<MedicalRecord, String> descriptionColumn;

    @FXML
    private TableColumn<MedicalRecord, String> treatmentColumn;

    @FXML
    private TextField searchField;

    @FXML
    private VBox addRecordForm;

    @FXML
    private ComboBox<Pet> petComboBox;

    @FXML
    private TextField ownerField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextArea treatmentArea;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button saveButton;

    private final MedicalRecordService medicalRecordService = new MedicalRecordService();
    private final PetService petService = new PetService();

    private MedicalRecord currentRecord;
    private boolean isEditing = false;

    @FXML
    public void initialize() {
        setupRecordsTable();
        setupFormControls();
        loadRecords();
        setupSearch();

        addRecordForm.setVisible(false);
        addRecordForm.setManaged(false);
    }

    private void setupRecordsTable() {
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateTimeColumn.setCellFactory(column -> new DateUtil.DateTimeTableCell<>());

        petNameColumn.setCellValueFactory(new PropertyValueFactory<>("petName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        treatmentColumn.setCellValueFactory(new PropertyValueFactory<>("treatment"));

        recordsTable.setRowFactory(tv -> {
            TableRow<MedicalRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editRecord(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupFormControls() {

        petComboBox.setConverter(new StringConverter<Pet>() {
            @Override
            public String toString(Pet pet) {
                return pet != null ? pet.getName() + " (" + pet.getOwnerName() + ")" : "";
            }

            @Override
            public Pet fromString(String string) {
                return null;
            }
        });

        petComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ownerField.setText(newVal.getOwnerName());
            } else {
                ownerField.clear();
            }
        });

        // Setup type combo box
        typeComboBox.setItems(FXCollections.observableArrayList(
                "Exam",
                "Vaccination",
                "Surgery",
                "Medication",
                "Treatment",
                "Lab Test",
                "X-Ray",
                "Other"
        ));


        ValidationUtil.addRequiredValidator(petComboBox);
        ValidationUtil.addRequiredValidator(datePicker);
        ValidationUtil.addRequiredValidator(typeComboBox);


        datePicker.setValue(LocalDate.now());
    }

    private void loadRecords() {
        List<MedicalRecord> records = medicalRecordService.getAllMedicalRecords();
        recordsTable.setItems(FXCollections.observableArrayList(records));
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadRecords();
            } else {
                List<MedicalRecord> filteredRecords = medicalRecordService.searchMedicalRecords(newValue);
                recordsTable.setItems(FXCollections.observableArrayList(filteredRecords));
            }
        });
    }

    @FXML
    private void showAddRecordForm() {
        clearForm();
        loadPets();
        showForm(false);
    }

    private void loadPets() {
        List<Pet> pets = petService.getAllPets();
        petComboBox.setItems(FXCollections.observableArrayList(pets));
    }

    @FXML
    private void editRecord(MedicalRecord record) {
        this.currentRecord = record;
        this.isEditing = true;

        loadPets();


        for (Pet pet : petComboBox.getItems()) {
            if (pet.getId() == record.getPetId()) {
                petComboBox.setValue(pet);
                break;
            }
        }

        datePicker.setValue(record.getDateTime().toLocalDate());
        typeComboBox.setValue(record.getType());
        descriptionArea.setText(record.getDescription());
        treatmentArea.setText(record.getTreatment());
        notesArea.setText(record.getNotes());

        showForm(true);
    }

    private void showForm(boolean isEdit) {
        saveButton.setText(isEdit ? "Update Record" : "Add Record");

        addRecordForm.setVisible(true);
        addRecordForm.setManaged(true);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), addRecordForm);
        slideIn.setFromY(50);
        slideIn.setToY(0);
        slideIn.play();
    }

    @FXML
    private void hideForm() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), addRecordForm);
        slideOut.setFromY(0);
        slideOut.setToY(50);
        slideOut.setOnFinished(e -> {
            addRecordForm.setVisible(false);
            addRecordForm.setManaged(false);
        });
        slideOut.play();
    }

    @FXML
    private void saveMedicalRecord() {
        if (!validateForm()) {
            return;
        }

        try {
            Pet selectedPet = petComboBox.getValue();
            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.now());
            String type = typeComboBox.getValue();
            String description = descriptionArea.getText();
            String treatment = treatmentArea.getText();
            String notes = notesArea.getText();

            if (isEditing && currentRecord != null) {
                currentRecord.setPetId(selectedPet.getId());
                currentRecord.setPetName(selectedPet.getName());
                currentRecord.setDateTime(dateTime);
                currentRecord.setType(type);
                currentRecord.setDescription(description);
                currentRecord.setTreatment(treatment);
                currentRecord.setNotes(notes);

                medicalRecordService.updateMedicalRecord(currentRecord);
                AlertUtil.showInformation("Success", "Medical record updated successfully");
            } else {
                MedicalRecord newRecord = new MedicalRecord(
                        0,
                        selectedPet.getId(),
                        selectedPet.getName(),
                        0,
                        dateTime,
                        type,
                        description,
                        treatment,
                        notes
                );

                medicalRecordService.addMedicalRecord(newRecord);
                AlertUtil.showInformation("Success", "Medical record added successfully");
            }

            loadRecords();
            hideForm();

        } catch (Exception e) {
            AlertUtil.showError("Error", "Error saving medical record: " + e.getMessage());
        }
    }

    @FXML
    private void deleteMedicalRecord() {
        MedicalRecord selectedRecord = recordsTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            AlertUtil.showWarning("No Selection", "Please select a medical record to delete");
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation(
                "Confirm Delete",
                "Are you sure you want to delete this medical record?",
                "This action cannot be undone."
        );

        if (confirmed) {
            try {
                medicalRecordService.deleteMedicalRecord(selectedRecord.getId());
                loadRecords();
                AlertUtil.showInformation("Success", "Medical record deleted successfully");
            } catch (Exception e) {
                AlertUtil.showError("Error", "Error deleting medical record: " + e.getMessage());
            }
        }
    }

    private boolean validateForm() {
        if (petComboBox.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select a pet");
            return false;
        }

        if (datePicker.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select a date");
            return false;
        }

        if (typeComboBox.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select a record type");
            return false;
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Please enter a description");
            return false;
        }

        return true;
    }

    private void clearForm() {
        petComboBox.setValue(null);
        ownerField.clear();
        datePicker.setValue(LocalDate.now());
        typeComboBox.setValue(null);
        descriptionArea.clear();
        treatmentArea.clear();
        notesArea.clear();

        currentRecord = null;
        isEditing = false;
    }

    @FXML
    private void cancelEdit() {
        hideForm();
    }
}