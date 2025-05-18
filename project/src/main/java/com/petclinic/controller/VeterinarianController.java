package com.petclinic.controller;

import com.petclinic.model.Veterinarian;
import com.petclinic.service.VeterinarianService;
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

public class VeterinarianController {

    @FXML private TextField searchField;
    @FXML private VBox addVetForm;
    @FXML private TextField firstNameField, lastNameField, phoneField, emailField, scheduleField;
    @FXML private ComboBox<String> specializationComboBox;
    @FXML private CheckBox availableCheckBox;
    @FXML private TextArea notesArea;
    @FXML private Button saveButton;
    @FXML private TableView<Veterinarian> veterinariansTable;
    @FXML private TableColumn<Veterinarian,String> firstNameColumn, lastNameColumn, specializationColumn, phoneColumn, emailColumn, scheduleColumn;
    @FXML private TableColumn<Veterinarian,Boolean> availableColumn;

    private final VeterinarianService service = new VeterinarianService();
    private Veterinarian currentVet;
    private boolean isEditing = false;

    @FXML
    public void initialize() {
        // table setup
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        specializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        scheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        specializationComboBox.setItems(FXCollections.observableArrayList(
                "General Practice","Surgery","Dentistry","Dermatology","Cardiology","Neurology","Oncology","Emergency Care","Other"
        ));
        ValidationUtil.addRequiredValidator(firstNameField);
        ValidationUtil.addRequiredValidator(lastNameField);
        ValidationUtil.addRequiredValidator(specializationComboBox);
        ValidationUtil.addRequiredValidator(phoneField);
        ValidationUtil.addEmailValidator(emailField);

        loadVeterinarians();
        setupSearch();
        addVetForm.setVisible(false);
        addVetForm.setManaged(false);
    }

    private void loadVeterinarians() {
        List<Veterinarian> list = service.getAllVeterinarians();
        veterinariansTable.setItems(FXCollections.observableArrayList(list));
    }

    private void setupSearch() {
        searchField.textProperty().addListener((o,oldV,newV)->{
            if (newV.isEmpty()) loadVeterinarians();
            else {
                List<Veterinarian> filtered = service.searchVeterinarians(newV);
                veterinariansTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });
    }

    @FXML
    private void showAddVetForm() {
        clearForm();
        saveButton.setText("Add Veterinarian");
        animateForm(true);
    }

    @FXML
    private void saveVeterinarian() {
        if (!validateForm()) return;
        String fn = firstNameField.getText(), ln = lastNameField.getText(),
                spec = specializationComboBox.getValue(),
                ph = phoneField.getText(), em = emailField.getText(),
                sch = scheduleField.getText(), notes = notesArea.getText();
        boolean avail = availableCheckBox.isSelected();

        if (isEditing) {
            currentVet.setFirstName(fn); currentVet.setLastName(ln);
            currentVet.setSpecialization(spec); currentVet.setPhone(ph);
            currentVet.setEmail(em); currentVet.setSchedule(sch);
            currentVet.setAvailable(avail); currentVet.setNotes(notes);
            service.updateVeterinarian(currentVet);
            AlertUtil.showInformation("Success","Vet updated");
        } else {
            Veterinarian vet = new Veterinarian(0,fn,ln,spec,ph,em,sch,avail,notes);
            service.addVeterinarian(vet);
            AlertUtil.showInformation("Success","Vet added");
        }
        loadVeterinarians();
        animateForm(false);
    }

    @FXML
    private void deleteVeterinarian() {
        Veterinarian sel = veterinariansTable.getSelectionModel().getSelectedItem();
        if (sel==null) { AlertUtil.showWarning("No Select","Select one"); return; }
        if (AlertUtil.showConfirmation("Confirm","Delete vet?","")) {
            service.deleteVeterinarian(sel.getId());
            loadVeterinarians();
        }
    }

    @FXML
    private void cancelEdit() { animateForm(false); }

    private boolean validateForm() {
        if (firstNameField.getText().isEmpty()
                || lastNameField.getText().isEmpty()
                || specializationComboBox.getValue()==null
                || phoneField.getText().isEmpty()) {
            AlertUtil.showWarning("Validation","Please fill required");
            return false;
        }
        if (!emailField.getText().isEmpty() && !ValidationUtil.isValidEmail(emailField.getText())) {
            AlertUtil.showWarning("Validation","Invalid email");
            return false;
        }
        return true;
    }

    private void clearForm() {
        firstNameField.clear(); lastNameField.clear();
        specializationComboBox.setValue(null);
        phoneField.clear(); emailField.clear(); scheduleField.clear();
        availableCheckBox.setSelected(true); notesArea.clear();
        currentVet = null; isEditing = false;
    }

    private void animateForm(boolean show) {
        addVetForm.setVisible(show); addVetForm.setManaged(show);
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), addVetForm);
        tt.setFromY(show?50:0); tt.setToY(show?0:50); tt.play();
    }
}
