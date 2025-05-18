package com.petclinic.controller;

import com.petclinic.util.FXMLLoaderUtil;
import com.petclinic.util.NavigationUtil;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainController {
    @FXML
    private BorderPane mainPane;
    @FXML
    private BorderPane mainBorderPane;
    
    @FXML
    private VBox sideMenu;
    
    @FXML
    private Button dashboardButton;
    
    @FXML
    private Button petsButton;
    
    @FXML
    private Button ownersButton;
    
    @FXML
    private Button appointmentsButton;
    
    @FXML
    private Button medicalRecordsButton;
    
    @FXML
    private Button reportsButton;

    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {

        NavigationUtil.setMainBorderPane(mainBorderPane);

        loadDashboard();


        setupMenuButtons();
    }

    private void setupMenuButtons() {

        for (javafx.scene.Node node : sideMenu.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                
                button.setOnMouseEntered(e -> {
                    if (!button.getStyleClass().contains("active-menu-button")) {
                        button.getStyleClass().add("hover-menu-button");
                    }
                });
                
                button.setOnMouseExited(e -> {
                    button.getStyleClass().remove("hover-menu-button");
                });
            }
        }
    }
    
    @FXML
    private void loadDashboard() {
        updateActiveButton(dashboardButton);
        titleLabel.setText("Dashboard");
        loadView("/fxml/DashboardView.fxml");
    }
    
    @FXML
    private void loadPets() {
        updateActiveButton(petsButton);
        titleLabel.setText("Pets");
        loadView("/fxml/PetView.fxml");
    }
    
    @FXML
    private void loadOwners() {
        updateActiveButton(ownersButton);
        titleLabel.setText("Owners");
        loadView("/fxml/OwnerView.fxml");
    }
    
    @FXML
    private void loadAppointments() {
        updateActiveButton(appointmentsButton);
        titleLabel.setText("Appointments");
        loadView("/fxml/AppointmentView.fxml");
    }
    
    @FXML
    private void loadMedicalRecords() {
        updateActiveButton(medicalRecordsButton);
        titleLabel.setText("Medical Records");
        loadView("/fxml/MedicalRecordView.fxml");
    }
    
    @FXML
    private void loadReports() {
        updateActiveButton(reportsButton);
        titleLabel.setText("Reports");
        loadView("/fxml/ReportsView.fxml");
    }
    @FXML
    private void loadVeterinarians() {
        titleLabel.setText("Veterinarian");
        loadView("/fxml/VeterinarianView.fxml");
    }
    private void updateActiveButton(Button activeButton) {
        // Remove active class from all buttons
        dashboardButton.getStyleClass().remove("active-menu-button");
        petsButton.getStyleClass().remove("active-menu-button");
        ownersButton.getStyleClass().remove("active-menu-button");
        appointmentsButton.getStyleClass().remove("active-menu-button");
        medicalRecordsButton.getStyleClass().remove("active-menu-button");
        reportsButton.getStyleClass().remove("active-menu-button");

        

        activeButton.getStyleClass().add("active-menu-button");
    }
    
    private void loadView(String fxmlPath) {
        try {
            Pane view = FXMLLoaderUtil.loadFXML(fxmlPath);
            

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), view);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            mainBorderPane.setCenter(view);
            fadeIn.play();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}