package com.petclinic.controller;

import com.petclinic.model.Owner;
import com.petclinic.model.Pet;
import com.petclinic.service.OwnerService;
import com.petclinic.service.PetService;
import com.petclinic.util.AlertUtil;
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
import java.util.List;

public class PetController {

    @FXML
    private TableView<Pet> petsTable;
    
    @FXML
    private TableColumn<Pet, String> nameColumn;
    
    @FXML
    private TableColumn<Pet, String> speciesColumn;
    
    @FXML
    private TableColumn<Pet, String> breedColumn;
    
    @FXML
    private TableColumn<Pet, LocalDate> birthDateColumn;
    
    @FXML
    private TableColumn<Pet, String> ownerNameColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private VBox addPetForm;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private ComboBox<String> speciesComboBox;
    
    @FXML
    private TextField breedField;
    
    @FXML
    private DatePicker birthDatePicker;
    
    @FXML
    private ComboBox<Owner> ownerComboBox;
    
    @FXML
    private TextArea notesArea;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private final PetService petService = new PetService();
    private final OwnerService ownerService = new OwnerService();
    
    private Pet currentPet;
    private boolean isEditing = false;

    @FXML
    public void initialize() {

        setupPetsTable();
        

        setupFormControls();
        

        loadPets();
        loadOwners();
        

        addPetForm.setVisible(false);
        addPetForm.setManaged(false);
        

        setupSearch();
    }
    
    private void setupPetsTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        speciesColumn.setCellValueFactory(new PropertyValueFactory<>("species"));
        breedColumn.setCellValueFactory(new PropertyValueFactory<>("breed"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        ownerNameColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        

        petsTable.setRowFactory(tv -> {
            TableRow<Pet> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editPet(row.getItem());
                }
            });
            return row;
        });
    }
    
    private void setupFormControls() {

        speciesComboBox.setItems(FXCollections.observableArrayList(
                "Dog", "Cat", "Bird", "Rabbit", "Hamster", "Guinea Pig", "Reptile", "Fish", "Other"
        ));
        

        ownerComboBox.setConverter(new StringConverter<Owner>() {
            @Override
            public String toString(Owner owner) {
                return owner != null ? owner.getFirstName() + " " + owner.getLastName() : "";
            }

            @Override
            public Owner fromString(String string) {
                return null; // Not needed for ComboBox
            }
        });
        

        ValidationUtil.addRequiredValidator(nameField);
        ValidationUtil.addRequiredValidator(speciesComboBox);
        ValidationUtil.addRequiredValidator(birthDatePicker);
        ValidationUtil.addRequiredValidator(ownerComboBox);
    }
    
    private void loadPets() {
        List<Pet> pets = petService.getAllPets();
        petsTable.setItems(FXCollections.observableArrayList(pets));
    }
    
    private void loadOwners() {
        List<Owner> owners = ownerService.getAllOwners();
        ownerComboBox.setItems(FXCollections.observableArrayList(owners));
    }
    
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadPets();
            } else {
                List<Pet> filteredPets = petService.searchPets(newValue);
                petsTable.setItems(FXCollections.observableArrayList(filteredPets));
            }
        });
    }
    
    @FXML
    private void showAddPetForm() {

        clearForm();

        showForm(false);
    }
    
    @FXML
    private void editPet(Pet pet) {
        this.currentPet = pet;
        this.isEditing = true;
        

        nameField.setText(pet.getName());
        speciesComboBox.setValue(pet.getSpecies());
        breedField.setText(pet.getBreed());
        birthDatePicker.setValue(pet.getBirthDate());
        

        for (Owner owner : ownerComboBox.getItems()) {
            if (owner.getId() == pet.getOwnerId()) {
                ownerComboBox.setValue(owner);
                break;
            }
        }
        
        notesArea.setText(pet.getNotes());
        

        showForm(true);
    }
    
    private void showForm(boolean isEdit) {

        saveButton.setText(isEdit ? "Update Pet" : "Add Pet");
        

        addPetForm.setVisible(true);
        addPetForm.setManaged(true);
        

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), addPetForm);
        slideIn.setFromY(50);
        slideIn.setToY(0);
        slideIn.play();
    }
    
    @FXML
    private void hideForm() {

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), addPetForm);
        slideOut.setFromY(0);
        slideOut.setToY(50);
        slideOut.setOnFinished(e -> {
            addPetForm.setVisible(false);
            addPetForm.setManaged(false);
        });
        slideOut.play();
    }
    
    @FXML
    private void savePet() {
        if (!validateForm()) {
            return;
        }
        
        try {
            String name = nameField.getText();
            String species = speciesComboBox.getValue();
            String breed = breedField.getText();
            LocalDate birthDate = birthDatePicker.getValue();
            Owner owner = ownerComboBox.getValue();
            String notes = notesArea.getText();
            
            if (isEditing && currentPet != null) {

                currentPet.setName(name);
                currentPet.setSpecies(species);
                currentPet.setBreed(breed);
                currentPet.setBirthDate(birthDate);
                currentPet.setOwnerId(owner.getId());
                currentPet.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
                currentPet.setNotes(notes);
                
                petService.updatePet(currentPet);
                AlertUtil.showInformation("Success", "Pet updated successfully");
            } else {

                Pet newPet = new Pet(
                    0,
                    name,
                    species,
                    breed,
                    birthDate,
                    owner.getId(),
                    owner.getFirstName() + " " + owner.getLastName(),
                    notes
                );
                
                petService.addPet(newPet);
                AlertUtil.showInformation("Success", "Pet added successfully");
            }
            

            loadPets();
            hideForm();
            
        } catch (Exception e) {
            AlertUtil.showError("Error", "Error saving pet: " + e.getMessage());
        }
    }
    
    @FXML
    private void deletePet() {
        Pet selectedPet = petsTable.getSelectionModel().getSelectedItem();
        if (selectedPet == null) {
            AlertUtil.showWarning("No Selection", "Please select a pet to delete");
            return;
        }
        
        boolean confirmed = AlertUtil.showConfirmation(
            "Confirm Delete", 
            "Are you sure you want to delete " + selectedPet.getName() + "?",
            "This action cannot be undone."
        );
        
        if (confirmed) {
            try {
                petService.deletePet(selectedPet.getId());
                loadPets();
                AlertUtil.showInformation("Success", "Pet deleted successfully");
            } catch (Exception e) {
                AlertUtil.showError("Error", "Error deleting pet: " + e.getMessage());
            }
        }
    }
    
    private boolean validateForm() {
        if (nameField.getText().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Pet name is required");
            return false;
        }
        
        if (speciesComboBox.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Species is required");
            return false;
        }
        
        if (birthDatePicker.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Birth date is required");
            return false;
        }
        
        if (ownerComboBox.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Owner is required");
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        nameField.clear();
        speciesComboBox.setValue(null);
        breedField.clear();
        birthDatePicker.setValue(null);
        ownerComboBox.setValue(null);
        notesArea.clear();
        
        currentPet = null;
        isEditing = false;
    }
    
    @FXML
    private void cancelEdit() {
        hideForm();
    }
}