package com.petclinic.controller;

import com.petclinic.model.Appointment;
import com.petclinic.model.Pet;
import com.petclinic.service.AppointmentService;
import com.petclinic.service.PetService;
import com.petclinic.service.VeterinarianService;
import com.petclinic.util.DateUtil;
import com.petclinic.util.NavigationUtil;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML
    private Label totalOwnersLabel;


    @FXML private Label lblTotalVets;
    private final VeterinarianService vetService = new VeterinarianService();


    @FXML
    private Label totalAppointmentsLabel;

    @FXML
    private Label totalPetsLabel;
    
    @FXML
    private Label todayAppointmentsLabel;
    
    @FXML
    private Label pendingAppointmentsLabel;
    
    @FXML
    private PieChart petTypesChart;
    
    @FXML
    private TableView<Appointment> upcomingAppointmentsTable;
    
    @FXML
    private TableColumn<Appointment, LocalDateTime> dateTimeColumn;
    
    @FXML
    private TableColumn<Appointment, String> petNameColumn;
    
    @FXML
    private TableColumn<Appointment, String> ownerNameColumn;
    
    @FXML
    private TableColumn<Appointment, String> reasonColumn;
    
    @FXML
    private VBox dashboardContainer;

    private final PetService petService = new PetService();
    private final AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {

        setupAnimations();
        

        setupUpcomingAppointmentsTable();
        

        loadStatistics();
        

        setupActionHandlers();
        int totalVets = vetService.getAllVeterinarians().size();
        lblTotalVets.setText(String.valueOf(totalVets));
    }
    
    private void setupAnimations() {

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), dashboardContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    private void setupUpcomingAppointmentsTable() {
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateTimeColumn.setCellFactory(column -> new DateUtil.DateTimeTableCell<>());
        
        petNameColumn.setCellValueFactory(new PropertyValueFactory<>("petName"));
        ownerNameColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        

        List<Appointment> appointments = appointmentService.getUpcomingAppointments(5);
        upcomingAppointmentsTable.setItems(FXCollections.observableArrayList(appointments));
    }
    
    private void loadStatistics() {

        int totalVets = vetService.getAllVeterinarians().size();
        lblTotalVets.setText(String.valueOf(totalVets));

        int totalPets = petService.getTotalPets();
        totalPetsLabel.setText(String.valueOf(totalPets));
        

        int todayAppointments = appointmentService.getAppointmentsForDate(LocalDate.now()).size();
        todayAppointmentsLabel.setText(String.valueOf(todayAppointments));
        

        int pendingAppointments = appointmentService.getPendingAppointments().size();
        pendingAppointmentsLabel.setText(String.valueOf(pendingAppointments));
        

        loadPetTypesChart();

    }
    
    private void loadPetTypesChart() {
        Map<String, Long> petTypeCount = petService.getAllPets().stream()
                .collect(Collectors.groupingBy(Pet::getSpecies, Collectors.counting()));
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        petTypeCount.forEach((type, count) -> 
            pieChartData.add(new PieChart.Data(type + " (" + count + ")", count))
        );
        
        petTypesChart.setData(pieChartData);
        petTypesChart.setTitle("Pet Types");
        petTypesChart.setLabelsVisible(true);
        petTypesChart.setAnimated(true);
    }
    
    private void setupActionHandlers() {

        upcomingAppointmentsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Appointment selectedAppointment = upcomingAppointmentsTable.getSelectionModel().getSelectedItem();
                if (selectedAppointment != null) {
                    NavigationUtil.loadAppointmentDetails(selectedAppointment.getId());
                }
            }
        });
    }
    
    @FXML
    private void viewAllAppointments() {
        NavigationUtil.loadAppointments();

    }
    
    @FXML
    private void addNewAppointment() {
        NavigationUtil.loadNewAppointment();

    }
}