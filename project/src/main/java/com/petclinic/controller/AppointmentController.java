package com.petclinic.controller;

import com.petclinic.model.Appointment;
import com.petclinic.model.Pet;
import com.petclinic.service.AppointmentService;
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

public class AppointmentController {

    @FXML
    private TableView<Appointment> appointmentsTable;

    @FXML
    private TableColumn<Appointment, LocalDateTime> dateTimeColumn;

    @FXML
    private TableColumn<Appointment, String> petNameColumn;

    @FXML
    private TableColumn<Appointment, String> ownerNameColumn;

    @FXML
    private TableColumn<Appointment, String> reasonColumn;

    @FXML
    private TableColumn<Appointment, String> statusColumn;

    @FXML
    private TextField searchField;

    @FXML
    private VBox addAppointmentForm;

    @FXML
    private ComboBox<Pet> petComboBox;

    @FXML
    private TextField ownerField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<LocalTime> timeComboBox;

    @FXML
    private TextArea reasonArea;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button saveButton;

    private final AppointmentService appointmentService = new AppointmentService();
    private final PetService petService = new PetService();

    private Appointment currentAppointment;
    private boolean isEditing = false;

    @FXML
    public void initialize() {
        setupAppointmentsTable();
        setupFormControls();
        loadAppointments();
        setupSearch();

        addAppointmentForm.setVisible(false);
        addAppointmentForm.setManaged(false);
    }

    private void setupAppointmentsTable() {
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateTimeColumn.setCellFactory(column -> new DateUtil.DateTimeTableCell<>());

        petNameColumn.setCellValueFactory(new PropertyValueFactory<>("petName"));
        ownerNameColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        appointmentsTable.setRowFactory(tv -> {
            TableRow<Appointment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editAppointment(row.getItem());
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


        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(18, 0);
        while (start.isBefore(end)) {
            timeComboBox.getItems().add(start);
            start = start.plusMinutes(30);
        }

        timeComboBox.setConverter(new StringConverter<LocalTime>() {
            @Override
            public String toString(LocalTime time) {
                return time != null ? time.toString() : "";
            }

            @Override
            public LocalTime fromString(String string) {
                return null;
            }
        });


        ValidationUtil.addRequiredValidator(petComboBox);
        ValidationUtil.addRequiredValidator(datePicker);
        ValidationUtil.addRequiredValidator(timeComboBox);


        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void loadAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        appointmentsTable.setItems(FXCollections.observableArrayList(appointments));
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadAppointments();
            } else {

            }
        });
    }

    @FXML
    private void showAddAppointmentForm() {
        clearForm();
        loadPets();
        showForm(false);
    }

    private void loadPets() {
        List<Pet> pets = petService.getAllPets();
        petComboBox.setItems(FXCollections.observableArrayList(pets));
    }

    @FXML
    private void editAppointment(Appointment appointment) {
        this.currentAppointment = appointment;
        this.isEditing = true;

        loadPets();


        for (Pet pet : petComboBox.getItems()) {
            if (pet.getId() == appointment.getPetId()) {
                petComboBox.setValue(pet);
                break;
            }
        }


        datePicker.setValue(appointment.getDateTime().toLocalDate());
        timeComboBox.setValue(appointment.getDateTime().toLocalTime());

        reasonArea.setText(appointment.getReason());
        notesArea.setText(appointment.getNotes());

        showForm(true);
    }

    private void showForm(boolean isEdit) {
        saveButton.setText(isEdit ? "Update Appointment" : "Schedule Appointment");

        addAppointmentForm.setVisible(true);
        addAppointmentForm.setManaged(true);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), addAppointmentForm);
        slideIn.setFromY(50);
        slideIn.setToY(0);
        slideIn.play();
    }

    @FXML
    private void hideForm() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), addAppointmentForm);
        slideOut.setFromY(0);
        slideOut.setToY(50);
        slideOut.setOnFinished(e -> {
            addAppointmentForm.setVisible(false);
            addAppointmentForm.setManaged(false);
        });
        slideOut.play();
    }

    @FXML
    private void saveAppointment() {
        if (!validateForm()) {
            return;
        }

        try {
            Pet selectedPet = petComboBox.getValue();
            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), timeComboBox.getValue());
            String reason = reasonArea.getText();
            String notes = notesArea.getText();

            if (isEditing && currentAppointment != null) {
                currentAppointment.setPetId(selectedPet.getId());
                currentAppointment.setPetName(selectedPet.getName());
                currentAppointment.setOwnerName(selectedPet.getOwnerName());
                currentAppointment.setDateTime(dateTime);
                currentAppointment.setReason(reason);
                currentAppointment.setNotes(notes);

                appointmentService.updateAppointment(currentAppointment);
                AlertUtil.showInformation("Success", "Appointment updated successfully");
            } else {
                Appointment newAppointment = new Appointment(
                        0,
                        selectedPet.getId(),
                        selectedPet.getName(),
                        selectedPet.getOwnerId(),
                        selectedPet.getOwnerName(),
                        dateTime,
                        reason,
                        "",
                        "",
                        notes,
                        "Scheduled"
                );

                appointmentService.addAppointment(newAppointment);
                AlertUtil.showInformation("Success", "Appointment scheduled successfully");
            }

            loadAppointments();
            hideForm();

        } catch (Exception e) {
            AlertUtil.showError("Error", "Error saving appointment: " + e.getMessage());
        }
    }

    @FXML
    private void deleteAppointment() {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            AlertUtil.showWarning("No Selection", "Please select an appointment to cancel");
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation(
                "Confirm Cancellation",
                "Are you sure you want to cancel this appointment?",
                "This action cannot be undone."
        );

        if (confirmed) {
            try {
                appointmentService.deleteAppointment(selectedAppointment.getId());
                loadAppointments();
                AlertUtil.showInformation("Success", "Appointment cancelled successfully");
            } catch (Exception e) {
                AlertUtil.showError("Error", "Error cancelling appointment: " + e.getMessage());
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

        if (timeComboBox.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select a time");
            return false;
        }

        if (reasonArea.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Please enter a reason for the visit");
            return false;
        }

        return true;
    }

    private void clearForm() {
        petComboBox.setValue(null);
        ownerField.clear();
        datePicker.setValue(null);
        timeComboBox.setValue(null);
        reasonArea.clear();
        notesArea.clear();

        currentAppointment = null;
        isEditing = false;
    }

    @FXML
    private void cancelEdit() {
        hideForm();
    }
}