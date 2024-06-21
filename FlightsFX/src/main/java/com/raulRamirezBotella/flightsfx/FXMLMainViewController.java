package com.raulRamirezBotella.flightsfx;

import com.raulRamirezBotella.model.Flight;
import com.raulRamirezBotella.utils.FileUtils;
import com.raulRamirezBotella.utils.MessageUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class FXMLMainViewController {


    public TableColumn flightNumberColumn;
    public TableColumn destinationColumn;
    @FXML
    private TableColumn<Flight, String> departureColumn;
    @FXML
    private TableColumn<Flight, String> durationColumn;
    public Button chartButton;
    public Label lbFlightNumber;
    public Label lbDeparture;
    public Label lbDestination;
    public Label lbDuration;
    public Button addButton;
    public Button deleteButton;

    @FXML
    private TableView<Flight> tablaVuelos;

    @FXML
    private TextField txtFlightNumber;

    @FXML
    private TextField txtDeparture;

    @FXML
    private TextField txtDuration;

    @FXML
    private TextField txtDestination;

    @FXML
    private ChoiceBox<String> filterChoiceBox;

    // Método para inicializar la vista, configura la tabla y carga los vuelos.
    @FXML
    public void initialize() {
        ObservableList<Flight> flights = FXCollections.observableArrayList(FileUtils.loadFlights());
        tablaVuelos.setItems(flights);

        deleteButton.setDisable(true);
        tablaVuelos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> deleteButton.setDisable(newSelection == null));

        // Configuración de la columna de salida (departure) para manejar varios formatos de fecha y hora
        departureColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(cellData.getValue().getDeparture().format(formatter));
        });

        // Configuración de la columna de duración (duration) para manejar varios formatos de hora
        durationColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return new SimpleStringProperty(cellData.getValue().getDuration().format(formatter));
        });

        filterChoiceBox.setItems(FXCollections.observableArrayList(
                "Show all flights",
                "Show flights to currently selected city",
                "Show long flights",
                "Show next 5 flights",
                "Show flight duration average"
        ));
        filterChoiceBox.getSelectionModel().selectFirst();
        filterChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                applyFilter(newValue);
            }
        });

        tablaVuelos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> filterChoiceBox.getItems().set(1, newSelection != null ? "Show flights to currently selected city" : "Select a flight first"));
    }

    // Aplica el filtro seleccionado en la vista de la tabla.
    private void applyFilter(String filter) {
        if (filter != null) { // Comprobar que filter no sea nulo
            switch (filter) {
                case "Show all flights":
                    showAllFlights();
                    break;
                case "Show flights to currently selected city":
                    showFlightsToSelectedCity();
                    break;
                case "Show long flights":
                    showLongFlights();
                    break;
                case "Show next 5 flights":
                    showNext5Flights();
                    break;
                case "Show flight duration average":
                    showAverageFlightDuration();
                    break;
            }
        }
    }

    // Muestra todos los vuelos sin aplicar ningún filtro.
    private void showAllFlights() {
        tablaVuelos.getItems().setAll(FileUtils.loadFlights());
    }

    // Filtra y muestra vuelos que van hacia la ciudad seleccionada.
    private void showFlightsToSelectedCity() {
        Flight selectedFlight = tablaVuelos.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            String selectedCity = selectedFlight.getDestination();
            tablaVuelos.getItems().setAll(
                    FileUtils.loadFlights().stream()
                            .filter(flight -> flight.getDestination().equals(selectedCity))
                            .collect(Collectors.toList())
            );
        } else {
            MessageUtils.showError("Debe seleccionar un vuelo para aplicar este filtro.");
        }
    }

    // Filtra y muestra vuelos cuya duración es más larga que un umbral específico.
    private void showLongFlights() {
        tablaVuelos.getItems().setAll(
                FileUtils.loadFlights().stream()
                        .filter(flight -> flight.getDuration().isAfter(LocalTime.of(3, 0)))
                        .collect(Collectors.toList())
        );
    }

    // Muestra los próximos 5 vuelos a partir de la hora actual.
    private void showNext5Flights() {
        tablaVuelos.getItems().setAll(
                FileUtils.loadFlights().stream()
                        .filter(flight -> flight.getDeparture().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Flight::getDeparture))
                        .limit(5)
                        .collect(Collectors.toList())
        );
    }

    // Calcula y muestra la duración media de todos los vuelos.
    private void showAverageFlightDuration() {
        OptionalDouble average = FileUtils.loadFlights().stream()
                .mapToInt(flight -> flight.getDuration().toSecondOfDay())
                .average();

        if (average.isPresent()) {
            int avgSeconds = (int) average.getAsDouble();
            LocalTime avgTime = LocalTime.ofSecondOfDay(avgSeconds);
            MessageUtils.showMessage("Duración media de vuelos: " + avgTime.toString());
        } else {
            MessageUtils.showMessage("No hay vuelos para calcular la duración media.");
        }
    }

    // Agrega un nuevo vuelo a la tabla y al archivo de vuelos cuando se pulsa el botón "Add".
    @FXML
    private void addAction() {
        // Validar que ninguno de los campos esté vacío
        if (!txtFlightNumber.getText().isEmpty() &&
                !txtDestination.getText().isEmpty() &&
                !txtDeparture.getText().isEmpty() &&
                !txtDuration.getText().isEmpty()) {

            try {
                // Se usa el mismo DateTimeFormatter que en FileUtils para asegurar la consistencia
                LocalDateTime departure = LocalDateTime.parse(txtDeparture.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                LocalTime duration = LocalTime.parse(txtDuration.getText(), DateTimeFormatter.ofPattern("HH:mm"));

                // Añadir nuevo vuelo a la tabla
                Flight newFlight = new Flight(
                        txtFlightNumber.getText(),
                        txtDestination.getText(),
                        departure,
                        duration
                );
                tablaVuelos.getItems().add(newFlight);

                // Limpiar los campos después de añadir el vuelo
                txtFlightNumber.clear();
                txtDestination.clear();
                txtDeparture.clear();
                txtDuration.clear();

                // Guardar los vuelos actualizados
                FileUtils.saveFlights(new ArrayList<>(tablaVuelos.getItems()));
                MessageUtils.showMessage("Vuelo añadido y guardado con éxito.");

            } catch (DateTimeParseException e) {
                // Mostrar mensaje de error si el formato de fecha/hora o duración no es válido
                MessageUtils.showError("El formato de la fecha/hora de salida o la duración no es válido. Asegúrese de usar el formato dd/MM/yyyy HH:mm para la salida y HH:mm para la duración.");
            }
        } else {
            // Mostrar mensaje de error porque hay campos vacíos
            MessageUtils.showError("Todos los campos son obligatorios.");
        }
    }

    // Elimina el vuelo seleccionado de la tabla y del archivo de vuelos cuando se pulsa el botón "Delete".
    @FXML
    private void deleteAction() {
        // Obtener el vuelo seleccionado
        Flight selectedFlight = tablaVuelos.getSelectionModel().getSelectedItem();

        if (selectedFlight != null) {
            // Mostrar un diálogo de confirmación antes de eliminar
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de que desea eliminar el vuelo seleccionado?", ButtonType.YES, ButtonType.NO);
            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    // Si el usuario confirma, eliminar el vuelo
                    tablaVuelos.getItems().remove(selectedFlight);

                    // Guardar los vuelos actualizados
                    FileUtils.saveFlights(new ArrayList<>(tablaVuelos.getItems()));
                    MessageUtils.showMessage("Vuelo eliminado y guardado con éxito.");
                }
            });
        } else {
            // Mostrar un mensaje de error si no hay selección
            MessageUtils.showError("No hay ningún vuelo seleccionado para eliminar.");
        }
    }

    // Muestra un gráfico de los vuelos por destino cuando se pulsa el botón "PieChart".
    public void mostrarGrafico() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChartView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 620, 440);
            Stage stage = new Stage();
            stage.setTitle("Gráfico de Vuelos");
            stage.setScene(scene);

            ChartViewController chartController = fxmlLoader.getController();
            chartController.initData(tablaVuelos.getItems()); // Pasar los datos de los vuelos al nuevo controlador

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
